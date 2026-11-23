package com.rentify.facturation.dto;

import com.rentify.facturation.domain.*;
import jakarta.validation.constraints.*;
import java.time.*;

/** Données reçues pour créer / modifier : Paiement partiel ou total d'une facture. */
public record PaiementRequest(
        @NotNull Long factureId,
        @NotNull @Positive Double montant,
        @NotNull ModePaiement mode) {
}
