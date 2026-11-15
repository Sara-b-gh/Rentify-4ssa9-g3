package com.rentify.facturation.web;

import com.rentify.facturation.dto.FactureResponse;
import com.rentify.facturation.dto.PaiementRequest;
import com.rentify.facturation.service.FactureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/factures")
public class FactureController {

    private final FactureService factureService;

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    // 200, 404
    @GetMapping("/{id}")
    public FactureResponse detail(@PathVariable UUID id) {
        return factureService.consulter(id);
    }

    // 200, 404
    @GetMapping("/par-contrat/{contratId}")
    public FactureResponse parContrat(@PathVariable UUID contratId) {
        return factureService.consulterParContrat(contratId);
    }

    // 201, 400, 404, 409
    @PostMapping("/{id}/paiements")
    public ResponseEntity<FactureResponse> payer(@PathVariable UUID id,
                                                 @Valid @RequestBody PaiementRequest request) {
        FactureResponse reponse = factureService.enregistrerPaiement(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    // 200, 404, 409
    @PostMapping("/{id}/remboursement")
    public FactureResponse rembourser(@PathVariable UUID id) {
        return factureService.rembourser(id);
    }
}