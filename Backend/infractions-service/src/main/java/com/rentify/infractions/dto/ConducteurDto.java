package com.rentify.infractions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Conducteur déclaré dans un contrat (réponse de Contrats). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConducteurDto(String userId, String numeroPermis, boolean principal) {
}
