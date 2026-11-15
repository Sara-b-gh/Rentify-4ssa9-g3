package com.rentify.flotteservice.Service;

import com.rentify.flotteservice.Entity.PeriodeIndisponible;
import com.rentify.flotteservice.Repository.PeriodeIndisponibleRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class IPeriodeIndisponibleServiceImp
        implements IPeriodeIndisponibleService {

    private final PeriodeIndisponibleRepository periodeRepository;

    @Override
    public PeriodeIndisponible ajouterPeriode(PeriodeIndisponible periode) {
        return periodeRepository.save(periode);
    }

    @Override
    public PeriodeIndisponible modifierPeriode(PeriodeIndisponible periode) {
        return periodeRepository.save(periode);
    }

    @Override
    public List<PeriodeIndisponible> afficherToutesPeriodes() {
        return periodeRepository.findAll();
    }

    @Override
    public PeriodeIndisponible afficherPeriodeById(UUID id) {
        return periodeRepository.findById(id).orElse(null);
    }

    @Override
    public void supprimerPeriode(UUID id) {
        periodeRepository.deleteById(id);
    }
}