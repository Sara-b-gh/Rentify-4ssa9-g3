package com.rentify.maintenance.repository;

import com.rentify.maintenance.domain.Intervention;
import com.rentify.maintenance.domain.StatutIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    List<Intervention> findByVehiculeId(Long vehiculeId);

    List<Intervention> findByVehiculeIdAndStatutAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
            Long vehiculeId,
            StatutIntervention statut,
            LocalDate dateFin,
            LocalDate dateDebut
    );
}
