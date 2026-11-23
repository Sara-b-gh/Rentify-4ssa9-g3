package com.rentify.facturation.client;

/** Contrat lu dans le service Contrats (seuls les champs utiles sont lus ; les autres sont ignorés). */
public record ContratDto(Long id, Long reservationId, Long clientId, String statut) {
}
