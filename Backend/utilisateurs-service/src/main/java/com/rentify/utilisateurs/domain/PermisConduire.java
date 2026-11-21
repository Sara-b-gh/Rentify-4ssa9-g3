package com.rentify.utilisateurs.domain;

import jakarta.persistence.*;
import java.time.*;

/** Permis de conduire d'un utilisateur (0 ou 1 par utilisateur). */
@Entity
@Table(name = "permis_conduire")
public class PermisConduire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long utilisateurId;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private LocalDate dateObtention;

    @Column(nullable = false)
    private LocalDate dateExpiration;

    public PermisConduire() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getDateObtention() { return dateObtention; }
    public void setDateObtention(LocalDate dateObtention) { this.dateObtention = dateObtention; }
    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    /** Le permis est valide tant que sa date d'expiration n'est pas dépassée (apparaît dans le JSON : "valide"). */
    public boolean isValide() {
        return dateExpiration != null && dateExpiration.isAfter(LocalDate.now());
    }
}
