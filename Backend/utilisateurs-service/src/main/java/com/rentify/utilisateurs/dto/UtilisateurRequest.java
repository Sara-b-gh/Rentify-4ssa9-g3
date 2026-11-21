package com.rentify.utilisateurs.dto;

import com.rentify.utilisateurs.domain.Role;
import jakarta.validation.constraints.*;

/**
 * Création / modification d'un utilisateur.
 * Le mot de passe n'est JAMAIS enregistré dans notre base : il est transmis à Keycloak.
 */
public record UtilisateurRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        @NotBlank @Email String email,
        String telephone,
        @NotNull Role role,
        @Size(min = 6, message = "6 caractères minimum") String motDePasse) {
}
