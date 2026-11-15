package com.rentify.facturation.client;

import com.rentify.facturation.dto.TarifResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

// url vide => Eureka résout "flotte-service" ; sinon adresse directe (rentify.flotte.url)
@FeignClient(name = "flotte-service",
        url = "${rentify.flotte.url:}",
        fallbackFactory = FlotteClientFallbackFactory.class)
public interface FlotteClient {

    @GetMapping("/api/vehicules/{id}/tarif")
    TarifResponse getTarif(@PathVariable("id") UUID vehiculeId);
}