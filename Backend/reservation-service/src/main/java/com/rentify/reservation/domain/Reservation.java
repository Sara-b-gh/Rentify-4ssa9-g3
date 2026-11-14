package com.rentify.reservation.domain;

import com.rentify.reservation.exception.ConflitReservationException;
import com.rentify.reservation.exception.ReservationInvalideException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entité racine de l'agrégat Réservation (DDD).
 * Les règles métier (confirmer, modifier les dates, annuler) sont dans l'entité elle-même.
 * Le client et le véhicule ne sont référencés que par leur identifiant (autres microservices).
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String clientId;      // identifiant Keycloak/Utilisateurs

    @Column(nullable = false)
    private UUID vehiculeId;      // identifiant du véhicule dans Flotte

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Column(nullable = false)
    private double montantEstime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut;

    protected Reservation() { }   // requis par JPA

    public Reservation(String clientId, UUID vehiculeId, LocalDate dateDebut,
                       LocalDate dateFin, double montantEstime) {
        verifierDates(dateDebut, dateFin);
        this.clientId = clientId;
        this.vehiculeId = vehiculeId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montantEstime = montantEstime;
    }

    /** Règle : début avant fin, et pas de début dans le passé. */
    public static void verifierDates(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null) {
            throw new ReservationInvalideException("Les dates de début et de fin sont obligatoires");
        }
        if (!fin.isAfter(debut)) {
            throw new ReservationInvalideException("La date de fin doit être après la date de début");
        }
        if (debut.isBefore(LocalDate.now())) {
            throw new ReservationInvalideException("La date de début ne peut pas être dans le passé");
        }
    }

    /** Confirme la réservation (appelée après vérification de la disponibilité). */
    public void confirmer() {
        this.statut = StatutReservation.CONFIRMEE;
    }

    /** Modifie la période ; impossible si la réservation n'est plus confirmée. */
    public void modifierDates(LocalDate debut, LocalDate fin, double nouveauMontant) {
        if (statut != StatutReservation.CONFIRMEE) {
            throw new ConflitReservationException("Seule une réservation confirmée peut être modifiée");
        }
        verifierDates(debut, fin);
        this.dateDebut = debut;
        this.dateFin = fin;
        this.montantEstime = nouveauMontant;
    }

    /** Annule la réservation ; impossible si elle est déjà annulée ou terminée. */
    public void annuler() {
        if (statut != StatutReservation.CONFIRMEE) {
            throw new ConflitReservationException("La réservation ne peut plus être annulée (statut " + statut + ")");
        }
        this.statut = StatutReservation.ANNULEE;
    }

    public UUID getId() { return id; }
    public String getClientId() { return clientId; }
    public UUID getVehiculeId() { return vehiculeId; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public double getMontantEstime() { return montantEstime; }
    public StatutReservation getStatut() { return statut; }
}
