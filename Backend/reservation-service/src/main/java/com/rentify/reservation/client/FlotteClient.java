package com.rentify.reservation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Appel synchrone (OpenFeign) vers le service Flotte :
 * GET /api/vehicules/{id}/disponibilite?debut=...&fin=...
 * - url vide => le nom "flotte-service" est résolu via Eureka ;
 * - url renseignée (rentify.flotte.url) => appel direct (utile sans Eureka).
 * - fallbackFactory : déclenché par Resilience4j (timeout, erreur, circuit ouvert).
 */
@FeignClient(name = "flotte-service", url = "${rentify.flotte.url:}", fallbackFactory = FlotteClientFallbackFactory.class)
public interface FlotteClient {

    @GetMapping("/api/vehicules/{id}/disponibilite")
    DisponibiliteResponse disponibiliteIso(@PathVariable("id") Long vehiculeId,
                                           @RequestParam("debut") String debut,
                                           @RequestParam("fin") String fin);

    /** Les dates sont envoyées au format ISO (2026-12-01) : sinon Feign utilise un format local que Flotte refuse (400). */
    default DisponibiliteResponse verifierDisponibilite(Long vehiculeId, LocalDate debut, LocalDate fin) {
        return disponibiliteIso(vehiculeId, debut.toString(), fin.toString());
    }
}