package com.rentify.infractions.client;

import com.rentify.infractions.exception.AucunContratActifException;
import com.rentify.infractions.exception.ServiceIndisponibleException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

// Appelé par le circuit breaker (Resilience4j) quand Contrats répond en erreur, est lent ou en panne
@Component
public class ContratsClientFallbackFactory implements FallbackFactory<ContratsClient> {

    @Override
    public ContratsClient create(Throwable cause) {
        return (vehiculeId, dateHeure) -> {
            if (FlotteClientFallbackFactory.estUn404(cause)) {
                throw new AucunContratActifException(
                        "Aucun contrat actif pour le véhicule " + vehiculeId + " à " + dateHeure);
            }
            throw new ServiceIndisponibleException("Service Contrats indisponible : " + cause.getMessage());
        };
    }
}