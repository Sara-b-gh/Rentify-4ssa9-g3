package com.rentify.infractions.dto;

import com.rentify.infractions.domain.IdentificationConducteur;

import java.time.LocalDateTime;
import java.util.UUID;

public record IdentificationResponse(
        UUID id,
        UUID contratId,
        String conducteurId,
        LocalDateTime dateIdentification) {

    public static IdentificationResponse from(IdentificationConducteur i) {
        return new IdentificationResponse(i.getId(), i.getContratId(), i.getConducteurId(), i.getDateIdentification());
    }
}
