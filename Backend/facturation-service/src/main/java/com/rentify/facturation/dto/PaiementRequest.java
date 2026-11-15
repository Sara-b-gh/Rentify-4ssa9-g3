package com.rentify.facturation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PaiementRequest(
        @Positive(message = "Le montant doit être positif") double montant,
        @NotBlank(message = "Le mode de paiement est obligatoire") String mode
) {
}