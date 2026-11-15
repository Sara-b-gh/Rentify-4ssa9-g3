package com.rentify.infractions.client;

import com.rentify.infractions.exception.ServiceIndisponibleException;
import com.rentify.infractions.exception.VehiculeIntrouvableException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

// Appelé par le circuit breaker (Resilience4j) quand Flotte répond en erreur, est lent ou en panne
@Component
public class FlotteClientFallbackFactory implements FallbackFactory<FlotteClient> {

    @Override
    public FlotteClient create(Throwable cause) {
        return plaque -> {
            if (estUn404(cause)) {
                throw new VehiculeIntrouvableException("Aucun véhicule avec la plaque " + plaque);
            }
            throw new ServiceIndisponibleException("Service Flotte indisponible : " + cause.getMessage());
        };
    }

    /** Le circuit breaker enveloppe l'erreur : on cherche un 404 dans toute la chaîne des causes. */
    static boolean estUn404(Throwable cause) {
        for (Throwable t = cause; t != null; t = t.getCause()) {
            if (t instanceof FeignException.NotFound) {
                return true;
            }
        }
        return false;
    }
}