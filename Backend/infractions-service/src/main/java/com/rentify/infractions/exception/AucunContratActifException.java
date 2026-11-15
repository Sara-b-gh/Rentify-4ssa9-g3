package com.rentify.infractions.exception;

/** Contrats répond 404 : la voiture n'était pas louée à cette date (infraction non imputable). */
public class AucunContratActifException extends RuntimeException {
    public AucunContratActifException(String message) {
        super(message);
    }
}
