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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Tests unitaires des pénalités (service Infractions simulé par un mock). */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PenaliteServiceTest {

    @Mock PenaliteRepository repository;
    @Mock FactureRepository factureRepository;
    @Mock InfractionClient infractionClient;
    @InjectMocks PenaliteService service;

    private final PenaliteRequest req = new PenaliteRequest(1L, 1L);

    private Facture facture() {
        Facture f = new Facture();
        f.setId(1L);
        f.setContratId(1L);
        f.setMontantTotal(250.0);
        f.setStatut(StatutFacture.EMISE);
        return f;
    }

    @Test
    void creer_ok_ajouteLeMontantALaFacture() {
        Facture f = facture();
        when(factureRepository.findById(1L)).thenReturn(Optional.of(f));
        when(infractionClient.trouver(1L)).thenReturn(new InfractionDto(1L, 60.0, "IDENTIFIEE"));
        when(infractionClient.parInfraction(1L)).thenReturn(new IdentificationDto(1L, 1L, 1L, 10L));
        when(repository.save(any(Penalite.class))).thenAnswer(i -> i.getArgument(0));

        Penalite p = service.creer(req);

        assertEquals(60.0, p.getMontant().doubleValue());
        assertEquals(310.0, f.getMontantTotal().doubleValue());
    }

    @Test
    void creer_infractionNonIdentifiee_leve409() {
        when(factureRepository.findById(1L)).thenReturn(Optional.of(facture()));
        when(infractionClient.trouver(1L)).thenReturn(new InfractionDto(1L, 60.0, "NOUVELLE"));

        assertThrows(ConflictException.class, () -> service.creer(req));
        verify(repository, never()).save(any());
    }

    @Test
    void creer_autreContrat_leve409() {
        when(factureRepository.findById(1L)).thenReturn(Optional.of(facture()));
        when(infractionClient.trouver(1L)).thenReturn(new InfractionDto(1L, 60.0, "IDENTIFIEE"));
        when(infractionClient.parInfraction(1L)).thenReturn(new IdentificationDto(1L, 1L, 2L, 10L));

        assertThrows(ConflictException.class, () -> service.creer(req));
    }

    @Test
    void creer_dejaFacturee_leve409() {
        when(factureRepository.findById(1L)).thenReturn(Optional.of(facture()));
        when(repository.existsByInfractionId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.creer(req));
        verifyNoInteractions(infractionClient);
    }
}
