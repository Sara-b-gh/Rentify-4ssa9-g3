package com.rentify.facturation.client;

import com.rentify.facturation.exception.TarifIndisponibleException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

// Appelé par le circuit breaker quand Flotte est lent, en panne ou répond en erreur
@Component
public class FlotteClientFallbackFactory implements FallbackFactory<FlotteClient> {

    @Override
    public FlotteClient create(Throwable cause) {
        return vehiculeId -> {
            throw new TarifIndisponibleException("Service Flotte indisponible : " + cause.getMessage());
        };
    }
}