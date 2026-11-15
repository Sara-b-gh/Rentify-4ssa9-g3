package com.rentify.contrats.service;

import com.rentify.contrats.client.ReservationClient;
import com.rentify.contrats.client.UtilisateursClient;
import com.rentify.contrats.domain.Conducteur;
import com.rentify.contrats.domain.Contrat;
import com.rentify.contrats.domain.EtatDesLieux;
import com.rentify.contrats.domain.StatutContrat;
import com.rentify.contrats.domain.TypeEtat;
import com.rentify.contrats.dto.ConducteurRequest;
import com.rentify.contrats.dto.EtatDesLieuxRequest;
import com.rentify.contrats.dto.OuvrirContratRequest;
import com.rentify.contrats.dto.ReservationDto;
import com.rentify.contrats.dto.UtilisateurDto;
import com.rentify.contrats.event.ContratCloture;
import com.rentify.contrats.event.ContratEventPublisher;
import com.rentify.contrats.event.ContratOuvert;
import com.rentify.contrats.exception.ContratNotFoundException;
import com.rentify.contrats.exception.ServiceIndisponibleException;
import com.rentify.contrats.repository.ContratRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContratService {

    private final ContratRepository repository;
    private final ReservationClient reservationClient;
    private final UtilisateursClient utilisateursClient;
    private final ContratEventPublisher eventPublisher;

    public ContratService(ContratRepository repository,
                          ReservationClient reservationClient,
                          UtilisateursClient utilisateursClient,
                          ContratEventPublisher eventPublisher) {
        this.repository = repository;
        this.reservationClient = reservationClient;
        this.utilisateursClient = utilisateursClient;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Ouvrir un contrat à partir d'une réservation.
     * 404 : réservation inconnue | 409 : réservation non confirmée ou véhicule déjà en contrat
     * 503 : service Réservation indisponible.
     */
    @Transactional
    public Contrat ouvrir(OuvrirContratRequest request) {
        // 1. Appel SYNCHRONE au service Réservation
        ReservationDto reservation = lireReservation(request.reservationId());

        // 2. Règles métier
        if (!"CONFIRMEE".equals(reservation.statut())) {
            throw new IllegalStateException(
                    "La réservation " + reservation.id() + " n'est pas confirmée (statut : " + reservation.statut() + ")");
        }
        if (repository.existsByVehiculeIdAndStatut(reservation.vehiculeId(), StatutContrat.OUVERT)) {
            throw new IllegalStateException(
                    "Le véhicule " + reservation.vehiculeId() + " a déjà un contrat ouvert");
        }

        // 3. Création du contrat avec l'état des lieux de DÉPART
        Contrat contrat = new Contrat();
        contrat.setReservationId(reservation.id());
        contrat.setVehiculeId(reservation.vehiculeId());
        contrat.setClientId(reservation.clientId());
        contrat.setDateDebut(reservation.dateDebut());
        contrat.setDateFinPrevue(reservation.dateFin());
        contrat.ouvrir(versEtat(request.etatDepart()));
        Contrat sauvegarde = repository.save(contrat);

        // 4. Événement ASYNCHRONE : Flotte passera le véhicule « en location »
        eventPublisher.publierOuvert(new ContratOuvert(
                sauvegarde.getId(), sauvegarde.getVehiculeId(),
                sauvegarde.getDateDebut(), sauvegarde.getDateFinPrevue()));
        return sauvegarde;
    }

    @Transactional(readOnly = true)
    public Contrat get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ContratNotFoundException("Contrat introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Contrat> parClient(String clientId) {
        return repository.findByClientId(clientId);
    }

    /** Utilisé par le service Infractions : contrat actif pour un véhicule à une date/heure. */
    @Transactional(readOnly = true)
    public Contrat actif(Long vehiculeId, LocalDateTime dateHeure) {
        return repository.findActif(vehiculeId, dateHeure)
                .orElseThrow(() -> new ContratNotFoundException(
                        "Aucun contrat actif pour le véhicule " + vehiculeId + " à " + dateHeure));
    }

    /**
     * Ajouter un conducteur : 404 contrat inconnu | 400 permis invalide / utilisateur inconnu
     * 409 contrat clôturé | 503 service Utilisateurs indisponible.
     */
    @Transactional
    public Contrat ajouterConducteur(Long contratId, ConducteurRequest request) {
        Contrat contrat = get(contratId);
        if (contrat.getStatut() == StatutContrat.CLOTURE) {
            throw new IllegalStateException("Le contrat " + contratId + " est clôturé");
        }

        // Appel SYNCHRONE au service Utilisateurs : le permis est-il valide ?
        UtilisateurDto utilisateur = lireUtilisateur(request.userId());
        if (!utilisateur.actif() || !utilisateur.permisValide()) {
            throw new IllegalArgumentException(
                    "Le permis de l'utilisateur " + request.userId() + " n'est pas valide");
        }

        Conducteur conducteur = new Conducteur();
        conducteur.setUserId(request.userId());
        conducteur.setNumeroPermis(request.numeroPermis());
        conducteur.setPrincipal(contrat.getConducteurs().isEmpty()); // le premier est le conducteur principal
        contrat.ajouterConducteur(conducteur);
        return repository.save(contrat);
    }

    /** Clôturer : 404 contrat inconnu | 409 déjà clôturé | 400 kilométrage de retour incohérent. */
    @Transactional
    public Contrat cloturer(Long contratId, EtatDesLieuxRequest etatRetour) {
        Contrat contrat = get(contratId);
        int kmDepart = contrat.getEtatsDesLieux().stream()
                .filter(e -> e.getType() == TypeEtat.DEPART)
                .mapToInt(EtatDesLieux::getKilometrage)
                .findFirst().orElse(0);
        if (contrat.getStatut() == StatutContrat.CLOTURE) {
            throw new IllegalStateException("Le contrat " + contratId + " est déjà clôturé");
        }
        if (etatRetour.kilometrage() < kmDepart) {
            throw new IllegalArgumentException(
                    "Le kilométrage de retour (" + etatRetour.kilometrage()
                            + ") ne peut pas être inférieur au départ (" + kmDepart + ")");
        }

        contrat.cloturer(versEtat(etatRetour), LocalDateTime.now());
        Contrat sauvegarde = repository.save(contrat);

        // Événement ASYNCHRONE : Flotte (véhicule disponible), Maintenance (kilométrage), Facturation
        eventPublisher.publierCloture(new ContratCloture(
                sauvegarde.getId(), sauvegarde.getVehiculeId(), sauvegarde.getDateDebut(),
                sauvegarde.getDateRetourReelle(), kmDepart, etatRetour.kilometrage()));
        return sauvegarde;
    }

    // ---------- méthodes privées ----------

    private EtatDesLieux versEtat(EtatDesLieuxRequest r) {
        EtatDesLieux etat = new EtatDesLieux();
        etat.setKilometrage(r.kilometrage());
        etat.setCarburant(r.carburant());
        etat.setDommages(r.dommages());
        return etat;
    }

    private ReservationDto lireReservation(Long id) {
        try {
            return reservationClient.getReservation(id);
        } catch (FeignException.NotFound e) {
            throw new ContratNotFoundException("Réservation introuvable : " + id);
        } catch (FeignException e) {
            throw new ServiceIndisponibleException("Service Réservation indisponible, réessayez plus tard");
        }
    }

    private UtilisateurDto lireUtilisateur(String id) {
        try {
            return utilisateursClient.getUtilisateur(id);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Utilisateur introuvable : " + id);
        } catch (FeignException e) {
            throw new ServiceIndisponibleException("Service Utilisateurs indisponible, réessayez plus tard");
        }
    }
}
