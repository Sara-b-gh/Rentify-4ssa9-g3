package com.rentify.flotteservice.Service;

import com.rentify.flotteservice.Entity.Categorie;

import java.util.List;
import java.util.UUID;

public interface ICategorieService {

    Categorie ajouterCategorie(Categorie categorie);

    Categorie modifierCategorie(Categorie categorie);

    List<Categorie> afficherToutesCategories();

    Categorie afficherCategorieById(UUID idCategorie);

    void supprimerCategorie(UUID idCategorie);
}