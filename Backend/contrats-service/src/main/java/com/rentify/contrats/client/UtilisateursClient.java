package com.rentify.contrats.client;

import com.rentify.contrats.dto.UtilisateurDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Appel SYNCHRONE : le permis du conducteur est-il valide ? */
@FeignClient(name = "utilisateurs-service", url = "${clients.utilisateurs.url:}")
public interface UtilisateursClient {

    @GetMapping("/api/utilisateurs/{id}")
    UtilisateurDto getUtilisateur(@PathVariable("id") String id);
}
