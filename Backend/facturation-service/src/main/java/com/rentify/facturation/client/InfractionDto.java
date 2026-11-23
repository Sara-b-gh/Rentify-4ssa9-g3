package com.rentify.facturation.client;

/** Infraction lue dans le service Infractions (seuls les champs utiles sont lus ; les autres sont ignorés). */
public record InfractionDto(Long id, Double montant, String statut) {
}
