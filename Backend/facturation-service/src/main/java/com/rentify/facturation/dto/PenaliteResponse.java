package com.rentify.facturation.dto;

import java.util.UUID;

public record PenaliteResponse(
        UUID id,
        UUID infractionId,
        String conducteurId,
        double montant
) {
}