package com.rentify.infractions.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests unitaires des règles métier de l'agrégat Infraction (sans Spring ni base). */
class InfractionTest {

    private Infraction nouvelleInfraction() {
        return new Infraction("123TU4567", UUID.randomUUID(), LocalDateTime.of(2026, 11, 10, 14, 30), "Tunis", 60.0);
    }

    @Test
    void uneNouvelleInfractionEstDeclareeSansConducteur() {
        Infraction infraction = nouvelleInfraction();
        assertEquals(StatutInfraction.DECLAREE, infraction.getStatut());
        assertNull(infraction.getIdentification());
    }

    @Test
    void leMontantDoitEtrePositif() {
        assertThrows(IllegalArgumentException.class, () ->
                new Infraction("123TU4567", UUID.randomUUID(), LocalDateTime.now(), "Tunis", 0));
    }

    @Test
    void identifierLeConducteurCreeLIdentification() {
        Infraction infraction = nouvelleInfraction();
        UUID contratId = UUID.randomUUID();
        infraction.identifierConducteur(contratId, "ahmed-001");
        assertEquals(StatutInfraction.CONDUCTEUR_IDENTIFIE, infraction.getStatut());
        assertNotNull(infraction.getIdentification());
        assertEquals(contratId, infraction.getIdentification().getContratId());
        assertEquals("ahmed-001", infraction.getIdentification().getConducteurId());
    }

    @Test
    void onNePeutPasIdentifierDeuxFois() {
        Infraction infraction = nouvelleInfraction();
        infraction.identifierConducteur(UUID.randomUUID(), "ahmed-001");
        assertThrows(IllegalStateException.class, () -> infraction.identifierConducteur(UUID.randomUUID(), "sara-002"));
    }

    @Test
    void sansContratActifLInfractionEstNonImputable() {
        Infraction infraction = nouvelleInfraction();
        infraction.marquerNonImputable();
        assertEquals(StatutInfraction.NON_IMPUTABLE, infraction.getStatut());
    }

    @Test
    void lAgentPeutCorrigerLeConducteur() {
        Infraction infraction = nouvelleInfraction();
        infraction.identifierConducteur(UUID.randomUUID(), "ahmed-001");
        UUID nouveauContrat = UUID.randomUUID();
        infraction.corrigerConducteur(nouveauContrat, "sara-002");
        assertEquals("sara-002", infraction.getIdentification().getConducteurId());
        assertEquals(nouveauContrat, infraction.getIdentification().getContratId());
    }
}
