package com.rentify.contrats.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


public record OuvrirContratRequest(
        @NotNull Long reservationId,
        @NotNull @Valid EtatDesLieuxRequest etatDepart) {
}
