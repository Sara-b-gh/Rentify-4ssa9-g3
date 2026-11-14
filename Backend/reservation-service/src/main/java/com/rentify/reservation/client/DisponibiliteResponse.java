package com.rentify.reservation.client;

/** Réponse du service Flotte : disponibilité + tarif journalier du véhicule. */
public record DisponibiliteResponse(boolean disponible, double prixJournalier) { }
