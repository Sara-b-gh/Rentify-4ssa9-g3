package com.rentify.facturation.repository;

import com.rentify.facturation.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.*;
import java.util.*;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    List<Paiement> findByFactureId(Long factureId);

    @Query("select coalesce(sum(p.montant), 0.0) from Paiement p where p.factureId = :factureId")
    double totalPaye(@Param("factureId") Long factureId);
}
