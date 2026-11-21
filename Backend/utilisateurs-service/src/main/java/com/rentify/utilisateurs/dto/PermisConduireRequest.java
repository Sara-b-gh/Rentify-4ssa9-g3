package com.rentify.utilisateurs.dto;

import com.rentify.utilisateurs.domain.*;
import jakarta.validation.constraints.*;
import java.time.*;

/** Données reçues pour créer / modifier : Permis de conduire d'un utilisateur (0 ou 1 par utilisateur). */
public record PermisConduireRequest(
        @NotNull Long utilisateurId,
        @NotBlank String numero,
        @NotNull LocalDate dateObtention,
        @NotNull LocalDate dateExpiration) {
}
