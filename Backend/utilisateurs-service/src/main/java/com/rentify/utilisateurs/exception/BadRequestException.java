package com.rentify.utilisateurs.exception;

/** Erreur métier (400) : transformée en réponse HTTP par GlobalExceptionHandler. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
