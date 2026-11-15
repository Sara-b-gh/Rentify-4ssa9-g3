package com.rentify.flotteservice.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String marque;

    private String modele;

    @Column(unique = true, nullable = false)
    private String immatriculation;

    private String couleur;

    private int nombrePlaces;

    private double tarifJournalier;

    @Enumerated(EnumType.STRING)
    private StatutVehicule statut;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
}