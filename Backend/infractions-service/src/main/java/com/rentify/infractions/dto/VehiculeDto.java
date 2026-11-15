package com.rentify.infractions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/** Réponse de Flotte (GET /api/vehicules/par-immatriculation/{plaque}) : on ne garde que ce qui sert. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VehiculeDto(UUID id, String immatriculation) {
}
