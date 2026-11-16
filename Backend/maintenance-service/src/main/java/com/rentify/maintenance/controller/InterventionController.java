package com.rentify.maintenance.controller;

import com.rentify.maintenance.domain.Intervention;
import com.rentify.maintenance.repository.InterventionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interventions")
public class InterventionController {

    private final InterventionRepository interventionRepository;

    public InterventionController(InterventionRepository interventionRepository) {
        this.interventionRepository = interventionRepository;
    }

    @PostMapping
    public ResponseEntity<Intervention> planifierIntervention(@RequestBody Intervention intervention) {
        intervention.planifier();
        Intervention saved = interventionRepository.save(intervention);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping
    public List<Intervention> getInterventions(@RequestParam(required = false) UUID vehiculeId) {
        if (vehiculeId != null) {
            return interventionRepository.findByVehiculeId(vehiculeId);
        }
        return interventionRepository.findAll();
    }

    @PatchMapping("/{id}/terminer")
    public ResponseEntity<Intervention> terminerIntervention(@PathVariable UUID id, @RequestParam double cout) {
        return interventionRepository.findById(id).map(interv -> {
            interv.terminer(cout);
            interventionRepository.save(interv);
            return ResponseEntity.ok(interv);
        }).orElse(ResponseEntity.notFound().build());
    }
}
