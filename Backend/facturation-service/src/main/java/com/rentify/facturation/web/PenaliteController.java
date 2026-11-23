package com.rentify.facturation.web;

import com.rentify.facturation.domain.*;
import com.rentify.facturation.dto.*;
import com.rentify.facturation.service.PenaliteService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

/** API REST : Pénalité (issue d'une infraction) ajoutée au montant d'une facture. */
@RestController
@RequestMapping("/api/penalites")
public class PenaliteController {

    private final PenaliteService service;

    public PenaliteController(PenaliteService service) {
        this.service = service;
    }

    /** 200 : liste complète. */
    @GetMapping
    public List<Penalite> lister() {
        return service.lister();
    }

    /** 200 trouvé, 404 introuvable. */
    @GetMapping("/{id}")
    public Penalite trouver(@PathVariable Long id) {
        return service.trouver(id);
    }


    /** 201 créé, 400 données invalides, 404 référence introuvable, 409 conflit, 503 service distant indisponible. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Penalite creer(@Valid @RequestBody PenaliteRequest req) {
        return service.creer(req);
    }
}
