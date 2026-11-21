package com.rentify.utilisateurs.exception;

/** Erreur métier (503) : transformée en réponse HTTP par GlobalExceptionHandler. */
public class ServiceIndisponibleException extends RuntimeException {
    public ServiceIndisponibleException(String message) {
        super(message);
    }
}
