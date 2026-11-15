package com.rentify.facturation;

import com.rentify.facturation.dto.FactureResponse;
import com.rentify.facturation.service.FactureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Données de test : actives uniquement avec le profil "h2"
@Component
@Profile("h2")
public class DonneesDeTest implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DonneesDeTest.class);

    private final FactureService factureService;

    public DonneesDeTest(FactureService factureService) {
        this.factureService = factureService;
    }

    @Override
    public void run(String... args) {
        UUID contratId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        FactureResponse facture = factureService.creerFacture(contratId, 300.0);
        log.info("Facture de test : id={} contratId={} total={}",
                facture.id(), facture.contratId(), facture.montantTotal());
    }
}