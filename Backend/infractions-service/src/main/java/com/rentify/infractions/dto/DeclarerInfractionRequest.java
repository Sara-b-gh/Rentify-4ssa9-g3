package com.rentify.infractions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/** Corps de POST /api/infractions : ce qui est écrit sur l'avis de contravention. */
public record DeclarerInfractionRequest(
        @NotBlank String immatriculation,
        @NotNull LocalDateTime dateHeure,
        @NotBlank String lieu,
        @Positive double montant) {
}
