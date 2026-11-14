package com.rentify.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Point d'entrée du microservice Réservation (contexte borné "Réservation").
 * @EnableFeignClients active les clients REST déclaratifs (appel du service Flotte).
 */
@SpringBootApplication
@EnableFeignClients
public class ReservationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}
