package com.rentify.utilisateurs.exception;

/** Erreur métier (409) : transformée en réponse HTTP par GlobalExceptionHandler. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
