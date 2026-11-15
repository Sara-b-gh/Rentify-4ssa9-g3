package com.rentify.facturation.service;

import com.rentify.facturation.domain.Facture;
import com.rentify.facturation.domain.Paiement;
import com.rentify.facturation.domain.StatutFacture;
import com.rentify.facturation.dto.FactureResponse;
import com.rentify.facturation.dto.PaiementRequest;
import com.rentify.facturation.exception.FactureIntrouvableException;
import com.rentify.facturation.exception.OperationInvalideException;
import com.rentify.facturation.repository.FactureRepository;
import com.rentify.facturation.repository.PenaliteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FactureServiceTest {

    @Mock
    private FactureRepository factureRepository;

    @Mock
    private PenaliteRepository penaliteRepository;

    @InjectMocks
    private FactureService factureService;

    // Crée une facture de 300 non payée
    private Facture factureDe300() {
        Facture facture = new Facture();
        facture.setId(UUID.randomUUID());
        facture.setContratId(UUID.randomUUID());
        facture.setMontantLocation(300.0);
        facture.setMontantTotal(300.0);
        facture.setDateEmission(LocalDate.now());
        return facture;
    }

    @Test
    void paiementPartiel_passeLeStatutAPartiellementPayee() {
        Facture facture = factureDe300();
        when(factureRepository.findById(facture.getId())).thenReturn(Optional.of(facture));

        FactureResponse reponse = factureService.enregistrerPaiement(
                facture.getId(), new PaiementRequest(100, "CARTE"));

        assertEquals(StatutFacture.PARTIELLEMENT_PAYEE, reponse.statut());
        assertEquals(100.0, reponse.montantPaye(), 0.001);
    }

    @Test
    void paiementDuTotal_passeLeStatutAPayee() {
        Facture facture = factureDe300();
        when(factureRepository.findById(facture.getId())).thenReturn(Optional.of(facture));

        FactureResponse reponse = factureService.enregistrerPaiement(
                facture.getId(), new PaiementRequest(300, "ESPECES"));

        assertEquals(StatutFacture.PAYEE, reponse.statut());
    }

    @Test
    void paiementSuperieurAuReste_leveUneErreur409() {
        Facture facture = factureDe300();
        when(factureRepository.findById(facture.getId())).thenReturn(Optional.of(facture));

        assertThrows(OperationInvalideException.class, () ->
                factureService.enregistrerPaiement(facture.getId(), new PaiementRequest(5000, "CARTE")));
    }

    @Test
    void paiementSurFactureDejaPayee_leveUneErreur409() {
        Facture facture = factureDe300();
        facture.setStatut(StatutFacture.PAYEE);
        when(factureRepository.findById(facture.getId())).thenReturn(Optional.of(facture));

        assertThrows(OperationInvalideException.class, () ->
                factureService.enregistrerPaiement(facture.getId(), new PaiementRequest(10, "CARTE")));
    }

    @Test
    void factureInconnue_leveUneErreur404() {
        UUID id = UUID.randomUUID();
        when(factureRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(FactureIntrouvableException.class, () -> factureService.consulter(id));
    }

    @Test
    void remboursementSansPaiement_leveUneErreur409() {
        Facture facture = factureDe300();
        when(factureRepository.findById(facture.getId())).thenReturn(Optional.of(facture));

        assertThrows(OperationInvalideException.class, () -> factureService.rembourser(facture.getId()));
    }

    @Test
    void remboursementApresPaiement_passeLeStatutARemboursee() {
        Facture facture = factureDe300();
        Paiement paiement = new Paiement();
        paiement.setMontant(300);
        paiement.setMode("CARTE");
        paiement.setDatePaiement(LocalDateTime.now());
        facture.ajouterPaiement(paiement);
        facture.setStatut(StatutFacture.PAYEE);
        when(factureRepository.findById(facture.getId())).thenReturn(Optional.of(facture));

        FactureResponse reponse = factureService.rembourser(facture.getId());

        assertEquals(StatutFacture.REMBOURSEE, reponse.statut());
    }

    @Test
    void creerFacture_evenementRecuDeuxFois_necreePasDeDoublon() {
        Facture existante = factureDe300();
        when(factureRepository.findByContratId(existante.getContratId()))
                .thenReturn(Optional.of(existante));

        factureService.creerFacture(existante.getContratId(), 300.0);

        verify(factureRepository, never()).save(any(Facture.class));
    }

    @Test
    void ajouterPenalite_augmenteLeTotalDeLaFacture() {
        Facture facture = factureDe300();
        UUID infractionId = UUID.randomUUID();
        when(factureRepository.findByContratId(facture.getContratId())).thenReturn(Optional.of(facture));
        when(penaliteRepository.existsByInfractionId(infractionId)).thenReturn(false);

        FactureResponse reponse = factureService.ajouterPenalite(
                facture.getContratId(), infractionId, "conducteur-1", 50.0);

        assertEquals(350.0, reponse.montantTotal(), 0.001);
        assertEquals(1, reponse.penalites().size());
    }

    @Test
    void ajouterPenalite_memeInfractionDeuxFois_neCreePasDeDoublon() {
        Facture facture = factureDe300();
        UUID infractionId = UUID.randomUUID();
        when(factureRepository.findByContratId(facture.getContratId())).thenReturn(Optional.of(facture));
        when(penaliteRepository.existsByInfractionId(infractionId)).thenReturn(true);

        FactureResponse reponse = factureService.ajouterPenalite(
                facture.getContratId(), infractionId, "conducteur-1", 50.0);

        assertEquals(0, reponse.penalites().size());
        assertEquals(300.0, reponse.montantTotal(), 0.001);
    }

    @Test
    void ajouterPenalite_surFactureRemboursee_leveUneErreur409() {
        Facture facture = factureDe300();
        facture.setStatut(StatutFacture.REMBOURSEE);
        UUID infractionId = UUID.randomUUID();
        when(factureRepository.findByContratId(facture.getContratId())).thenReturn(Optional.of(facture));
        when(penaliteRepository.existsByInfractionId(infractionId)).thenReturn(false);

        assertThrows(OperationInvalideException.class, () ->
                factureService.ajouterPenalite(facture.getContratId(), infractionId, "conducteur-1", 50.0));
    }
}