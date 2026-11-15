package com.rentify.facturation.repository;

import com.rentify.facturation.domain.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FactureRepository extends JpaRepository<Facture, UUID> {

    // Sert à GET /api/factures/par-contrat/{contratId}
    Optional<Facture> findByContratId(UUID contratId);

    boolean existsByContratId(UUID contratId);
}