package com.rentify.facturation.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ContratClotureEvent(
        UUID contratId,
        UUID vehiculeId,
        LocalDate dateDebut,
        LocalDate dateFin
) {
}