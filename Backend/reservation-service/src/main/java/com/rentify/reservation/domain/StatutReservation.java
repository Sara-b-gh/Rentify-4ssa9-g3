package com.rentify.reservation.domain;

/** Cycle de vie d'une réservation. */
public enum StatutReservation {
    CONFIRMEE,  // réservation validée (vérification Flotte OK)
    ANNULEE,    // annulée par le client / l'agent ou suite à la désactivation du client
    TERMINEE    // période écoulée (contrat clôturé)
}
