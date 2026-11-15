package com.rentify.infractions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservice Infractions (Rentify) : déclare les infractions et identifie
 * le conducteur à partir du contrat actif à la date de l'infraction.
 */
@SpringBootApplication
@EnableFeignClients
public class InfractionsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfractionsServiceApplication.class, args);
    }
}
