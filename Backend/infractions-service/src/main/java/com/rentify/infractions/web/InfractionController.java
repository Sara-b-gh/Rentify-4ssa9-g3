package com.rentify.infractions.web;

import com.rentify.infractions.domain.StatutInfraction;
import com.rentify.infractions.dto.CorrigerConducteurRequest;
import com.rentify.infractions.dto.DeclarerInfractionRequest;
import com.rentify.infractions.dto.IdentificationResponse;
import com.rentify.infractions.dto.InfractionResponse;
import com.rentify.infractions.service.InfractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** API REST du microservice Infractions. Rôle (Keycloak) : AGENT pour déclarer et corriger (401/403). */
@Tag(name = "Infractions", description = "Déclaration des infractions et identification du conducteur")
@RestController
@RequestMapping("/api/infractions")
public class InfractionController {

    private final InfractionService service;

    public InfractionController(InfractionService service) {
        this.service = service;
    }

    /** Déclarer une infraction et identifier le conducteur : 201, 400, 404, 503. */
    @Operation(summary = "Déclarer une infraction et identifier le conducteur (201, 400, 404, 503)")
    @PostMapping
    public ResponseEntity<InfractionResponse> declarer(@Valid @RequestBody DeclarerInfractionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(InfractionResponse.from(service.declarer(request)));
    }

    /** Lister les infractions (filtre facultatif par statut) : 200. */
    @Operation(summary = "Lister les infractions, filtre facultatif par statut (200)")
    @GetMapping
    public List<InfractionResponse> lister(@RequestParam(required = false) StatutInfraction statut) {
        return service.lister(statut).stream().map(InfractionResponse::from).toList();
    }

    /** Détail d'une infraction : 200, 404. */
    @Operation(summary = "Détail d'une infraction (200, 404)")
    @GetMapping("/{id}")
    public InfractionResponse get(@PathVariable UUID id) {
        return InfractionResponse.from(service.get(id));
    }

    /** Conducteur identifié : 200, 404. */
    @Operation(summary = "Conducteur identifié pour une infraction (200, 404)")
    @GetMapping("/{id}/conducteur")
    public IdentificationResponse conducteur(@PathVariable UUID id) {
        return IdentificationResponse.from(service.conducteur(id));
    }

    /** Relancer l'identification (si Contrats était en panne) : 200, 404, 409, 503. */
    @Operation(summary = "Relancer l'identification du conducteur (200, 404, 409, 503)")
    @PostMapping("/{id}/identification")
    public InfractionResponse relancer(@PathVariable UUID id) {
        return InfractionResponse.from(service.relancerIdentification(id));
    }

    /** Corriger le conducteur manuellement : 200, 400, 404. */
    @Operation(summary = "Corriger le conducteur manuellement (200, 400, 404)")
    @PatchMapping("/{id}/conducteur")
    public InfractionResponse corriger(@PathVariable UUID id, @Valid @RequestBody CorrigerConducteurRequest request) {
        return InfractionResponse.from(service.corrigerConducteur(id, request));
    }
}
