package com.rentify.flotteservice.Repository;

import com.rentify.flotteservice.Entity.PeriodeIndisponible;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PeriodeIndisponibleRepository
        extends JpaRepository<PeriodeIndisponible, UUID> {
    List<PeriodeIndisponible> findByVehiculeIdAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
            UUID vehiculeId,
            LocalDate dateFin,
            LocalDate dateDebut
    );


}