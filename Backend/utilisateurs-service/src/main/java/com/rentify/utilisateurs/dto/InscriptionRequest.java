package com.rentify.utilisateurs.dto;

import jakarta.validation.constraints.*;

/** Inscription libre d'un client (endpoint public) : le rôle est forcé à CLIENT. */
public record InscriptionRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        @NotBlank @Email String email,
        String telephone,
        @NotBlank @Size(min = 6, message = "6 caractères minimum") String motDePasse) {
}
