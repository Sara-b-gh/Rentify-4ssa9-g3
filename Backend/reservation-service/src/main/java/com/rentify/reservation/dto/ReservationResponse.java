package com.rentify.reservation.dto;

import com.rentify.reservation.domain.Reservation;

import java.time.LocalDate;

/** Représentation renvoyée par l'API (on n'expose jamais l'entité directement). */
public record ReservationResponse(
        Long id, Long clientId, Long vehiculeId,
        LocalDate dateDebut, LocalDate dateFin,
        double montantEstime, String statut) {

    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(r.getId(), r.getClientId(), r.getVehiculeId(),
                r.getDateDebut(), r.getDateFin(), r.getMontantEstime(), r.getStatut().name());
    }
}
