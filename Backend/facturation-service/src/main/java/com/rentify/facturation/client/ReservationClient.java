package com.rentify.facturation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/** Appel synchrone (OpenFeign) vers le service reservation-service. */
@FeignClient(name = "reservation-service", url = "${rentify.reservation.url}")
public interface ReservationClient {
    @GetMapping("/api/reservations/{id}")
    ReservationDto trouver(@PathVariable("id") Long id);
}
