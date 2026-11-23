package com.rentify.facturation.dto;

import com.rentify.facturation.domain.*;
import jakarta.validation.constraints.*;
import java.time.*;

/** Données reçues pour créer / modifier : Pénalité (issue d'une infraction) ajoutée au montant d'une facture. */
public record PenaliteRequest(
        @NotNull Long factureId,
        @NotNull Long infractionId) {
}
