package com.rentify.contrats.mock;

import com.rentify.contrats.dto.ReservationDto;
import com.rentify.contrats.dto.UtilisateurDto;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * FAUX services Réservation et Utilisateurs, actifs UNIQUEMENT avec le profil « local ».
 * Ils permettent de tester le service Contrats tant que les services des coéquipiers ne tournent pas.
 * À SUPPRIMER (ou ne pas activer le profil) quand les vrais services sont lancés.
 *
 * Réservation 1 : CONFIRMEE (Ahmed, véhicule 12)      Réservation 2 : ANNULEE
 * Réservation 3 : CONFIRMEE (Sara, véhicule 12)       autre id : 404
 * Utilisateur ahmed-001 : permis valide               utilisateur sara-002 : permis EXPIRÉ
 * Utilisateur karim-003 : permis valide               autre id : 404
 */
@Profile("local")
@RestController
@RequestMapping("/api")
public class MockServicesExternesController {

    @GetMapping("/reservations/{id}")
    public ReservationDto reservation(@PathVariable Long id) {
        return switch (id.intValue()) {
            case 1 -> new ReservationDto(1L, "ahmed-001", 12L,
                    LocalDateTime.of(2026, 11, 15, 9, 0), LocalDateTime.of(2026, 11, 18, 18, 0), "CONFIRMEE");
            case 2 -> new ReservationDto(2L, "ahmed-001", 13L,
                    LocalDateTime.of(2026, 12, 1, 9, 0), LocalDateTime.of(2026, 12, 3, 18, 0), "ANNULEE");
            case 3 -> new ReservationDto(3L, "sara-002", 12L,
                    LocalDateTime.of(2026, 11, 16, 9, 0), LocalDateTime.of(2026, 11, 20, 18, 0), "CONFIRMEE");
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation inconnue");
        };
    }

    @GetMapping("/utilisateurs/{id}")
    public UtilisateurDto utilisateur(@PathVariable String id) {
        return switch (id) {
            case "ahmed-001" -> new UtilisateurDto("ahmed-001", "Ben Salah", "Ahmed", true, true);
            case "sara-002" -> new UtilisateurDto("sara-002", "Trabelsi", "Sara", true, false);
            case "karim-003" -> new UtilisateurDto("karim-003", "Jlassi", "Karim", true, true);
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur inconnu");
        };
    }
}
