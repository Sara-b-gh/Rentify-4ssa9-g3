package com.rentify.facturation.dto;

import java.time.LocalDateTime;

public record ErreurResponse(
        int status,
        String erreur,
        String message,
        LocalDateTime date
) {
}
