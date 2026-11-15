package com.rentify.facturation.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "penalites")
public class Penalite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID infractionId;

    @Column(nullable = false)
    private String conducteurId;

    @Column(nullable = false)
    private double montant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id")
    private Facture facture;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getInfractionId() { return infractionId; }
    public void setInfractionId(UUID infractionId) { this.infractionId = infractionId; }

    public String getConducteurId() { return conducteurId; }
    public void setConducteurId(String conducteurId) { this.conducteurId = conducteurId; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public Facture getFacture() { return facture; }
    public void setFacture(Facture facture) { this.facture = facture; }
}