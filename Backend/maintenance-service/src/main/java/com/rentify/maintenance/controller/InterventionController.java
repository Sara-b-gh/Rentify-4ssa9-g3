package com.rentify.maintenance.controller;

import com.rentify.maintenance.domain.Intervention;
import com.rentify.maintenance.service.InterventionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/interventions")
public class InterventionController {

    private final InterventionService interventionService;

    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Intervention planifier(@Valid @RequestBody PlanifierInterventionRequest request) {
        return interventionService.planifier(
                request.vehiculeId(),
                request.technicienId(),
                request.type(),
                request.dateDebut(),
                request.dateFin()
        );
    }

    @GetMapping
    public List<Intervention> lister(@RequestParam Long vehiculeId) {
        return interventionService.listerParVehicule(vehiculeId);
    }

    @PatchMapping("/{id}/terminer")
    public Intervention terminerPatch(@PathVariable Long id, @RequestParam Double cout) {
        return interventionService.terminer(id, cout);
    }

    @PutMapping("/{id}/terminer")
    public Intervention terminerPut(@PathVariable Long id, @RequestParam Double cout) {
        return interventionService.terminer(id, cout);
    }

    public record PlanifierInterventionRequest(
            @NotNull Long vehiculeId,
            @NotNull Long technicienId,
            @NotBlank String type,
            @NotNull LocalDate dateDebut,
            @NotNull LocalDate dateFin
    ) {
    }
}
