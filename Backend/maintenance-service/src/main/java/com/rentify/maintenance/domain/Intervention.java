package com.rentify.maintenance.domain;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "interventions")
public class Intervention {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID vehiculeId;
    private String type;
    private Date dateDebut;
    private Date dateFin;
    private double cout;
    private String statut; // PROGRAMMEE, TERMINEE

    public void planifier() {
        this.statut = "PROGRAMMEE";
        this.dateDebut = new Date();
    }

    public void terminer(double cout) {
        this.cout = cout;
        this.statut = "TERMINEE";
        this.dateFin = new Date();
    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(UUID vehiculeId) { this.vehiculeId = vehiculeId; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
