package com.rentify.infractions.exception;

/** Un service appelé (Flotte, Contrats) ne répond pas : on répond 503. */
public class ServiceIndisponibleException extends RuntimeException {
    public ServiceIndisponibleException(String message) {
        super(message);
    }
}
