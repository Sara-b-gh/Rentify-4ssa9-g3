package com.rentify.facturation.service;

import com.rentify.facturation.domain.Facture;
import com.rentify.facturation.dto.FactureResponse;
import com.rentify.facturation.dto.PaiementResponse;
import com.rentify.facturation.dto.PenaliteResponse;

import java.util.List;

public final class FactureMapper {

    private FactureMapper() {
    }

    public static FactureResponse toResponse(Facture f) {
        List<PaiementResponse> paiements = f.getPaiements().stream()
                .map(p -> new PaiementResponse(p.getId(), p.getMontant(), p.getMode(), p.getDatePaiement()))
                .toList();

        List<PenaliteResponse> penalites = f.getPenalites().stream()
                .map(p -> new PenaliteResponse(p.getId(), p.getInfractionId(), p.getConducteurId(), p.getMontant()))
                .toList();

        return new FactureResponse(
                f.getId(),
                f.getContratId(),
                f.getMontantLocation(),
                f.getMontantTotal(),
                f.montantPaye(),
                f.getDateEmission(),
                f.getStatut(),
                paiements,
                penalites
        );
    }
}