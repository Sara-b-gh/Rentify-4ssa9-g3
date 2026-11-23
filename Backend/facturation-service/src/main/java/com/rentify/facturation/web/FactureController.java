package com.rentify.facturation.web;

import com.rentify.facturation.domain.*;
import com.rentify.facturation.dto.*;
import com.rentify.facturation.service.FactureService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

/** API REST : Facture d'un contrat de location (montantTotal = location + pénalités). */
@RestController
@RequestMapping("/api/factures")
public class FactureController {

    private final FactureService service;

    public FactureController(FactureService service) {
        this.service = service;
    }

    /** 200 : liste complète. */
    @GetMapping
    public List<Facture> lister() {
        return service.lister();
    }

    /** 200 trouvé, 404 introuvable. */
    @GetMapping("/{id}")
    public Facture trouver(@PathVariable Long id) {
        return service.trouver(id);
    }


    /** 201 créé, 400 données invalides, 404 référence introuvable, 409 conflit, 503 service distant indisponible. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Facture creer(@Valid @RequestBody FactureRequest req) {
        return service.creer(req);
    }

    /** 204 supprimé, 404 introuvable. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }

    /** Montant total, déjà payé et reste à payer. */
    @GetMapping("/{id}/reste")
    public ResteAPayerResponse reste(@PathVariable Long id) {
        return service.reste(id);
    }
}
