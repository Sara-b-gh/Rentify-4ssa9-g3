package com.rentify.reservation.service;

import com.rentify.reservation.client.DisponibiliteResponse;
import com.rentify.reservation.client.FlotteClient;
import com.rentify.reservation.domain.Reservation;
import com.rentify.reservation.domain.StatutReservation;
import com.rentify.reservation.dto.ModifierDatesRequest;
import com.rentify.reservation.dto.ReservationRequest;
import com.rentify.reservation.event.ReservationEventPublisher;
import com.rentify.reservation.exception.ConflitReservationException;
import com.rentify.reservation.exception.ReservationIntrouvableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/** Cas d'usage du contexte Réservation. */
@Service
public class ReservationService {

    /** Identifiant "vide" utilisé quand il n'y a aucune réservation à exclure du contrôle. */
    private static final UUID AUCUN = new UUID(0L, 0L);

    private final com.rentify.reservation.repository.ReservationRepository repository;
    private final FlotteClient flotteClient;
    private final ReservationEventPublisher publisher;

    public ReservationService(com.rentify.reservation.repository.ReservationRepository repository,
                              FlotteClient flotteClient,
                              ReservationEventPublisher publisher) {
        this.repository = repository;
        this.flotteClient = flotteClient;
        this.publisher = publisher;
    }

    /** POST : crée et confirme une réservation (201 / 400 / 409). */
    @Transactional
    public Reservation creer(ReservationRequest req) {
        Reservation.verifierDates(req.dateDebut(), req.dateFin());          // 400
        double prix = verifierDisponibiliteEtPrix(req.vehiculeId(), req.dateDebut(), req.dateFin(), AUCUN);

        Reservation r = new Reservation(req.clientId(), req.vehiculeId(),
                req.dateDebut(), req.dateFin(), calculerMontant(prix, req.dateDebut(), req.dateFin()));
        r.confirmer();
        Reservation saved = repository.save(r);
        publisher.reservationConfirmee(saved);
        return saved;
    }

    /** GET : lecture (200 / 404). */
    @Transactional(readOnly = true)
    public Reservation trouver(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ReservationIntrouvableException(id));
    }

    @Transactional(readOnly = true)
    public List<Reservation> lister() {
        return repository.findAll();
    }

    /** PUT : modifie les dates (200 / 400 / 409). */
    @Transactional
    public Reservation modifierDates(UUID id, ModifierDatesRequest req) {
        Reservation r = trouver(id);
        Reservation.verifierDates(req.dateDebut(), req.dateFin());
        double prix = verifierDisponibiliteEtPrix(r.getVehiculeId(), req.dateDebut(), req.dateFin(), r.getId());
        r.modifierDates(req.dateDebut(), req.dateFin(), calculerMontant(prix, req.dateDebut(), req.dateFin()));
        Reservation saved = repository.save(r);
        publisher.reservationConfirmee(saved);   // Flotte met à jour son calendrier d'indisponibilité
        return saved;
    }

    /** DELETE : annule (204 / 404 / 409). */
    @Transactional
    public void annuler(UUID id) {
        Reservation r = trouver(id);
        r.annuler();
        publisher.reservationAnnulee(repository.save(r));
    }

    /** Réaction à UtilisateurDesactive : annule toutes les réservations futures du client. */
    @Transactional
    public void annulerReservationsFutures(String clientId) {
        List<Reservation> futures = repository.findByClientIdAndStatutAndDateDebutAfter(
                clientId, StatutReservation.CONFIRMEE, LocalDate.now());
        for (Reservation r : futures) {
            r.annuler();
            publisher.reservationAnnulee(repository.save(r));
        }
    }

    // ---------- méthodes privées ----------

    /**
     * 1) conflit avec une autre réservation locale => 409
     * 2) appel Flotte (timeout/indisponible => exception 503, rien n'est enregistré)
     * 3) véhicule indisponible selon Flotte => 409
     * Retourne le prix journalier.
     */
    private double verifierDisponibiliteEtPrix(UUID vehiculeId, LocalDate debut, LocalDate fin, UUID exclu) {
        if (repository.existeChevauchement(vehiculeId, StatutReservation.CONFIRMEE, debut, fin, exclu)) {
            throw new ConflitReservationException("Le véhicule est déjà réservé sur cette période");
        }
        DisponibiliteResponse dispo = flotteClient.verifierDisponibilite(vehiculeId, debut, fin);
        if (dispo == null || !dispo.disponible()) {
            throw new ConflitReservationException("Le véhicule n'est pas disponible sur cette période");
        }
        return dispo.prixJournalier();
    }

    /** Montant estimé = nombre de jours x prix journalier. */
    private double calculerMontant(double prixJournalier, LocalDate debut, LocalDate fin) {
        return ChronoUnit.DAYS.between(debut, fin) * prixJournalier;
    }
}
