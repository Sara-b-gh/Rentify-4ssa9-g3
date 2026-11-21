package com.rentify.infractions.repository;

import com.rentify.infractions.domain.Infraction;
import com.rentify.infractions.domain.StatutInfraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Accès à la base infractions_db (une base propre à ce microservice). */
public interface InfractionRepository extends JpaRepository<Infraction, UUID> {

    List<Infraction> findByStatut(StatutInfraction statut);

    List<Infraction> findByVehiculeId(UUID vehiculeId);
}
