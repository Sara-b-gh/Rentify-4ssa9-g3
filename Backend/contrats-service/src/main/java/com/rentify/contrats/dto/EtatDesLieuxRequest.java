package com.rentify.contrats.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record EtatDesLieuxRequest(
        @PositiveOrZero int kilometrage,
        @NotBlank String carburant,
        String dommages) {
}
