package com.rentify.facturation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaiementResponse(
        UUID id,
        double montant,
        String mode,
        LocalDateTime datePaiement
) {
}