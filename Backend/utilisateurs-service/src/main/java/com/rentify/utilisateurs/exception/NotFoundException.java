package com.rentify.utilisateurs.exception;

/** Erreur métier (404) : transformée en réponse HTTP par GlobalExceptionHandler. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
