package com.rentify.contrats.dto;

import java.time.LocalDateTime;

/** Réponse du service Réservation. À ALIGNER avec le DTO réel du Membre 2. */
public record ReservationDto(
        Long id,
        String clientId,
        Long vehiculeId,
        LocalDateTime dateDebut,
        LocalDateTime dateFin,
        String statut) {
}
