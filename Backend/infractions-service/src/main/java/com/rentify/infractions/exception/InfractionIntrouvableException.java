package com.rentify.infractions.exception;

/** Infraction inconnue : on répond 404. */
public class InfractionIntrouvableException extends RuntimeException {
    public InfractionIntrouvableException(String message) {
        super(message);
    }
}
