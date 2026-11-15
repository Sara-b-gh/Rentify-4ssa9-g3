package com.rentify.infractions.service;

import com.rentify.infractions.client.ContratsClient;
import com.rentify.infractions.client.FlotteClient;
import com.rentify.infractions.domain.IdentificationConducteur;
import com.rentify.infractions.domain.Infraction;
import com.rentify.infractions.domain.StatutInfraction;
import com.rentify.infractions.dto.ContratActifDto;
import com.rentify.infractions.dto.CorrigerConducteurRequest;
import com.rentify.infractions.dto.DeclarerInfractionRequest;
import com.rentify.infractions.dto.VehiculeDto;
import com.rentify.infractions.event.ConducteurIdentifie;
import com.rentify.infractions.event.InfractionEventPublisher;
import com.rentify.infractions.exception.AucunContratActifException;
import com.rentify.infractions.exception.InfractionIntrouvableException;
import com.rentify.infractions.exception.ServiceIndisponibleException;
import com.rentify.infractions.repository.InfractionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class InfractionService {

    private static final Logger log = LoggerFactory.getLogger(InfractionService.class);

    private final InfractionRepository repository;
    private final FlotteClient flotteClient;
    private final ContratsClient contratsClient;
    private final InfractionEventPublisher eventPublisher;

    public InfractionService(InfractionRepository repository,
                             FlotteClient flotteClient,
                             ContratsClient contratsClient,
                             InfractionEventPublisher eventPublisher) {
        this.repository = repository;
        this.flotteClient = flotteClient;
        this.contratsClient = contratsClient;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Déclarer une infraction et identifier le conducteur.
     * 404 : plaque inconnue de Flotte | 503 : Flotte indisponible.
     * Si Contrats est indisponible, l'infraction est QUAND MÊME enregistrée (statut DECLAREE) : on relancera.
     */
    @Transactional
    public Infraction declarer(DeclarerInfractionRequest request) {
        // 1. Appel SYNCHRONE à Flotte : plaque -> vehiculeId
        VehiculeDto vehicule = flotteClient.parImmatriculation(request.immatriculation());

        // 2. Enregistrement de l'infraction (statut DECLAREE)
        Infraction infraction = repository.save(new Infraction(
                request.immatriculation(), vehicule.id(), request.dateHeure(), request.lieu(), request.montant()));

        // 3. Appel SYNCHRONE à Contrats : qui conduisait ? (sans faire échouer la déclaration si Contrats est en panne)
        tenterIdentification(infraction, false);
        return infraction;
    }

    /** Relancer l'identification (après une panne de Contrats) : 404, 409 (déjà identifié), 503. */
    @Transactional
    public Infraction relancerIdentification(UUID id) {
        Infraction infraction = get(id);
        if (infraction.getStatut() == StatutInfraction.CONDUCTEUR_IDENTIFIE) {
            throw new IllegalStateException("Le conducteur de l'infraction " + id + " est déjà identifié");
        }
        tenterIdentification(infraction, true);
        return infraction;
    }

    /** Correction manuelle du conducteur par l'agent : 200, 400, 404. */
    @Transactional
    public Infraction corrigerConducteur(UUID id, CorrigerConducteurRequest request) {
        Infraction infraction = get(id);
        boolean dejaIdentifie = infraction.getStatut() == StatutInfraction.CONDUCTEUR_IDENTIFIE;
        infraction.corrigerConducteur(request.contratId(), request.conducteurId());
        Infraction sauvegarde = repository.save(infraction);
        // Si aucune pénalité n'avait encore été demandée, on prévient Facturation
        if (!dejaIdentifie) {
            publier(sauvegarde);
        }
        return sauvegarde;
    }

    @Transactional(readOnly = true)
    public Infraction get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new InfractionIntrouvableException("Infraction introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Infraction> lister(StatutInfraction statut) {
        return statut == null ? repository.findAll() : repository.findByStatut(statut);
    }

    /** Conducteur identifié : 404 si l'infraction est inconnue ou pas encore imputée. */
    @Transactional(readOnly = true)
    public IdentificationConducteur conducteur(UUID id) {
        Infraction infraction = get(id);
        if (infraction.getIdentification() == null) {
            throw new InfractionIntrouvableException(
                    "Aucun conducteur identifié pour l'infraction " + id + " (statut : " + infraction.getStatut() + ")");
        }
        return infraction.getIdentification();
    }

    /**
     * Demande à Contrats le contrat actif à la date de l'infraction.
     * Contrat trouvé -> CONDUCTEUR_IDENTIFIE + événement | aucun contrat -> NON_IMPUTABLE
     * Contrats en panne -> reste DECLAREE (erreur 503 seulement si l'agent a demandé une relance).
     */
    private void tenterIdentification(Infraction infraction, boolean signalerPanne) {
        try {
            ContratActifDto contrat = contratsClient.contratActif(infraction.getVehiculeId(), infraction.getDateHeure());
            infraction.identifierConducteur(contrat.id(), contrat.conducteurResponsable());
            repository.save(infraction);
            publier(infraction);
        } catch (AucunContratActifException e) {
            infraction.marquerNonImputable();
            repository.save(infraction);
            log.info("Infraction {} non imputable : {}", infraction.getId(), e.getMessage());
        } catch (ServiceIndisponibleException e) {
            log.warn("Infraction {} reste DECLAREE : {}", infraction.getId(), e.getMessage());
            if (signalerPanne) {
                throw e;
            }
        }
    }

    /** Événement ASYNCHRONE : Facturation créera la pénalité. */
    private void publier(Infraction infraction) {
        IdentificationConducteur identification = infraction.getIdentification();
        eventPublisher.publierConducteurIdentifie(new ConducteurIdentifie(
                infraction.getId(), identification.getContratId(),
                identification.getConducteurId(), infraction.getMontant()));
    }
}
