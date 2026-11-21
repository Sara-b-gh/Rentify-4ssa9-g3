
package com.rentify.flotteservice.event;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationEvent(
        String type,
        UUID reservationId,
        String clientId,
        UUID vehiculeId,
        LocalDate dateDebut,
        LocalDate dateFin,
        Instant survenuLe
) {
}
