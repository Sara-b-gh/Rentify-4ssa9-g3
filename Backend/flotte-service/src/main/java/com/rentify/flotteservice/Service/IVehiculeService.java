package com.rentify.flotteservice.Service;

import com.rentify.flotteservice.Entity.Vehicule;
import com.rentify.flotteservice.Entity.StatutVehicule;
import java.time.LocalDate;

import java.util.List;
import java.util.UUID;
public interface IVehiculeService {

    Vehicule ajouterVehicule(Vehicule vehicule);

    Vehicule modifierVehicule(Vehicule vehicule);

    List<Vehicule> afficherToutesVehicule();

    Vehicule afficherVehiculeById(UUID idVehicule);

    void supprimerVehicule(UUID idVehicule);

    Vehicule rechercherParImmatriculation(String immatriculation);

    Vehicule modifierStatut(UUID idVehicule, StatutVehicule statut);
    boolean verifierDisponibilite(UUID id, LocalDate debut, LocalDate fin);

    List<Vehicule> afficherVehiculesDisponibles(LocalDate debut, LocalDate fin);
}
