package com.rentify.maintenance.service;

import com.rentify.maintenance.domain.Intervention;
import com.rentify.maintenance.repository.InterventionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InterventionService {

    private final InterventionRepository interventionRepository;

    public InterventionService(InterventionRepository interventionRepository) {
        this.interventionRepository = interventionRepository;
    }

    public Intervention planifierIntervention(Intervention intervention) {
        intervention.planifier();
        return interventionRepository.save(intervention);
    }

    public List<Intervention> getAllInterventions() {
        return interventionRepository.findAll();
    }

    public List<Intervention> getInterventionsByVehicule(UUID vehiculeId) {
        return interventionRepository.findByVehiculeId(vehiculeId);
    }

    public Optional<Intervention> terminerIntervention(UUID id, double cout) {
        return interventionRepository.findById(id).map(interv -> {
            interv.terminer(cout);
            return interventionRepository.save(interv);
        });
    }
}
