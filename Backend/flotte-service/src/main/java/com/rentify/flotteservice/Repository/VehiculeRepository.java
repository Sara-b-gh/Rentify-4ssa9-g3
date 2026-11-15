package com.rentify.flotteservice.Repository;

import com.rentify.flotteservice.Entity.Vehicule;
import com.rentify.flotteservice.Entity.StatutVehicule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehiculeRepository extends JpaRepository<Vehicule, UUID> {

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    List<Vehicule> findByStatut(StatutVehicule statut);
}