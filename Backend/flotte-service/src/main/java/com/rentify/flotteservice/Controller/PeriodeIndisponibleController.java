package com.rentify.flotteservice.Controller;

import com.rentify.flotteservice.Entity.PeriodeIndisponible;
import com.rentify.flotteservice.Service.IPeriodeIndisponibleService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/periodes-indisponibles")
public class PeriodeIndisponibleController {

    private final IPeriodeIndisponibleService periodeService;

    @PostMapping
    public PeriodeIndisponible ajouterPeriode(
            @RequestBody PeriodeIndisponible periode) {
        return periodeService.ajouterPeriode(periode);
    }

    @PutMapping
    public PeriodeIndisponible modifierPeriode(
            @RequestBody PeriodeIndisponible periode) {
        return periodeService.modifierPeriode(periode);
    }

    @GetMapping
    public List<PeriodeIndisponible> afficherToutesPeriodes() {
        return periodeService.afficherToutesPeriodes();
    }

    @GetMapping("/{id}")
    public PeriodeIndisponible afficherPeriodeById(@PathVariable UUID id) {
        return periodeService.afficherPeriodeById(id);
    }

    @DeleteMapping("/{id}")
    public void supprimerPeriode(@PathVariable UUID id) {
        periodeService.supprimerPeriode(id);
    }
}