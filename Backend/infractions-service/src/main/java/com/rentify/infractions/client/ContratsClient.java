package com.rentify.infractions.client;

import com.rentify.infractions.dto.ContratActifDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.UUID;

/** Appel SYNCHRONE : quel contrat était actif pour ce véhicule à cette date, et qui conduisait ? */
@FeignClient(name = "contrats-service",
        url = "${rentify.contrats.url:}",
        fallbackFactory = ContratsClientFallbackFactory.class)
public interface ContratsClient {

    @GetMapping("/api/contrats/actif")
    ContratActifDto contratActif(@RequestParam("vehiculeId") UUID vehiculeId,
                                 @RequestParam("dateHeure")
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateHeure);
}
