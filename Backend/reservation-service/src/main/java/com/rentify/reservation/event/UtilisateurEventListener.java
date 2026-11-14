package com.rentify.reservation.event;

import com.rentify.reservation.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consomme UtilisateurDesactive : annule les réservations futures du client. */
@Component
public class UtilisateurEventListener {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurEventListener.class);

    private final ReservationService service;

    public UtilisateurEventListener(ReservationService service) {
        this.service = service;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_UTILISATEUR_DESACTIVE)
    public void onUtilisateurDesactive(UtilisateurDesactiveEvent event) {
        log.info("UtilisateurDesactive reçu pour {}", event.utilisateurId());
        service.annulerReservationsFutures(event.utilisateurId());
    }
}
