package com.rentify.reservation.client;

import com.rentify.reservation.exception.FlotteIndisponibleException;
import com.rentify.reservation.exception.VehiculeIntrouvableException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Comportement quand l'appel à Flotte échoue (Resilience4j) :
 * - Flotte répond 404  => le véhicule n'existe pas : HTTP 404 (et non 503) ;
 * - Flotte arrêtée / timeout / erreur => la réservation est REFUSÉE (HTTP 503), rien n'est enregistré.
 */
@Component
public class FlotteClientFallbackFactory implements FallbackFactory<FlotteClient> {

    @Override
    public FlotteClient create(Throwable cause) {
        return new FlotteClient() {
            @Override
            public DisponibiliteResponse disponibiliteIso(Long vehiculeId, String debut, String fin) {
                if (cause instanceof FeignException.NotFound) {
                    throw new VehiculeIntrouvableException(vehiculeId);          // 404
                }
                // le détail de la cause aide au diagnostic (ex : Flotte arrêtée, erreur de format)
                throw new FlotteIndisponibleException(
                        "Service Flotte indisponible : la réservation est refusée, veuillez réessayer plus tard ("
                                + cause.getClass().getSimpleName() + ")");        // 503
            }
        };
    }
}