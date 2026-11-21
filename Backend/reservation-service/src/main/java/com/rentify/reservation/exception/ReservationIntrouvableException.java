package com.rentify.reservation.exception;


/** Réservation inexistante => HTTP 404. */
public class ReservationIntrouvableException extends RuntimeException {
    public ReservationIntrouvableException(Long id) { super("Réservation introuvable : " + id); }
}
