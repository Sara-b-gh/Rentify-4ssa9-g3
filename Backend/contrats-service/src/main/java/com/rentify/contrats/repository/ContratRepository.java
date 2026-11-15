package com.rentify.contrats.repository;

import com.rentify.contrats.domain.Contrat;
import com.rentify.contrats.domain.StatutContrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByClientId(String clientId);

    boolean existsByVehiculeIdAndStatut(Long vehiculeId, StatutContrat statut);

    /** Contrat actif pour un véhicule à une date/heure donnée (utilisé par le service Infractions). */
    @Query("select c from Contrat c where c.vehiculeId = :vehiculeId and c.dateDebut <= :dateHeure "
            + "and coalesce(c.dateRetourReelle, c.dateFinPrevue) >= :dateHeure")
    Optional<Contrat> findActif(@Param("vehiculeId") Long vehiculeId,
                                @Param("dateHeure") LocalDateTime dateHeure);
}
