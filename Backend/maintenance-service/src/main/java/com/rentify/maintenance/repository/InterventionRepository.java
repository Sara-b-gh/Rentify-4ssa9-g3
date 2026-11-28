package com.rentify.maintenance.repository;

import com.rentify.maintenance.domain.Intervention;
import com.rentify.maintenance.domain.StatutIntervention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    List<Intervention> findByVehiculeId(Long vehiculeId);

    List<Intervention> findByVehiculeIdAndStatutAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
            Long vehiculeId,
            StatutIntervention statut,
            LocalDate dateFin,
            LocalDate dateDebut
    );
}
