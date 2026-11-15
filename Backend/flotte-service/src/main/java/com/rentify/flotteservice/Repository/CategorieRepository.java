package com.rentify.flotteservice.Repository;

import com.rentify.flotteservice.Entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategorieRepository extends JpaRepository<Categorie, UUID> {
}