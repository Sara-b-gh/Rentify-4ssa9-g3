package com.rentify.facturation.dto;

import com.rentify.facturation.domain.StatutFacture;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record FactureResponse(
        UUID id,
        UUID contratId,
        double montantLocation,
        double montantTotal,
        double montantPaye,
        LocalDate dateEmission,
        StatutFacture statut,
        List<PaiementResponse> paiements,
        List<PenaliteResponse> penalites
) {
}