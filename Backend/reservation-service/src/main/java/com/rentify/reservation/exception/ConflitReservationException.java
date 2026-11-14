package com.rentify.reservation.exception;

/** Conflit (véhicule déjà réservé, statut incompatible) => HTTP 409. */
public class ConflitReservationException extends RuntimeException {
    public ConflitReservationException(String message) { super(message); }
}
