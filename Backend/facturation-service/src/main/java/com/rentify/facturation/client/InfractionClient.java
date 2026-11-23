package com.rentify.facturation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/** Appel synchrone (OpenFeign) vers le service infractions-service. */
@FeignClient(name = "infractions-service", url = "${rentify.infractions.url}")
public interface InfractionClient {
    @GetMapping("/api/infractions/{id}")
    InfractionDto trouver(@PathVariable("id") Long id);

    @GetMapping("/api/identifications/par-infraction/{infractionId}")
    IdentificationDto parInfraction(@PathVariable("infractionId") Long infractionId);
}
