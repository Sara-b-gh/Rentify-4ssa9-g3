package com.rentify.facturation.domain;

import jakarta.persistence.*;
import java.time.*;

/** Paiement partiel ou total d'une facture. */
@Entity
@Table(name = "paiements")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long factureId;

    @Column(nullable = false)
    private Double montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModePaiement mode;

    @Column
    private LocalDate datePaiement = LocalDate.now();

    public Paiement() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFactureId() { return factureId; }
    public void setFactureId(Long factureId) { this.factureId = factureId; }
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
    public ModePaiement getMode() { return mode; }
    public void setMode(ModePaiement mode) { this.mode = mode; }
    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }
}
