package com.rentify.utilisateurs.service;

import com.rentify.utilisateurs.domain.*;
import com.rentify.utilisateurs.dto.*;
import com.rentify.utilisateurs.exception.*;
import com.rentify.utilisateurs.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

/** Logique métier : Permis de conduire d'un utilisateur (0 ou 1 par utilisateur). */
@Service
public class PermisConduireService {

    private final PermisConduireRepository repository;
    private final UtilisateurRepository utilisateurRepository;

    public PermisConduireService(PermisConduireRepository repository, UtilisateurRepository utilisateurRepository) {
        this.repository = repository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public List<PermisConduire> lister() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PermisConduire trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Permis introuvable : " + id));
    }


    @Transactional
    public PermisConduire creer(PermisConduireRequest req) {
        verifier(req, null);
        PermisConduire e = new PermisConduire();
        appliquer(e, req);
        PermisConduire saved = repository.save(e);
        return saved;
    }

    @Transactional
    public PermisConduire modifier(Long id, PermisConduireRequest req) {
        PermisConduire courant = trouver(id);
        verifier(req, courant);
        appliquer(courant, req);
        return repository.save(courant);
    }

    @Transactional
    public void supprimer(Long id) {
        repository.delete(trouver(id));
    }

    private void appliquer(PermisConduire e, PermisConduireRequest req) {
        e.setUtilisateurId(req.utilisateurId());
        e.setNumero(req.numero());
        e.setDateObtention(req.dateObtention());
        e.setDateExpiration(req.dateExpiration());
    }

    /** Règles métier (400 / 404 / 409). "courant" vaut null à la création. */
    private void verifier(PermisConduireRequest req, PermisConduire courant) {
        if (!utilisateurRepository.existsById(req.utilisateurId())) {
            throw new NotFoundException("Utilisateur introuvable : " + req.utilisateurId());
        }
        if (!req.dateExpiration().isAfter(req.dateObtention())) {
            throw new BadRequestException("La date d'expiration doit être postérieure à la date d'obtention");
        }
        if (repository.existsByUtilisateurId(req.utilisateurId())
                && (courant == null || !courant.getUtilisateurId().equals(req.utilisateurId()))) {
            throw new ConflictException("Cet utilisateur a déjà un permis enregistré");
        }
    }

    /** Permis d'un utilisateur (utilisé par le service Contrats). */
    @Transactional(readOnly = true)
    public PermisConduire parUtilisateur(Long utilisateurId) {
        return repository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Aucun permis enregistré pour l'utilisateur : " + utilisateurId));
    }
}
