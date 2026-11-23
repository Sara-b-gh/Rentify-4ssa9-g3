package com.rentify.facturation.client;

/** Réservation lue dans le service Réservation (seuls les champs utiles sont lus ; les autres sont ignorés). */
public record ReservationDto(Long id, Double montantEstime) {
}
