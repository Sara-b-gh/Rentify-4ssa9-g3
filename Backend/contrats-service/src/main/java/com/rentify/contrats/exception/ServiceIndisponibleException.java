package com.rentify.contrats.exception;

/** Un service appelé (Réservation, Utilisateurs) ne répond pas : on répond 503. */
public class ServiceIndisponibleException extends RuntimeException {
    public ServiceIndisponibleException(String message) {
        super(message);
    }
}
