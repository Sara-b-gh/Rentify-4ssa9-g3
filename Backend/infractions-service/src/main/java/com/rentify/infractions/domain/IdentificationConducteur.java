package com.rentify.infractions.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité interne de l'agrégat Infraction (DDD) : n'existe pas sans son infraction.
 * Le contrat et le conducteur appartiennent au service Contrats : on ne garde que leurs identifiants.
 */
@Entity
@Table(name = "identifications_conducteur")
public class IdentificationConducteur {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID contratId;          // identifiant du contrat (service Contrats)

    @Column(nullable = false)
    private String conducteurId;     // identifiant du conducteur (userId Keycloak)

    @Column(nullable = false)
    private LocalDateTime dateIdentification;

    protected IdentificationConducteur() { }   // requis par JPA

    public IdentificationConducteur(UUID contratId, String conducteurId) {
        this.contratId = contratId;
        this.conducteurId = conducteurId;
        this.dateIdentification = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getContratId() { return contratId; }
    public String getConducteurId() { return conducteurId; }
    public LocalDateTime getDateIdentification() { return dateIdentification; }
}
