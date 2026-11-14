package com.rentify.reservation.exception;

/** Données invalides => HTTP 400. */
public class ReservationInvalideException extends RuntimeException {
    public ReservationInvalideException(String message) { super(message); }
}
