package com.rentify.facturation.service;

import com.rentify.facturation.domain.Facture;
import com.rentify.facturation.domain.Paiement;
import com.rentify.facturation.domain.Penalite;
import com.rentify.facturation.domain.StatutFacture;
import com.rentify.facturation.dto.FactureResponse;
import com.rentify.facturation.dto.PaiementRequest;
import com.rentify.facturation.exception.FactureIntrouvableException;
import com.rentify.facturation.exception.OperationInvalideException;
import com.rentify.facturation.repository.FactureRepository;
import com.rentify.facturation.repository.PenaliteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FactureService {

    // Les montants sont des double : on compare avec une petite tolérance
    private static final double TOLERANCE = 0.001;

    private final FactureRepository factureRepository;
    private final PenaliteRepository penaliteRepository;

    public FactureService(FactureRepository factureRepository, PenaliteRepository penaliteRepository) {
        this.factureRepository = factureRepository;
        this.penaliteRepository = penaliteRepository;
    }

    // GET /api/factures/{id}
    @Transactional(readOnly = true)
    public FactureResponse consulter(UUID id) {
        return FactureMapper.toResponse(trouver(id));
    }

    // GET /api/factures/par-contrat/{contratId}
    @Transactional(readOnly = true)
    public FactureResponse consulterParContrat(UUID contratId) {
        Facture facture = factureRepository.findByContratId(contratId)
                .orElseThrow(() -> new FactureIntrouvableException(
                        "Aucune facture pour le contrat " + contratId));
        return FactureMapper.toResponse(facture);
    }

    // Appelée à la réception de l'événement ContratCloture
    @Transactional
    public FactureResponse creerFacture(UUID contratId, double montantLocation) {
        // Si l'événement arrive deux fois, on ne crée pas une deuxième facture
        return factureRepository.findByContratId(contratId)
                .map(FactureMapper::toResponse)
                .orElseGet(() -> {
                    Facture facture = new Facture();
                    facture.setContratId(contratId);
                    facture.setMontantLocation(montantLocation);
                    facture.setMontantTotal(montantLocation);
                    facture.setDateEmission(LocalDate.now());
                    facture.setStatut(StatutFacture.EMISE);
                    return FactureMapper.toResponse(factureRepository.save(facture));
                });
    }

    // POST /api/factures/{id}/paiements
    @Transactional
    public FactureResponse enregistrerPaiement(UUID factureId, PaiementRequest request) {
        Facture facture = trouver(factureId);

        if (facture.getStatut() == StatutFacture.PAYEE) {
            throw new OperationInvalideException("La facture est déjà payée");
        }
        if (facture.getStatut() == StatutFacture.REMBOURSEE) {
            throw new OperationInvalideException("La facture a été remboursée");
        }

        double resteAPayer = facture.getMontantTotal() - facture.montantPaye();
        if (request.montant() > resteAPayer + TOLERANCE) {
            throw new OperationInvalideException(
                    "Le montant dépasse le reste à payer (" + resteAPayer + ")");
        }

        Paiement paiement = new Paiement();
        paiement.setMontant(request.montant());
        paiement.setMode(request.mode());
        paiement.setDatePaiement(LocalDateTime.now());
        facture.ajouterPaiement(paiement);

        boolean totalementPayee = facture.montantPaye() >= facture.getMontantTotal() - TOLERANCE;
        facture.setStatut(totalementPayee ? StatutFacture.PAYEE : StatutFacture.PARTIELLEMENT_PAYEE);

        // flush : écrit en base pour que le paiement ait son id avant la réponse
        factureRepository.flush();
        return FactureMapper.toResponse(facture);
    }

    // POST /api/factures/{id}/remboursement
    @Transactional
    public FactureResponse rembourser(UUID factureId) {
        Facture facture = trouver(factureId);

        if (facture.getStatut() == StatutFacture.REMBOURSEE) {
            throw new OperationInvalideException("La facture est déjà remboursée");
        }
        if (facture.getPaiements().isEmpty()) {
            throw new OperationInvalideException("Aucun paiement à rembourser");
        }

        facture.setStatut(StatutFacture.REMBOURSEE);
        return FactureMapper.toResponse(facture);
    }

    // Appelée à la réception de ConducteurIdentifie
    @Transactional
    public FactureResponse ajouterPenalite(UUID contratId, UUID infractionId,
                                           String conducteurId, double montant) {
        Facture facture = factureRepository.findByContratId(contratId)
                .orElseThrow(() -> new FactureIntrouvableException(
                        "Aucune facture pour le contrat " + contratId));

        // Événement reçu deux fois : on ne crée pas une deuxième pénalité
        if (penaliteRepository.existsByInfractionId(infractionId)) {
            return FactureMapper.toResponse(facture);
        }
        if (facture.getStatut() == StatutFacture.REMBOURSEE) {
            throw new OperationInvalideException("La facture a été remboursée");
        }

        Penalite penalite = new Penalite();
        penalite.setInfractionId(infractionId);
        penalite.setConducteurId(conducteurId);
        penalite.setMontant(montant);
        facture.ajouterPenalite(penalite);

        // Le total a augmenté : on recalcule le statut
        double paye = facture.montantPaye();
        if (paye >= facture.getMontantTotal() - TOLERANCE) {
            facture.setStatut(StatutFacture.PAYEE);
        } else if (paye > 0) {
            facture.setStatut(StatutFacture.PARTIELLEMENT_PAYEE);
        } else {
            facture.setStatut(StatutFacture.EMISE);
        }

        factureRepository.flush();
        return FactureMapper.toResponse(facture);
    }

    private Facture trouver(UUID id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new FactureIntrouvableException("Facture introuvable : " + id));
    }
}