package com.rentify.maintenance.web;

import com.rentify.maintenance.domain.Intervention;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/interventions")
public class InterventionController {

    @PostMapping
    public Intervention creerIntervention(@RequestBody Intervention intervention) {
        intervention.setId(UUID.randomUUID());
        intervention.planifier();
        return intervention;
    }

    @PutMapping("/{id}/terminer")
    public Intervention terminerIntervention(@PathVariable UUID id, @RequestParam double cout) {
        Intervention intervention = new Intervention();
        intervention.setId(id);
        intervention.terminer(cout);
        return intervention;
    }
}
