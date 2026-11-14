package com.rentify.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/** Corps du POST /api/reservations. */
public record ReservationRequest(
        @NotBlank String clientId,
        @NotNull UUID vehiculeId,
        @NotNull LocalDate dateDebut,
        @NotNull LocalDate dateFin) { }
