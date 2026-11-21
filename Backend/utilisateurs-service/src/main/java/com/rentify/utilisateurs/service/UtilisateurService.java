package com.rentify.utilisateurs.service;

import com.rentify.utilisateurs.domain.Role;
import com.rentify.utilisateurs.domain.Utilisateur;
import com.rentify.utilisateurs.dto.InscriptionRequest;
import com.rentify.utilisateurs.dto.UtilisateurRequest;
import com.rentify.utilisateurs.exception.BadRequestException;
import com.rentify.utilisateurs.exception.ConflictException;
import com.rentify.utilisateurs.exception.NotFoundException;
import com.rentify.utilisateurs.keycloak.KeycloakAdminClient;
import com.rentify.utilisateurs.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Logique métier Utilisateurs : Keycloak gère le compte (login, mot de passe, rôle),
 * cette base garde le profil métier et le lien par keycloakId.
 */
@Service
public class UtilisateurService {

    private final UtilisateurRepository repository;
    private final KeycloakAdminClient keycloak;

    public UtilisateurService(UtilisateurRepository repository, KeycloakAdminClient keycloak) {
        this.repository = repository;
        this.keycloak = keycloak;
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> lister() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Utilisateur trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable : " + id));
    }

    /** Profil de l'utilisateur connecté (sub du token = keycloakId). */
    @Transactional(readOnly = true)
    public Utilisateur parKeycloakId(String keycloakId) {
        return repository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Aucun profil lié à ce compte Keycloak"));
    }

    /** Crée le compte Keycloak puis le profil ; 400 sans mot de passe, 409 email déjà pris, 503 Keycloak arrêté. */
    @Transactional
    public Utilisateur creer(UtilisateurRequest req) {
        if (req.motDePasse() == null || req.motDePasse().isBlank()) {
            throw new BadRequestException("Le mot de passe est obligatoire");
        }
        if (repository.existsByEmailIgnoreCase(req.email())) {
            throw new ConflictException("Un utilisateur avec cet email existe déjà : " + req.email());
        }
        String keycloakId = keycloak.creerUtilisateur(
                req.email(), req.prenom(), req.nom(), req.motDePasse(), req.role().name());

        Utilisateur u = new Utilisateur();
        u.setKeycloakId(keycloakId);
        u.setNom(req.nom());
        u.setPrenom(req.prenom());
        u.setEmail(req.email());
        u.setTelephone(req.telephone());
        u.setRole(req.role());
        try {
            return repository.save(u);
        } catch (RuntimeException e) {
            keycloak.supprimer(keycloakId);          // pas de compte Keycloak orphelin
            throw e;
        }
    }

    /** Inscription libre : toujours le rôle CLIENT. */
    @Transactional
    public Utilisateur inscrire(InscriptionRequest req) {
        return creer(new UtilisateurRequest(req.nom(), req.prenom(), req.email(), req.telephone(),
                Role.CLIENT, req.motDePasse()));
    }

    /** Modifie nom, prénom, téléphone (l'email = identifiant de connexion ne change pas ; le rôle a son propre endpoint). */
    @Transactional
    public Utilisateur modifier(Long id, UtilisateurRequest req) {
        Utilisateur u = trouver(id);
        if (!u.getEmail().equalsIgnoreCase(req.email())) {
            throw new BadRequestException("L'email ne peut pas être modifié");
        }
        u.setNom(req.nom());
        u.setPrenom(req.prenom());
        u.setTelephone(req.telephone());
        return repository.save(u);
    }

    /** Change le rôle dans Keycloak puis dans le profil. */
    @Transactional
    public Utilisateur changerRole(Long id, Role role) {
        Utilisateur u = trouver(id);
        if (u.getKeycloakId() != null) {
            keycloak.changerRole(u.getKeycloakId(), role.name());
        }
        u.setRole(role);
        return repository.save(u);
    }

    /** Désactive le compte (Keycloak refuse alors la connexion) ; 409 si déjà désactivé. */
    @Transactional
    public Utilisateur desactiver(Long id) {
        Utilisateur u = trouver(id);
        if (!Boolean.TRUE.equals(u.getActif())) {
            throw new ConflictException("Le compte est déjà désactivé");
        }
        if (u.getKeycloakId() != null) {
            keycloak.activer(u.getKeycloakId(), false);
        }
        u.setActif(false);
        return repository.save(u);
    }

    @Transactional
    public void supprimer(Long id) {
        Utilisateur u = trouver(id);
        if (u.getKeycloakId() != null) {
            keycloak.supprimer(u.getKeycloakId());
        }
        repository.delete(u);
    }
}
