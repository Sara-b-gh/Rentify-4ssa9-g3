package com.rentify.reservation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Appel synchrone (OpenFeign) vers le service Utilisateurs :
 * GET /api/utilisateurs/{id} => le client doit exister et son compte doit être actif.
 */
@FeignClient(name = "utilisateurs-service", url = "${rentify.utilisateurs.url:http://localhost:8087}")
public interface UtilisateurClient {

    @GetMapping("/api/utilisateurs/{id}")
    UtilisateurResponse trouver(@PathVariable("id") Long id);
}
