package com.rentify.reservation.event;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Événement publié sur RabbitMQ (ReservationConfirmee / ReservationAnnulee). */
public record ReservationEvent(
        String type, UUID reservationId, String clientId, UUID vehiculeId,
        LocalDate dateDebut, LocalDate dateFin, Instant survenuLe) { }
