package com.rentify.facturation.service;

import com.rentify.facturation.client.*;
import com.rentify.facturation.domain.*;
import com.rentify.facturation.dto.*;
import com.rentify.facturation.exception.*;
import com.rentify.facturation.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Tests unitaires de la création de facture (services Contrats et Réservation simulés). */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FactureServiceTest {

    @Mock FactureRepository repository;
    @Mock PaiementRepository paiementRepository;
    @Mock ContratClient contratClient;
    @Mock ReservationClient reservationClient;
    @InjectMocks FactureService service;

    @Test
    void creer_ok_reprendClientEtMontant() {
        when(contratClient.trouver(1L)).thenReturn(new ContratDto(1L, 5L, 9L, "CLOTURE"));
        when(reservationClient.trouver(5L)).thenReturn(new ReservationDto(5L, 200.0));
        when(repository.save(any(Facture.class))).thenAnswer(i -> i.getArgument(0));

        Facture f = service.creer(new FactureRequest(1L));

        assertEquals(9L, f.getClientId().longValue());
        assertEquals(200.0, f.getMontantTotal().doubleValue());
        assertEquals(StatutFacture.EMISE, f.getStatut());
    }

    @Test
    void creer_contratNonCloture_leve409() {
        when(contratClient.trouver(1L)).thenReturn(new ContratDto(1L, 5L, 9L, "EN_COURS"));

        assertThrows(ConflictException.class, () -> service.creer(new FactureRequest(1L)));
        verify(repository, never()).save(any());
    }

    @Test
    void creer_contratDejaFacture_leve409SansAppelDistant() {
        when(repository.existsByContratId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.creer(new FactureRequest(1L)));
        verifyNoInteractions(contratClient);
    }
}
