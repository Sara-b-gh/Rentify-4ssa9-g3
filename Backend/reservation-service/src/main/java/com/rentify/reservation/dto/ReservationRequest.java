package com.rentify.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Corps du POST /api/reservations. */
public record ReservationRequest(
        @NotNull Long clientId,
        @NotNull Long vehiculeId,
        @NotNull LocalDate dateDebut,
        @NotNull LocalDate dateFin) { }
