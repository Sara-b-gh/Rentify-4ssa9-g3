package com.rentify.utilisateurs.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    @Id
    @GeneratedValue
    private UUID id;

    private String keycloakId;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role;
    private boolean actif = true;

    @OneToOne(cascade = CascadeType.ALL)
    private PermisConduire permis;

    public void changerRole(String nouveauRole) {
        this.role = nouveauRole;
    }

    public void desactiver() {
        this.actif = false;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
