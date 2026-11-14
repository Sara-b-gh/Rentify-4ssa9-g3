package com.rentify.reservation.event;

/** Événement reçu du service Utilisateurs quand un compte est désactivé. */
public record UtilisateurDesactiveEvent(String utilisateurId) { }
