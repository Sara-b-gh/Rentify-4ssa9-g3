package com.rentify.contrats.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrat de location. reservationId, vehiculeId et clientId sont des liens PAR IDENTIFIANT
 * vers les autres microservices (pas de clé étrangère).
 */
@Entity
@Table(name = "contrat")
@Getter
@Setter
@NoArgsConstructor
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private Long vehiculeId;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFinPrevue;

    /** Vide tant que le contrat n'est pas clôturé. */
    private LocalDateTime dateRetourReelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutContrat statut = StatutContrat.OUVERT;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "contrat_id")
    private List<Conducteur> conducteurs = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "contrat_id")
    private List<EtatDesLieux> etatsDesLieux = new ArrayList<>();

    /** Le contrat est actif à cette date/heure si elle est entre le début et la fin (réelle si clôturé, sinon prévue). */
    public boolean estActifA(LocalDateTime dateHeure) {
        LocalDateTime fin = (dateRetourReelle != null) ? dateRetourReelle : dateFinPrevue;
        return !dateHeure.isBefore(dateDebut) && !dateHeure.isAfter(fin);
    }

    public void ouvrir(EtatDesLieux etatDepart) {
        etatDepart.setType(TypeEtat.DEPART);
        etatsDesLieux.add(etatDepart);
        statut = StatutContrat.OUVERT;
    }

    public void ajouterConducteur(Conducteur conducteur) {
        conducteurs.add(conducteur);
    }

    public void cloturer(EtatDesLieux etatRetour, LocalDateTime dateRetour) {
        if (statut == StatutContrat.CLOTURE) {
            throw new IllegalStateException("Le contrat est déjà clôturé");
        }
        etatRetour.setType(TypeEtat.RETOUR);
        etatsDesLieux.add(etatRetour);
        dateRetourReelle = dateRetour;
        statut = StatutContrat.CLOTURE;
    }
}
