package com.rentify.reservation.exception;

/** Le client n'existe pas dans le service Utilisateurs => HTTP 404. */
public class ClientIntrouvableException extends RuntimeException {
    public ClientIntrouvableException(Long clientId) { super("Client introuvable : " + clientId); }
}
