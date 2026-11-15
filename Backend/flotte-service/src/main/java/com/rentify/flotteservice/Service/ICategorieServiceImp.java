package com.rentify.flotteservice.Service;

import com.rentify.flotteservice.Entity.Categorie;
import com.rentify.flotteservice.Repository.CategorieRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ICategorieServiceImp implements ICategorieService {

    private final CategorieRepository categorieRepository;

    @Override
    public Categorie ajouterCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    @Override
    public Categorie modifierCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    @Override
    public List<Categorie> afficherToutesCategories() {
        return categorieRepository.findAll();
    }

    @Override
    public Categorie afficherCategorieById(UUID idCategorie) {
        return categorieRepository.findById(idCategorie).orElse(null);
    }

    @Override
    public void supprimerCategorie(UUID idCategorie) {
        categorieRepository.deleteById(idCategorie);
    }
}