package com.rentify.reservation.event;

import com.rentify.reservation.domain.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

/** Publie les événements de domaine de Réservation (consommés par Flotte, Contrats...). */
@Component
public class ReservationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ReservationEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public ReservationEventPublisher(RabbitTemplate rabbitTemplate,
                                     @Value("${rentify.events.exchange}") String exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void reservationConfirmee(Reservation r) {
        publier(RabbitConfig.RK_CONFIRMEE, "ReservationConfirmee", r);
    }

    public void reservationAnnulee(Reservation r) {
        publier(RabbitConfig.RK_ANNULEE, "ReservationAnnulee", r);
    }

    private void publier(String routingKey, String type, Reservation r) {
        ReservationEvent event = new ReservationEvent(type, r.getId(), r.getClientId(),
                r.getVehiculeId(), r.getDateDebut(), r.getDateFin(), Instant.now());
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("Événement {} publié pour la réservation {}", type, r.getId());
        } catch (Exception e) {
            // RabbitMQ absent : on journalise sans faire échouer la réservation
            log.warn("Impossible de publier {} (RabbitMQ indisponible) : {}", type, e.getMessage());
        }
    }
}
