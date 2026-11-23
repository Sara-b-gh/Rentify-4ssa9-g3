package com.rentify.facturation.web;

import com.rentify.facturation.domain.*;
import com.rentify.facturation.dto.*;
import com.rentify.facturation.service.PaiementService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

/** API REST : Paiement partiel ou total d'une facture. */
@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService service;

    public PaiementController(PaiementService service) {
        this.service = service;
    }

    /** 200 : liste complète. */
    @GetMapping
    public List<Paiement> lister() {
        return service.lister();
    }

    /** 200 trouvé, 404 introuvable. */
    @GetMapping("/{id}")
    public Paiement trouver(@PathVariable Long id) {
        return service.trouver(id);
    }


    /** 201 créé, 400 données invalides, 404 référence introuvable, 409 conflit, 503 service distant indisponible. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Paiement creer(@Valid @RequestBody PaiementRequest req) {
        return service.creer(req);
    }

    /** Paiements d'une facture. */
    @GetMapping("/par-facture/{factureId}")
    public List<Paiement> parFacture(@PathVariable Long factureId) {
        return service.listerParFacture(factureId);
    }
}
