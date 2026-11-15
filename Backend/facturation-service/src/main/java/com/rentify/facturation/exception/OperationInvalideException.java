package com.rentify.facturation.exception;

// Utilisée quand l'opération est impossible dans l'état actuel de la facture
// (ex : payer une facture déjà payée, rembourser une facture non payée)
public class OperationInvalideException extends RuntimeException {

    public OperationInvalideException(String message) {
        super(message);
    }
}