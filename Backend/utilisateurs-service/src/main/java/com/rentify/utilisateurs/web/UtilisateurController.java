package com.rentify.utilisateurs.web;

import com.rentify.utilisateurs.domain.Utilisateur;
import com.rentify.utilisateurs.repository.UtilisateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping
    public ResponseEntity<Utilisateur> creerCompte(@RequestBody Utilisateur utilisateur) {
        Utilisateur saved = utilisateurRepository.save(utilisateur);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateur(@PathVariable UUID id) {
        return utilisateurRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Void> changerStatut(@PathVariable UUID id) {
        return utilisateurRepository.findById(id).map(user -> {
            user.desactiver();
            utilisateurRepository.save(user);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
