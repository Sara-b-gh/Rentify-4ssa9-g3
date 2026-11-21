package com.rentify.infractions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Corps de PATCH /api/infractions/{id}/conducteur : correction manuelle par l'agent. */
public record CorrigerConducteurRequest(
        @NotNull UUID contratId,
        @NotBlank String conducteurId) {
}
