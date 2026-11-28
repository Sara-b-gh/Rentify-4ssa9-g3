package com.rentify.maintenance.service;

import com.rentify.maintenance.domain.Intervention;
import com.rentify.maintenance.domain.StatutIntervention;
import com.rentify.maintenance.repository.InterventionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InterventionService {

    private final InterventionRepository interventionRepository;
    private final RestTemplate restTemplate;
    private final String flotteBaseUrl;

    public InterventionService(InterventionRepository interventionRepository,
                               @Value("${rentify.flotte.base-url:http://localhost:8081}") String flotteBaseUrl) {
        this.interventionRepository = interventionRepository;
        this.flotteBaseUrl = flotteBaseUrl;
        this.restTemplate = new RestTemplate();
    }

    public Intervention planifier(Long vehiculeId,
                                Long technicienId,
                                String type,
                                LocalDate dateDebut,
                                LocalDate dateFin) {
        if (dateDebut == null || dateFin == null || dateFin.isBefore(dateDebut)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dates d'intervention invalides");
        }

        verifierVehiculeExiste(vehiculeId);

        List<Intervention> conflits = interventionRepository
                .findByVehiculeIdAndStatutAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
                        vehiculeId,
                        StatutIntervention.PLANIFIEE,
                        dateFin,
                        dateDebut
                );
        if (!conflits.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflit avec une intervention existante");
        }

        creerPeriodeIndisponibleDansFlotte(vehiculeId, dateDebut, dateFin);

        Intervention intervention = Intervention.planifier(vehiculeId, technicienId, type, dateDebut, dateFin);
        return interventionRepository.save(intervention);
    }

    @Transactional(readOnly = true)
    public List<Intervention> listerParVehicule(Long vehiculeId) {
        return interventionRepository.findByVehiculeId(vehiculeId);
    }

    public Intervention terminer(Long id, Double cout) {
        if (cout == null || cout < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cout invalide");
        }

        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intervention introuvable"));

        if (intervention.getStatut() == StatutIntervention.TERMINEE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Intervention deja terminee");
        }

        intervention.terminer(cout);
        return interventionRepository.save(intervention);
    }

    private void verifierVehiculeExiste(Long vehiculeId) {
        try {
            restTemplate.getForEntity(flotteBaseUrl + "/api/vehicules/" + vehiculeId, String.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicule introuvable dans Flotte");
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service Flotte indisponible");
        }
    }

    private void creerPeriodeIndisponibleDansFlotte(Long vehiculeId, LocalDate dateDebut, LocalDate dateFin) {
        try {
            Map<String, Object> payload = Map.of(
                    "vehiculeId", vehiculeId,
                    "dateDebut", dateDebut.toString(),
                    "dateFin", dateFin.toString(),
                    "motif", "MAINTENANCE"
            );
            restTemplate.postForEntity(flotteBaseUrl + "/api/periodes-indisponibles", payload, String.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicule introuvable dans Flotte");
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service Flotte indisponible");
        }
    }
}
