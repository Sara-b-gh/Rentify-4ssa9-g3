package com.rentify.utilisateurs.web;

import com.rentify.utilisateurs.domain.Role;
import com.rentify.utilisateurs.domain.Utilisateur;
import com.rentify.utilisateurs.dto.InscriptionRequest;
import com.rentify.utilisateurs.dto.UtilisateurRequest;
import com.rentify.utilisateurs.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** API REST Utilisateurs (sécurisée par Keycloak : voir SecurityConfig pour les rôles exigés). */
@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService service;

    public UtilisateurController(UtilisateurService service) {
        this.service = service;
    }

    /** ADMIN ou AGENT : liste complète. */
    @GetMapping
    public List<Utilisateur> lister() {
        return service.lister();
    }

    /** Utilisateur connecté : son profil, retrouvé grâce au "sub" du token. */
    @GetMapping("/moi")
    public Utilisateur moi(@AuthenticationPrincipal Jwt jwt) {
        return service.parKeycloakId(jwt.getSubject());
    }

    /** 200 ou 404 (lecture interne appelée par les autres microservices). */
    @GetMapping("/{id}")
    public Utilisateur trouver(@PathVariable Long id) {
        return service.trouver(id);
    }

    /** ADMIN : crée le compte Keycloak + le profil. 201, 400, 409, 503. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Utilisateur creer(@Valid @RequestBody UtilisateurRequest req) {
        return service.creer(req);
    }

    /** Public : inscription d'un client (rôle CLIENT). 201, 400, 409, 503. */
    @PostMapping("/inscription")
    @ResponseStatus(HttpStatus.CREATED)
    public Utilisateur inscrire(@Valid @RequestBody InscriptionRequest req) {
        return service.inscrire(req);
    }

    /** ADMIN : modifie nom, prénom, téléphone. */
    @PutMapping("/{id}")
    public Utilisateur modifier(@PathVariable Long id, @Valid @RequestBody UtilisateurRequest req) {
        return service.modifier(id, req);
    }

    /** ADMIN : change le rôle (CLIENT, AGENT, ADMIN) dans Keycloak et en base. */
    @PutMapping("/{id}/role")
    public Utilisateur changerRole(@PathVariable Long id, @RequestParam Role role) {
        return service.changerRole(id, role);
    }

    /** ADMIN : désactive le compte. 200, 404, 409 déjà désactivé. */
    @PutMapping("/{id}/desactiver")
    public Utilisateur desactiver(@PathVariable Long id) {
        return service.desactiver(id);
    }

    /** ADMIN : supprime le compte Keycloak et le profil. 204, 404. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
