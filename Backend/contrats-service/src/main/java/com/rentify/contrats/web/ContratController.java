package com.rentify.contrats.web;

import com.rentify.contrats.dto.ConducteurRequest;
import com.rentify.contrats.dto.ContratResponse;
import com.rentify.contrats.dto.EtatDesLieuxRequest;
import com.rentify.contrats.dto.OuvrirContratRequest;
import com.rentify.contrats.service.ContratService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/** API REST du microservice Contrats. Rôles (Keycloak) : AGENT/ADMIN pour ouvrir et clôturer (401/403). */
@RestController
@RequestMapping("/api/contrats")
public class ContratController {

    private final ContratService service;

    public ContratController(ContratService service) {
        this.service = service;
    }

    /** Ouvrir un contrat : 201, 400, 404, 409. */
    @PostMapping
    public ResponseEntity<ContratResponse> ouvrir(@Valid @RequestBody OuvrirContratRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ContratResponse.from(service.ouvrir(request)));
    }

    /** Contrat actif à une date/heure (appel interne, utilisé par Infractions) : 200, 404. */
    @GetMapping("/actif")
    public ContratResponse actif(@RequestParam Long vehiculeId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateHeure) {
        return ContratResponse.from(service.actif(vehiculeId, dateHeure));
    }

    /** Détail d'un contrat : 200, 404. */
    @GetMapping("/{id}")
    public ContratResponse get(@PathVariable Long id) {
        return ContratResponse.from(service.get(id));
    }

    /** Contrats d'un client : 200. */
    @GetMapping
    public List<ContratResponse> parClient(@RequestParam String clientId) {
        return service.parClient(clientId).stream().map(ContratResponse::from).toList();
    }

    /** Ajouter un conducteur : 201, 400, 404. */
    @PostMapping("/{id}/conducteurs")
    public ResponseEntity<ContratResponse> ajouterConducteur(@PathVariable Long id,
                                                             @Valid @RequestBody ConducteurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ContratResponse.from(service.ajouterConducteur(id, request)));
    }

    /** Clôturer le contrat (retour du véhicule) : 200, 404, 409. */
    @PostMapping("/{id}/retour")
    public ContratResponse cloturer(@PathVariable Long id, @Valid @RequestBody EtatDesLieuxRequest etatRetour) {
        return ContratResponse.from(service.cloturer(id, etatRetour));
    }
}
