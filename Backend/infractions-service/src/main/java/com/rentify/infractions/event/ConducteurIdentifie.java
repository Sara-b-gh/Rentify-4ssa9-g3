package com.rentify.infractions.event;

import java.util.UUID;

/** Événement publié quand le conducteur est identifié : Facturation crée la pénalité. */
public record ConducteurIdentifie(
        UUID infractionId,
        UUID contratId,
        String conducteurId,
        double montant) {
}
