package com.rentify.contrats.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/** Personne autorisée à conduire pendant le contrat. userId = identifiant Keycloak (lien par identifiant). */
@Entity
@Table(name = "conducteur")
@Getter
@Setter
@NoArgsConstructor
public class Conducteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    /** Copie figée du numéro de permis au moment du contrat. */
    @Column(nullable = false)
    private String numeroPermis;

    private boolean principal;
}
