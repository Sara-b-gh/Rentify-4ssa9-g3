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

/** Logique métier : Pénalité (issue d'une infraction) ajoutée au montant d'une facture. */
@Service
public class PenaliteService {

    private final PenaliteRepository repository;
    private final FactureRepository factureRepository;
    private final InfractionClient infractionClient;

    public PenaliteService(PenaliteRepository repository, FactureRepository factureRepository, InfractionClient infractionClient) {
        this.repository = repository;
        this.factureRepository = factureRepository;
        this.infractionClient = infractionClient;
    }

    @Transactional(readOnly = true)
    public List<Penalite> lister() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Penalite trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pénalité introuvable : " + id));
    }


    @Transactional
    public Penalite creer(PenaliteRequest req) {
        verifier(req, null);
        Penalite e = new Penalite();
        appliquer(e, req);
        // Appels RÉELS au service Infractions : infraction identifiée et appartenant au contrat de la facture
        Facture f = factureRepository.findById(req.factureId())
                .orElseThrow(() -> new NotFoundException("Facture introuvable : " + req.factureId()));
        InfractionDto i = AppelDistant.appeler("infractions-service", "Infraction",
                () -> infractionClient.trouver(req.infractionId()));
        if (!"IDENTIFIEE".equals(i.statut())) {
            throw new ConflictException("L'infraction n'est pas encore identifiée");
        }
        IdentificationDto ident = AppelDistant.appeler("infractions-service", "Identification de l'infraction",
                () -> infractionClient.parInfraction(req.infractionId()));
        if (!ident.contratId().equals(f.getContratId())) {
            throw new ConflictException("Cette infraction concerne un autre contrat que celui de la facture");
        }
        e.setMontant(i.montant());
        Penalite saved = repository.save(e);
        // La pénalité augmente le montant total de la facture
        factureRepository.findById(req.factureId()).ifPresent(facture -> {
            facture.setMontantTotal(facture.getMontantTotal() + saved.getMontant());
            factureRepository.save(facture);
        });
        return saved;
    }

    private void appliquer(Penalite e, PenaliteRequest req) {
        e.setFactureId(req.factureId());
        e.setInfractionId(req.infractionId());
    }

    /** Règles métier (400 / 404 / 409). "courant" vaut null à la création. */
    private void verifier(PenaliteRequest req, Penalite courant) {
        Facture f = factureRepository.findById(req.factureId())
                .orElseThrow(() -> new NotFoundException("Facture introuvable : " + req.factureId()));
        if (f.getStatut() == StatutFacture.PAYEE) {
            throw new ConflictException("Impossible d'ajouter une pénalité à une facture payée");
        }
        if (repository.existsByInfractionId(req.infractionId())) {
            throw new ConflictException("Cette infraction est déjà facturée");
        }
    }
}
