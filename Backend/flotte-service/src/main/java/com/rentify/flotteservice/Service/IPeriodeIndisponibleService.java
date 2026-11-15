package com.rentify.flotteservice.Service;

import com.rentify.flotteservice.Entity.PeriodeIndisponible;

import java.util.List;
import java.util.UUID;

public interface IPeriodeIndisponibleService {

    PeriodeIndisponible ajouterPeriode(PeriodeIndisponible periode);

    PeriodeIndisponible modifierPeriode(PeriodeIndisponible periode);

    List<PeriodeIndisponible> afficherToutesPeriodes();

    PeriodeIndisponible afficherPeriodeById(UUID id);

    void supprimerPeriode(UUID id);
}