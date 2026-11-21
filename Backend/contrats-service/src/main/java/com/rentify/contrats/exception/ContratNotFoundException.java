package com.rentify.contrats.exception;

public class ContratNotFoundException extends RuntimeException {
    public ContratNotFoundException(String message) {
        super(message);
    }
}
