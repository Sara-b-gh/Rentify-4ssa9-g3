package com.rentify.reservation.exception;

/** Service Flotte injoignable (timeout / circuit ouvert) : la réservation est refusée => HTTP 503. */
public class FlotteIndisponibleException extends RuntimeException {
    public FlotteIndisponibleException(String message) { super(message); }
}
