package com.rentify.infractions.mock;

import com.rentify.infractions.dto.ConducteurDto;
import com.rentify.infractions.dto.ContratActifDto;
import com.rentify.infractions.dto.VehiculeDto;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * FAUX services Flotte et Contrats, actifs UNIQUEMENT avec le profil « local ».
 * Ils permettent de tester Infractions tant que les services des coéquipières ne tournent pas.
 *
 * Plaque 123TU4567 : véhicule loué en novembre 2026 (conducteur principal ahmed-001)  -> CONDUCTEUR_IDENTIFIE
 * Plaque 200TU1000 : véhicule non loué                                               -> NON_IMPUTABLE
 * Plaque 999TU9999 : Contrats simule une panne (503)                                 -> reste DECLAREE
 * Autre plaque     : 404 (véhicule inconnu)
 */
@Profile("local")
@RestController
@RequestMapping("/api")
public class MockServicesExternesController {

    static final UUID VEHICULE_LOUE = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
    static final UUID VEHICULE_LIBRE = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000002");
    static final UUID VEHICULE_PANNE = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000003");
    // Même contratId que les données de test de Facturation : la pénalité s'ajoute à sa facture
    static final UUID CONTRAT = UUID.fromString("11111111-1111-1111-1111-111111111111");

    /** Faux Flotte : GET /api/vehicules/par-immatriculation/{plaque} */
    @GetMapping("/vehicules/par-immatriculation/{plaque}")
    public VehiculeDto vehicule(@PathVariable String plaque) {
        return switch (plaque) {
            case "123TU4567" -> new VehiculeDto(VEHICULE_LOUE, plaque);
            case "200TU1000" -> new VehiculeDto(VEHICULE_LIBRE, plaque);
            case "999TU9999" -> new VehiculeDto(VEHICULE_PANNE, plaque);
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Véhicule inconnu");
        };
    }

    /** Faux Contrats : GET /api/contrats/actif?vehiculeId=&dateHeure= */
    @GetMapping("/contrats/actif")
    public ContratActifDto contratActif(@RequestParam UUID vehiculeId,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                        LocalDateTime dateHeure) {
        if (VEHICULE_PANNE.equals(vehiculeId)) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Contrats en panne (simulation)");
        }
        boolean enNovembre2026 = !dateHeure.isBefore(LocalDateTime.of(2026, 11, 1, 0, 0))
                && dateHeure.isBefore(LocalDateTime.of(2026, 12, 1, 0, 0));
        if (VEHICULE_LOUE.equals(vehiculeId) && enNovembre2026) {
            return new ContratActifDto(CONTRAT, VEHICULE_LOUE, "ahmed-001", List.of(
                    new ConducteurDto("ahmed-001", "P-123456", true),
                    new ConducteurDto("sara-002", "P-654321", false)));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun contrat actif");
    }
}
