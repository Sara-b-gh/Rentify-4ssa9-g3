package com.rentify.flotteservice.Service;

import com.rentify.flotteservice.Entity.Vehicule;
import com.rentify.flotteservice.Entity.StatutVehicule;
import com.rentify.flotteservice.Repository.VehiculeRepository;
import java.time.LocalDate;
import com.rentify.flotteservice.Repository.PeriodeIndisponibleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class IVehiculeServiceImp implements IVehiculeService {

    private final VehiculeRepository vehiculeRepository;

    @Override
    public Vehicule ajouterVehicule(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule modifierVehicule(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public List<Vehicule> afficherToutesVehicule() {
        return vehiculeRepository.findAll();
    }

    @Override
    public Vehicule afficherVehiculeById(UUID idVehicule) {
        return vehiculeRepository.findById(idVehicule).orElse(null);
    }

    @Override
    public void supprimerVehicule(UUID idVehicule) {
        vehiculeRepository.deleteById(idVehicule);
    }

    @Override
    public Vehicule rechercherParImmatriculation(String immatriculation) {
        return vehiculeRepository.findByImmatriculation(immatriculation)
                .orElse(null);
    }

    @Override
    public Vehicule modifierStatut(UUID idVehicule, StatutVehicule statut) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElse(null);

        if (vehicule == null) {
            return null;
        }

        vehicule.setStatut(statut);
        return vehiculeRepository.save(vehicule);
    }

    private final PeriodeIndisponibleRepository periodeRepository;
    @Override
    public boolean verifierDisponibilite(UUID id, LocalDate debut, LocalDate fin) {

        Vehicule vehicule = vehiculeRepository.findById(id).orElse(null);

        if (vehicule == null || vehicule.getStatut() != StatutVehicule.DISPONIBLE) {
            return false;
        }

        return periodeRepository
                .findByVehiculeIdAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
                        id, fin, debut
                ).isEmpty();
    }

    @Override
    public List<Vehicule> afficherVehiculesDisponibles(
            LocalDate debut, LocalDate fin) {

        return vehiculeRepository.findByStatut(StatutVehicule.DISPONIBLE)
                .stream()
                .filter(v -> verifierDisponibilite(v.getId(), debut, fin))
                .toList();
    }
}
