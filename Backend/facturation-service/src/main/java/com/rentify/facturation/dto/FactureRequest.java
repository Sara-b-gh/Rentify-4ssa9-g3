package com.rentify.facturation.dto;

import com.rentify.facturation.domain.*;
import jakarta.validation.constraints.*;
import java.time.*;

/** Données reçues pour créer / modifier : Facture d'un contrat de location (montantTotal = location + pénalités). */
public record FactureRequest(
        @NotNull Long contratId) {
}
