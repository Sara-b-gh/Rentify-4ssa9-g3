package com.rentify.reservation.exception;

import java.util.UUID;

/** Réservation inexistante => HTTP 404. */
public class ReservationIntrouvableException extends RuntimeException {
    public ReservationIntrouvableException(UUID id) { super("Réservation introuvable : " + id); }
}
