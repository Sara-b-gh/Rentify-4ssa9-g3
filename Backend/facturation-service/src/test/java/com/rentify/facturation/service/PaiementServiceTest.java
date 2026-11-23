package com.rentify.facturation.service;

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

/** Tests unitaires des paiements (sans base de données). */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaiementServiceTest {

    @Mock PaiementRepository repository;
    @Mock FactureRepository factureRepository;
    @InjectMocks PaiementService service;

    private Facture facture(double total, StatutFacture statut) {
        Facture f = new Facture();
        f.setId(1L);
        f.setMontantTotal(total);
        f.setStatut(statut);
        return f;
    }

    @Test
    void creer_paiementPartiel_factureResteEmise() {
        Facture f = facture(250.0, StatutFacture.EMISE);
        when(factureRepository.findById(1L)).thenReturn(Optional.of(f));
        when(repository.totalPaye(1L)).thenReturn(0.0, 100.0);
        when(repository.save(any(Paiement.class))).thenAnswer(i -> i.getArgument(0));

        service.creer(new PaiementRequest(1L, 100.0, ModePaiement.CARTE));

        assertEquals(StatutFacture.EMISE, f.getStatut());
    }

    @Test
    void creer_paiementSoldeLaFacture_devientPayee() {
        Facture f = facture(250.0, StatutFacture.EMISE);
        when(factureRepository.findById(1L)).thenReturn(Optional.of(f));
        when(repository.totalPaye(1L)).thenReturn(0.0, 250.0);
        when(repository.save(any(Paiement.class))).thenAnswer(i -> i.getArgument(0));

        service.creer(new PaiementRequest(1L, 250.0, ModePaiement.ESPECES));

        assertEquals(StatutFacture.PAYEE, f.getStatut());
        verify(factureRepository).save(f);
    }

    @Test
    void creer_montantSuperieurAuReste_leve409() {
        when(factureRepository.findById(1L)).thenReturn(Optional.of(facture(250.0, StatutFacture.EMISE)));
        when(repository.totalPaye(1L)).thenReturn(0.0);

        assertThrows(ConflictException.class,
                () -> service.creer(new PaiementRequest(1L, 500.0, ModePaiement.CARTE)));
        verify(repository, never()).save(any());
    }

    @Test
    void creer_factureDejaPayee_leve409() {
        when(factureRepository.findById(1L)).thenReturn(Optional.of(facture(250.0, StatutFacture.PAYEE)));

        assertThrows(ConflictException.class,
                () -> service.creer(new PaiementRequest(1L, 10.0, ModePaiement.CARTE)));
    }

    @Test
    void creer_factureInconnue_leve404() {
        when(factureRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.creer(new PaiementRequest(1L, 10.0, ModePaiement.CARTE)));
    }
}
