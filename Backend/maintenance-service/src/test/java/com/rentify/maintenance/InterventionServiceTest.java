package com.rentify.maintenance;

import com.rentify.maintenance.domain.Intervention;
import com.rentify.maintenance.domain.StatutIntervention;
import com.rentify.maintenance.repository.InterventionRepository;
import com.rentify.maintenance.service.InterventionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterventionServiceTest {

    @Mock
    private InterventionRepository interventionRepository;

    private InterventionService interventionService;

    @BeforeEach
    void setUp() {
        interventionService = new InterventionService(interventionRepository, "http://localhost:8081");
    }

    @Test
    void planifier_devraitRetournerErreurSiDatesInvalides() {
        LocalDate debut = LocalDate.of(2026, 12, 10);
        LocalDate fin = LocalDate.of(2026, 12, 5);

        assertThrows(ResponseStatusException.class, () ->
                interventionService.planifier(1L, 2L, "Vidange", debut, fin)
        );
    }

    @Test
    void terminer_devraitMettreAJourLeCoutEtLeStatut() {
        Intervention intervention = Intervention.planifier(
                1L,
                2L,
                "Vidange",
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 11)
        );
        intervention.setId(1L);

        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

        Intervention resultat = interventionService.terminer(1L, 150.0);

        assertEquals(StatutIntervention.TERMINEE, resultat.getStatut());
        assertEquals(150.0, resultat.getCout());
    }

    @Test
    void terminer_devraitRetournerErreurSiInterventionIntrouvable() {
        when(interventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                interventionService.terminer(99L, 120.0)
        );
    }

    @Test
    void listerParVehicule_devraitRetournerLaListe() {
        Intervention intervention = Intervention.planifier(
                1L,
                2L,
                "Freins",
                LocalDate.of(2026, 12, 12),
                LocalDate.of(2026, 12, 13)
        );
        when(interventionRepository.findByVehiculeId(1L)).thenReturn(List.of(intervention));

        List<Intervention> liste = interventionService.listerParVehicule(1L);

        assertNotNull(liste);
        assertEquals(1, liste.size());
        assertEquals(Collections.emptyList(), interventionService.listerParVehicule(2L));
    }
}
