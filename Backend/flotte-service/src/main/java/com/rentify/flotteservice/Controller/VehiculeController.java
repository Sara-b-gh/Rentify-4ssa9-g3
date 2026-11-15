package com.rentify.flotteservice.Controller;

import com.rentify.flotteservice.Entity.Vehicule;
import com.rentify.flotteservice.Entity.StatutVehicule;
import com.rentify.flotteservice.Service.IVehiculeService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @PostMapping
    public Vehicule ajouterVehicule(@RequestBody Vehicule vehicule) {
        return vehiculeService.ajouterVehicule(vehicule);
    }

    @PutMapping
    public Vehicule modifierVehicule(@RequestBody Vehicule vehicule) {
        return vehiculeService.modifierVehicule(vehicule);
    }

    @GetMapping
    public List<Vehicule> afficherToutesVehicule() {
        return vehiculeService.afficherToutesVehicule();
    }

    @GetMapping("/{id}")
    public Vehicule afficherVehiculeById(@PathVariable UUID id) {
        return vehiculeService.afficherVehiculeById(id);
    }

    @GetMapping("/par-immatriculation/{plaque}")
    public Vehicule rechercherParImmatriculation(
            @PathVariable String plaque) {
        return vehiculeService.rechercherParImmatriculation(plaque);
    }

    @PatchMapping("/{id}/statut")
    public Vehicule modifierStatut(
            @PathVariable UUID id,
            @RequestParam StatutVehicule statut) {
        return vehiculeService.modifierStatut(id, statut);
    }

    @DeleteMapping("/{id}")
    public void supprimerVehicule(@PathVariable UUID id) {
        vehiculeService.supprimerVehicule(id);
    }
    @GetMapping("/{id}/disponibilite")
    public boolean verifierDisponibilite(
            @PathVariable UUID id,
            @RequestParam LocalDate debut,
            @RequestParam LocalDate fin) {

        return vehiculeService.verifierDisponibilite(id, debut, fin);
    }

    @GetMapping("/disponibles")
    public List<Vehicule> afficherVehiculesDisponibles(
            @RequestParam LocalDate debut,
            @RequestParam LocalDate fin) {

        return vehiculeService.afficherVehiculesDisponibles(debut, fin);
    }
}