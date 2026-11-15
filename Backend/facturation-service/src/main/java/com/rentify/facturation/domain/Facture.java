package com.rentify.facturation.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID contratId;

    // Montant de la location (tarif obtenu auprès du service Flotte)
    @Column(nullable = false)
    private double montantLocation;

    @Column(nullable = false)
    private double montantTotal;

    @Column(nullable = false)
    private LocalDate dateEmission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFacture statut = StatutFacture.EMISE;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paiement> paiements = new ArrayList<>();

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Penalite> penalites = new ArrayList<>();

    // Total = montant de la location + somme des pénalités
    public double calculerTotal() {
        double totalPenalites = penalites.stream().mapToDouble(Penalite::getMontant).sum();
        return montantLocation + totalPenalites;
    }

    public double montantPaye() {
        return paiements.stream().mapToDouble(Paiement::getMontant).sum();
    }

    public void ajouterPaiement(Paiement paiement) {
        paiement.setFacture(this);
        paiements.add(paiement);
    }

    public void ajouterPenalite(Penalite penalite) {
        penalite.setFacture(this);
        penalites.add(penalite);
        this.montantTotal = calculerTotal();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getContratId() { return contratId; }
    public void setContratId(UUID contratId) { this.contratId = contratId; }

    public double getMontantLocation() { return montantLocation; }
    public void setMontantLocation(double montantLocation) { this.montantLocation = montantLocation; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }

    public LocalDate getDateEmission() { return dateEmission; }
    public void setDateEmission(LocalDate dateEmission) { this.dateEmission = dateEmission; }

    public StatutFacture getStatut() { return statut; }
    public void setStatut(StatutFacture statut) { this.statut = statut; }

    public List<Paiement> getPaiements() { return paiements; }
    public void setPaiements(List<Paiement> paiements) { this.paiements = paiements; }

    public List<Penalite> getPenalites() { return penalites; }
    public void setPenalites(List<Penalite> penalites) { this.penalites = penalites; }
}