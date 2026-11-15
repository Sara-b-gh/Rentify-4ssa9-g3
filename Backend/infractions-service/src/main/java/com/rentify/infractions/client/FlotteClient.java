package com.rentify.infractions.client;

import com.rentify.infractions.dto.VehiculeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Appel SYNCHRONE : quel véhicule porte cette plaque ? url vide => Eureka résout "flotte-service". */
@FeignClient(name = "flotte-service",
        url = "${rentify.flotte.url:}",
        fallbackFactory = FlotteClientFallbackFactory.class)
public interface FlotteClient {

    @GetMapping("/api/vehicules/par-immatriculation/{plaque}")
    VehiculeDto parImmatriculation(@PathVariable("plaque") String plaque);
}
