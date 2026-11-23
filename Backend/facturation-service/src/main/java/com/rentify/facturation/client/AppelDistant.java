package com.rentify.facturation.client;

import com.rentify.facturation.exception.BadRequestException;
import com.rentify.facturation.exception.ConflictException;
import com.rentify.facturation.exception.NotFoundException;
import com.rentify.facturation.exception.ServiceIndisponibleException;
import feign.FeignException;

import java.util.function.Supplier;

/**
 * Traduit les erreurs d'un appel vers un autre microservice en erreurs HTTP de CE service :
 * 404 distant => 404 ; 409 distant => 409 ; autre 4xx => 400 ; service arrêté, timeout ou 5xx => 503.
 */
public final class AppelDistant {

    private AppelDistant() {
    }

    public static <T> T appeler(String service, String ressource, Supplier<T> appel) {
        try {
            return appel.get();
        } catch (FeignException e) {
            throw traduire(e, service, ressource);
        }
    }

    public static void appelerSansResultat(String service, String ressource, Runnable appel) {
        try {
            appel.run();
        } catch (FeignException e) {
            throw traduire(e, service, ressource);
        }
    }

    private static RuntimeException traduire(FeignException e, String service, String ressource) {
        int status = e.status();
        if (status == 404) {
            return new NotFoundException(ressource + " introuvable (service " + service + ")");
        }
        if (status == 409) {
            return new ConflictException("Refusé par le service " + service + " (conflit) : " + ressource);
        }
        if (status >= 400 && status < 500) {
            return new BadRequestException("Requête refusée par le service " + service + " (HTTP " + status + ")");
        }
        return new ServiceIndisponibleException("Le service " + service + " est indisponible, réessayer plus tard");
    }
}
