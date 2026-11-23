package com.rentify.facturation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/** Appel synchrone (OpenFeign) vers le service contrats-service. */
@FeignClient(name = "contrats-service", url = "${rentify.contrats.url}")
public interface ContratClient {
    @GetMapping("/api/contrats/{id}")
    ContratDto trouver(@PathVariable("id") Long id);
}
