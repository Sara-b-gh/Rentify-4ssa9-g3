package com.rentify.infractions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.UUID;

/** Réponse de Contrats (GET /api/contrats/actif) : contrat actif et ses conducteurs. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ContratActifDto(UUID id, UUID vehiculeId, String clientId, List<ConducteurDto> conducteurs) {

    /** Le conducteur responsable : le conducteur principal, sinon le premier, sinon le client. */
    public String conducteurResponsable() {
        if (conducteurs == null || conducteurs.isEmpty()) {
            return clientId;
        }
        return conducteurs.stream()
                .filter(ConducteurDto::principal)
                .findFirst()
                .orElse(conducteurs.get(0))
                .userId();
    }
}
