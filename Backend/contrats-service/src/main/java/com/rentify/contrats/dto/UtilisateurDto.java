package com.rentify.contrats.dto;

/** Réponse du service Utilisateurs. À ALIGNER avec le DTO réel de l'équipe (microservice commun). */
public record UtilisateurDto(
        String id,
        String nom,
        String prenom,
        boolean actif,
        boolean permisValide) {
}
