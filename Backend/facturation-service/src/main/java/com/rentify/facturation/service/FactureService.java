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

/** Logique métier : Facture d'un contrat de location (montantTotal = location + pénalités). */
@Service
public class FactureService {

    private final FactureRepository repository;
    private final PaiementRepository paiementRepository;
    private final ContratClient contratClient;
    private final ReservationClient reservationClient;

    public FactureService(FactureRepository repository, PaiementRepository paiementRepository, ContratClient contratClient, ReservationClient reservationClient) {
        this.repository = repository;
        this.paiementRepository = paiementRepository;
        this.contratClient = contratClient;
        this.reservationClient = reservationClient;
    }

    @Transactional(readOnly = true)
    public List<Facture> lister() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Facture trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Facture introuvable : " + id));
    }


    @Transactional
    public Facture creer(FactureRequest req) {
        verifier(req, null);
        Facture e = new Facture();
        appliquer(e, req);
        // Appels RÉELS : le contrat (Contrats) doit être clôturé ; le montant vient de la réservation (Réservation)
        ContratDto c = AppelDistant.appeler("contrats-service", "Contrat",
                () -> contratClient.trouver(req.contratId()));
        if (!"CLOTURE".equals(c.statut())) {
            throw new ConflictException("Le contrat doit être clôturé avant la facturation (statut : " + c.statut() + ")");
        }
        ReservationDto r = AppelDistant.appeler("reservation-service", "Réservation",
                () -> reservationClient.trouver(c.reservationId()));
        e.setClientId(c.clientId());
        e.setMontantTotal(r.montantEstime());
        Facture saved = repository.save(e);
        return saved;
    }

    @Transactional
    public void supprimer(Long id) {
        repository.delete(trouver(id));
    }

    private void appliquer(Facture e, FactureRequest req) {
        e.setContratId(req.contratId());
    }

    /** Règles métier (400 / 404 / 409). "courant" vaut null à la création. */
    private void verifier(FactureRequest req, Facture courant) {
        if (repository.existsByContratId(req.contratId())) {
            throw new ConflictException("Une facture existe déjà pour le contrat " + req.contratId());
        }
    }

    /** Total payé, reste à payer. */
    @Transactional(readOnly = true)
    public ResteAPayerResponse reste(Long id) {
        Facture f = trouver(id);
        double paye = paiementRepository.totalPaye(id);
        return new ResteAPayerResponse(id, f.getMontantTotal(), paye, f.getMontantTotal() - paye);
    }
}
