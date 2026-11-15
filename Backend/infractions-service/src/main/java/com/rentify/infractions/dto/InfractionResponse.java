package com.rentify.infractions.dto;

import com.rentify.infractions.domain.Infraction;
import com.rentify.infractions.domain.StatutInfraction;

import java.time.LocalDateTime;
import java.util.UUID;

public record InfractionResponse(
        UUID id,
        String immatriculation,
        UUID vehiculeId,
        LocalDateTime dateHeure,
        String lieu,
        double montant,
        StatutInfraction statut,
        IdentificationResponse identification) {

    public static InfractionResponse from(Infraction i) {
        return new InfractionResponse(i.getId(), i.getImmatriculation(), i.getVehiculeId(), i.getDateHeure(),
                i.getLieu(), i.getMontant(), i.getStatut(),
                i.getIdentification() == null ? null : IdentificationResponse.from(i.getIdentification()));
    }
}
