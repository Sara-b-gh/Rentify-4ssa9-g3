package com.rentify.facturation.event;

import com.rentify.facturation.client.FlotteClient;
import com.rentify.facturation.dto.ConducteurIdentifieEvent;
import com.rentify.facturation.dto.ContratClotureEvent;
import com.rentify.facturation.dto.TarifResponse;
import com.rentify.facturation.service.FactureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class FactureEventListener {

    private static final Logger log = LoggerFactory.getLogger(FactureEventListener.class);

    private final FactureService factureService;
    private final FlotteClient flotteClient;

    public FactureEventListener(FactureService factureService, FlotteClient flotteClient) {
        this.factureService = factureService;
        this.flotteClient = flotteClient;
    }

    // Contrat clôturé : on demande le tarif à Flotte (synchrone) puis on émet la facture.
    // Si Flotte est indisponible, l'exception fait rejouer le message (il n'est pas perdu).
    @RabbitListener(queues = RabbitConfig.QUEUE_CONTRAT_CLOTURE)
    public void onContratCloture(ContratClotureEvent event) {
        log.info("ContratCloture reçu : contratId={}", event.contratId());
        TarifResponse tarif = flotteClient.getTarif(event.vehiculeId());
        long jours = Math.max(1, ChronoUnit.DAYS.between(event.dateDebut(), event.dateFin()));
        factureService.creerFacture(event.contratId(), tarif.tarifJournalier() * jours);
    }

    // Conducteur identifié pour une infraction : on ajoute une pénalité à la facture
    @RabbitListener(queues = RabbitConfig.QUEUE_CONDUCTEUR_IDENTIFIE)
    public void onConducteurIdentifie(ConducteurIdentifieEvent event) {
        log.info("ConducteurIdentifie reçu : infractionId={}", event.infractionId());
        factureService.ajouterPenalite(event.contratId(), event.infractionId(),
                event.conducteurId(), event.montant());
    }
}