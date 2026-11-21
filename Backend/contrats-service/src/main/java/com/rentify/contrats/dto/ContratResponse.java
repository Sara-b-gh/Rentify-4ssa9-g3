package com.rentify.contrats.dto;

import com.rentify.contrats.domain.Contrat;
import com.rentify.contrats.domain.StatutContrat;

import java.time.LocalDateTime;
import java.util.List;

public record ContratResponse(
        Long id,
        Long reservationId,
        Long vehiculeId,
        String clientId,
        LocalDateTime dateDebut,
        LocalDateTime dateFinPrevue,
        LocalDateTime dateRetourReelle,
        StatutContrat statut,
        List<ConducteurResponse> conducteurs) {

    public static ContratResponse from(Contrat c) {
        return new ContratResponse(
                c.getId(), c.getReservationId(), c.getVehiculeId(), c.getClientId(),
                c.getDateDebut(), c.getDateFinPrevue(), c.getDateRetourReelle(), c.getStatut(),
                c.getConducteurs().stream().map(ConducteurResponse::from).toList());
    }
}
