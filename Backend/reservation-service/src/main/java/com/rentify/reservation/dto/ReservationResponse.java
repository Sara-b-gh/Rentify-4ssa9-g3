package com.rentify.reservation.dto;

import com.rentify.reservation.domain.Reservation;

import java.time.LocalDate;
import java.util.UUID;

/** Représentation renvoyée par l'API (on n'expose jamais l'entité directement). */
public record ReservationResponse(
        UUID id, String clientId, UUID vehiculeId,
        LocalDate dateDebut, LocalDate dateFin,
        double montantEstime, String statut) {

    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(r.getId(), r.getClientId(), r.getVehiculeId(),
                r.getDateDebut(), r.getDateFin(), r.getMontantEstime(), r.getStatut().name());
    }
}
