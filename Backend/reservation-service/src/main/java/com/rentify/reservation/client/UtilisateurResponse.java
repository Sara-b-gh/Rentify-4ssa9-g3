package com.rentify.reservation.client;

/** Utilisateur lu dans le service Utilisateurs (seuls les champs utiles sont lus). */
public record UtilisateurResponse(Long id, String role, Boolean actif) {
}
