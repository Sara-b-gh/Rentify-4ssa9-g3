package com.rentify.infractions.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité racine de l'agrégat Infraction (DDD).
 * Le véhicule appartient au service Flotte : on ne garde que son identifiant (vehiculeId).
 * L'identification du conducteur fait partie de l'agrégat (composition 1 -> 0..1).
 */
@Entity
@Table(name = "infractions")
public class Infraction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String immatriculation;   // plaque lue sur l'avis de contravention

    @Column(nullable = false)
    private UUID vehiculeId;          // identifiant du véhicule (service Flotte)

    @Column(nullable = false)
    private LocalDateTime dateHeure;  // date et heure de l'infraction

    @Column(nullable = false)
    private String lieu;

    @Column(nullable = false)
    private double montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInfraction statut;

    /** Composition : supprimée avec l'infraction. Vide tant que le conducteur n'est pas identifié. */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "identification_id")
    private IdentificationConducteur identification;

    protected Infraction() { }   // requis par JPA

    public Infraction(String immatriculation, UUID vehiculeId, LocalDateTime dateHeure,
                      String lieu, double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant de l'amende doit être positif");
        }
        this.immatriculation = immatriculation;
        this.vehiculeId = vehiculeId;
        this.dateHeure = dateHeure;
        this.lieu = lieu;
        this.montant = montant;
        this.statut = StatutInfraction.DECLAREE;
    }

    /** Rattache le conducteur trouvé grâce au contrat actif à la date de l'infraction. */
    public void identifierConducteur(UUID contratId, String conducteurId) {
        if (statut == StatutInfraction.CONDUCTEUR_IDENTIFIE) {
            throw new IllegalStateException("Le conducteur est déjà identifié");
        }
        this.identification = new IdentificationConducteur(contratId, conducteurId);
        this.statut = StatutInfraction.CONDUCTEUR_IDENTIFIE;
    }

    /** Aucun contrat actif à cette date : l'amende reste à la charge de l'agence. */
    public void marquerNonImputable() {
        this.statut = StatutInfraction.NON_IMPUTABLE;
    }

    public UUID getId() { return id; }
    public String getImmatriculation() { return immatriculation; }
    public UUID getVehiculeId() { return vehiculeId; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public String getLieu() { return lieu; }
    public double getMontant() { return montant; }
    public StatutInfraction getStatut() { return statut; }
    public IdentificationConducteur getIdentification() { return identification; }
}
