package com.rentify.facturation.domain;

import jakarta.persistence.*;
import java.time.*;

/** Pénalité (issue d'une infraction) ajoutée au montant d'une facture. */
@Entity
@Table(name = "penalites")
public class Penalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long factureId;

    @Column(nullable = false, unique = true)
    private Long infractionId;

    @Column(nullable = false)
    private Double montant;

    public Penalite() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFactureId() { return factureId; }
    public void setFactureId(Long factureId) { this.factureId = factureId; }
    public Long getInfractionId() { return infractionId; }
    public void setInfractionId(Long infractionId) { this.infractionId = infractionId; }
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
}
