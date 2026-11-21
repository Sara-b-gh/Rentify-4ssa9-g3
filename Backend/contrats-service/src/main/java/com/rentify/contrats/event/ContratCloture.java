package com.rentify.contrats.event;

import java.time.LocalDateTime;

/** Consommé par : Flotte (kilométrage, disponibilité), Facturation (facture), Maintenance. */
public record ContratCloture(
        Long contratId,
        Long vehiculeId,
        LocalDateTime dateDebut,
        LocalDateTime dateRetourReelle,
        int kmDepart,
        int kmRetour) {
}
