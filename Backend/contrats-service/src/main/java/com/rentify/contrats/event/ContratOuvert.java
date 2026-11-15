package com.rentify.contrats.event;

import java.time.LocalDateTime;

/** Consommé par : Flotte (statut du véhicule), Maintenance. */
public record ContratOuvert(
        Long contratId,
        Long vehiculeId,
        LocalDateTime dateDebut,
        LocalDateTime dateFinPrevue) {
}
