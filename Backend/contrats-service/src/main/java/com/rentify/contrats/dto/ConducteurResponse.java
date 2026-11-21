package com.rentify.contrats.dto;

import com.rentify.contrats.domain.Conducteur;

public record ConducteurResponse(String userId, String numeroPermis, boolean principal) {

    public static ConducteurResponse from(Conducteur c) {
        return new ConducteurResponse(c.getUserId(), c.getNumeroPermis(), c.isPrincipal());
    }
}
