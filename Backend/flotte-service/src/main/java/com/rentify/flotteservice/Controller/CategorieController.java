package com.rentify.flotteservice.Controller;

import com.rentify.flotteservice.Entity.Categorie;
import com.rentify.flotteservice.Service.ICategorieService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategorieController {

    private final ICategorieService categorieService;

    @PostMapping
    public Categorie ajouterCategorie(@RequestBody Categorie categorie) {
        return categorieService.ajouterCategorie(categorie);
    }

    @PutMapping
    public Categorie modifierCategorie(@RequestBody Categorie categorie) {
        return categorieService.modifierCategorie(categorie);
    }

    @GetMapping
    public List<Categorie> afficherToutesCategories() {
        return categorieService.afficherToutesCategories();
    }

    @GetMapping("/{id}")
    public Categorie afficherCategorieById(@PathVariable UUID id) {
        return categorieService.afficherCategorieById(id);
    }

    @DeleteMapping("/{id}")
    public void supprimerCategorie(@PathVariable UUID id) {
        categorieService.supprimerCategorie(id);
    }
}