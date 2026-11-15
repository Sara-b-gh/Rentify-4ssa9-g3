package com.rentify.facturation.repository;

import com.rentify.facturation.domain.Penalite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PenaliteRepository extends JpaRepository<Penalite, UUID> {

    // Évite de créer deux fois la même pénalité si l'événement arrive en double
    boolean existsByInfractionId(UUID infractionId);
}