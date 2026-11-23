package com.rentify.facturation.domain;

import jakarta.persistence.*;
import java.time.*;

/** Facture d'un contrat de location (montantTotal = location + pénalités). */
@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long contratId;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Double montantTotal;

    @Column
    private LocalDate dateEmission = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column
    private StatutFacture statut = StatutFacture.EMISE;

    public Facture() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getContratId() { return contratId; }
    public void setContratId(Long contratId) { this.contratId = contratId; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }
    public LocalDate getDateEmission() { return dateEmission; }
    public void setDateEmission(LocalDate dateEmission) { this.dateEmission = dateEmission; }
    public StatutFacture getStatut() { return statut; }
    public void setStatut(StatutFacture statut) { this.statut = statut; }
}
