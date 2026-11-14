package com.rentify.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Corps du PUT /api/reservations/{id}. */
public record ModifierDatesRequest(
        @NotNull LocalDate dateDebut,
        @NotNull LocalDate dateFin) { }
