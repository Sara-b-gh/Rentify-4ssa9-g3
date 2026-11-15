package com.rentify.infractions.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publication ASYNCHRONE de ConducteurIdentifie (consommé par Facturation, file facturation.conducteur-identifie).
 * Si RabbitMQ est arrêté, l'infraction reste enregistrée : on journalise l'échec au lieu de faire échouer la requête.
 */
@Component
public class InfractionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InfractionEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public InfractionEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publierConducteurIdentifie(ConducteurIdentifie event) {
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_CONDUCTEUR_IDENTIFIE, event);
            log.info("Événement publié : {} -> {}", RabbitConfig.ROUTING_CONDUCTEUR_IDENTIFIE, event);
        } catch (Exception e) {
            log.warn("RabbitMQ indisponible, événement {} non publié : {}",
                    RabbitConfig.ROUTING_CONDUCTEUR_IDENTIFIE, e.getMessage());
        }
    }
}
