package com.rentify.contrats.client;

import com.rentify.contrats.dto.ReservationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/** Appel SYNCHRONE : la réservation est-elle valide ? */
@FeignClient(name = "reservation-service", url = "${clients.reservation.url:}")
public interface ReservationClient {

    @GetMapping("/api/reservations/{id}")
    ReservationDto getReservation(@PathVariable("id") Long id);
}
