package com.rentify.facturation.service;

import com.rentify.facturation.domain.*;
import com.rentify.facturation.dto.*;
import com.rentify.facturation.exception.*;
import com.rentify.facturation.repository.*;
import com.rentify.facturation.client.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

/** Logique métier : Paiement partiel ou total d'une facture. */
@Service
public class PaiementService {

    private final PaiementRepository repository;
    private final FactureRepository factureRepository;

    public PaiementService(PaiementRepository repository, FactureRepository factureRepository) {
        this.repository = repository;
        this.factureRepository = factureRepository;
    }

    @Transactional(readOnly = true)
    public List<Paiement> lister() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Paiement trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paiement introuvable : " + id));
    }


    @Transactional
    public Paiement creer(PaiementRequest req) {
        verifier(req, null);
        Paiement e = new Paiement();
        appliquer(e, req);
        Paiement saved = repository.save(e);
        // Facture soldée => statut PAYEE
        factureRepository.findById(req.factureId()).ifPresent(f -> {
            if (repository.totalPaye(f.getId()) >= f.getMontantTotal() - 0.001) {
                f.setStatut(StatutFacture.PAYEE);
                factureRepository.save(f);
            }
        });
        return saved;
    }

    private void appliquer(Paiement e, PaiementRequest req) {
        e.setFactureId(req.factureId());
        e.setMontant(req.montant());
        e.setMode(req.mode());
    }

    /** Règles métier (400 / 404 / 409). "courant" vaut null à la création. */
    private void verifier(PaiementRequest req, Paiement courant) {
        Facture f = factureRepository.findById(req.factureId())
                .orElseThrow(() -> new NotFoundException("Facture introuvable : " + req.factureId()));
        if (f.getStatut() == StatutFacture.PAYEE) {
            throw new ConflictException("La facture est déjà payée");
        }
        double reste = f.getMontantTotal() - repository.totalPaye(f.getId());
        if (req.montant() > reste + 0.001) {
            throw new ConflictException("Le montant dépasse le reste à payer (" + reste + ")");
        }
    }

    @Transactional(readOnly = true)
    public List<Paiement> listerParFacture(Long factureId) {
        return repository.findByFactureId(factureId);
    }
}
