package com.rentify.infractions.exception;

/** Aucune voiture de la flotte ne porte cette plaque : on répond 404. */
public class VehiculeIntrouvableException extends RuntimeException {
    public VehiculeIntrouvableException(String message) {
        super(message);
    }
}
