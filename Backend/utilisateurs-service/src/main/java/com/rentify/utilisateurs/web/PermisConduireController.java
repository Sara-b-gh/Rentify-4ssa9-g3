package com.rentify.utilisateurs.web;

import com.rentify.utilisateurs.domain.*;
import com.rentify.utilisateurs.dto.*;
import com.rentify.utilisateurs.service.PermisConduireService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

/** API REST : Permis de conduire d'un utilisateur (0 ou 1 par utilisateur). */
@RestController
@RequestMapping("/api/permis")
public class PermisConduireController {

    private final PermisConduireService service;

    public PermisConduireController(PermisConduireService service) {
        this.service = service;
    }

    /** 200 : liste complète. */
    @GetMapping
    public List<PermisConduire> lister() {
        return service.lister();
    }

    /** 200 trouvé, 404 introuvable. */
    @GetMapping("/{id}")
    public PermisConduire trouver(@PathVariable Long id) {
        return service.trouver(id);
    }


    /** 201 créé, 400 données invalides, 404 référence introuvable, 409 conflit, 503 service distant indisponible. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermisConduire creer(@Valid @RequestBody PermisConduireRequest req) {
        return service.creer(req);
    }

    /** 200 modifié, 400, 404, 409. */
    @PutMapping("/{id}")
    public PermisConduire modifier(@PathVariable Long id, @Valid @RequestBody PermisConduireRequest req) {
        return service.modifier(id, req);
    }

    /** 204 supprimé, 404 introuvable. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }

    /** Permis d'un utilisateur : 200 ou 404 (appelé par le service Contrats). */
    @GetMapping("/par-utilisateur/{utilisateurId}")
    public PermisConduire parUtilisateur(@PathVariable Long utilisateurId) {
        return service.parUtilisateur(utilisateurId);
    }
}
