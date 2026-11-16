package com.rentify.maintenance.repository;

import com.rentify.maintenance.domain.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, UUID> {
    List<Intervention> findByVehiculeId(UUID vehiculeId);
}
