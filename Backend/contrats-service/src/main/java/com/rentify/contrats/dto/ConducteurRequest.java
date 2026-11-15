package com.rentify.contrats.dto;

import jakarta.validation.constraints.NotBlank;

public record ConducteurRequest(
        @NotBlank String userId,
        @NotBlank String numeroPermis) {
}
