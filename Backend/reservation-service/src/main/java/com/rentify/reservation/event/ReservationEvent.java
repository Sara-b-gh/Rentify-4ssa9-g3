package com.rentify.reservation.event;

import java.time.Instant;
import java.time.LocalDate;

/** Événement publié sur RabbitMQ (ReservationConfirmee / ReservationAnnulee). */
public record ReservationEvent(
        String type, Long reservationId, Long clientId, Long vehiculeId,
        LocalDate dateDebut, LocalDate dateFin, Instant survenuLe) { }
