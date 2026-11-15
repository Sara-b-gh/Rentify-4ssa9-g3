package com.rentify.facturation.dto;

import java.util.UUID;

public record ConducteurIdentifieEvent(
        UUID infractionId,
        UUID contratId,
        String conducteurId,
        double montant
) {
}