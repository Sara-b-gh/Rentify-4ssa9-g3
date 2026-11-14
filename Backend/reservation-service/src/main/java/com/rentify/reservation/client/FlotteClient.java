package com.rentify.reservation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Appel synchrone (OpenFeign) vers le service Flotte :
 * GET /api/vehicules/{id}/disponibilite?debut=...&fin=...
 * - url vide => le nom "flotte-service" est résolu via Eureka ;
 * - url renseignée (rentify.flotte.url) => appel direct (utile sans Eureka).
 * - fallback : déclenché par Resilience4j (timeout, erreur, circuit ouvert).
 */
@FeignClient(name = "flotte-service", url = "${rentify.flotte.url:}", fallback = FlotteClientFallback.class)
public interface FlotteClient {

    @GetMapping("/api/vehicules/{id}/disponibilite")
    DisponibiliteResponse verifierDisponibilite(@PathVariable("id") UUID vehiculeId,
                                                @RequestParam("debut") LocalDate debut,
                                                @RequestParam("fin") LocalDate fin);
}
