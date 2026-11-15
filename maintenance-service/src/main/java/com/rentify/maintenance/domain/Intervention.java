package com.rentify.maintenance.domain;

import java.util.Date;
import java.util.UUID;

public class Intervention {
    private UUID id;
    private UUID vehiculeId;
    private String type;
    private Date dateDebut;
    private Date dateFin;
    private double cout;
    private StatutIntervention statut;

    public void planifier() {
        this.statut = StatutIntervention.PLANIFIEE;
    }

    public void terminer(double cout) {
        this.cout = cout;
        this.statut = StatutIntervention.TERMINEE;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(UUID vehiculeId) { this.vehiculeId = vehiculeId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public double getCout() { return cout; }
    public void setCout(double cout) { this.cout = cout; }
    public StatutIntervention getStatut() { return statut; }
    public void setStatut(StatutIntervention statut) { this.statut = statut; }
}
