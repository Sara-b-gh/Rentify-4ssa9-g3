package com.rentify.contrats.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publication ASYNCHRONE des événements du service Contrats.
 * Si RabbitMQ est arrêté, le contrat reste enregistré : on journalise l'échec au lieu de faire échouer la requête.
 */
@Component
public class ContratEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ContratEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public ContratEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publierOuvert(ContratOuvert event) {
        publier("contrat.ouvert", event);
    }

    public void publierCloture(ContratCloture event) {
        publier("contrat.cloture", event);
    }

    private void publier(String routingKey, Object event) {
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, event);
            log.info("Événement publié : {} -> {}", routingKey, event);
        } catch (Exception e) {
            log.warn("RabbitMQ indisponible, événement {} non publié : {}", routingKey, e.getMessage());
        }
    }
}
