package com.rentify.facturation.client;

/** Identification lue dans le service Infractions (seuls les champs utiles sont lus ; les autres sont ignorés). */
public record IdentificationDto(Long id, Long infractionId, Long contratId, Long conducteurId) {
}
