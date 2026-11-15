package com.rentify.facturation.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "paiements")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private double montant;

    @Column(nullable = false)
    private String mode;

    @Column(nullable = false)
    private LocalDateTime datePaiement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id")
    private Facture facture;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }

    public Facture getFacture() { return facture; }
    public void setFacture(Facture facture) { this.facture = facture; }
}