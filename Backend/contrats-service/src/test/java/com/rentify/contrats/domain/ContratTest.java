package com.rentify.contrats.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContratTest {

    private Contrat contratAhmed() {
        Contrat c = new Contrat();
        c.setDateDebut(LocalDateTime.of(2026, 12, 10, 9, 0));
        c.setDateFinPrevue(LocalDateTime.of(2026, 12, 15, 18, 0));
        return c;
    }

    @Test
    void estActifPendantLaLocation() {
        assertTrue(contratAhmed().estActifA(LocalDateTime.of(2026, 12, 12, 14, 30)));
    }

    @Test
    void neSEstPasActifEnDehorsDeLaLocation() {
        assertFalse(contratAhmed().estActifA(LocalDateTime.of(2026, 12, 20, 10, 0)));
    }
}
