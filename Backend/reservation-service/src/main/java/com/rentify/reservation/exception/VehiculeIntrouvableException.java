package com.rentify.reservation.exception;

/** Le véhicule n'existe pas dans le service Flotte => HTTP 404. */
public class VehiculeIntrouvableException extends RuntimeException {
    public VehiculeIntrouvableException(Long vehiculeId) { super("Véhicule introuvable : " + vehiculeId); }
}