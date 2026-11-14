package com.rentify.reservation.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper.TypePrecedence;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Déclaration de l'échange, de la file et du binding RabbitMQ. */
@Configuration
public class RabbitConfig {

    public static final String QUEUE_UTILISATEUR_DESACTIVE = "reservation.utilisateur-desactive";
    public static final String RK_UTILISATEUR_DESACTIVE = "utilisateur.desactive";
    public static final String RK_CONFIRMEE = "reservation.confirmee";
    public static final String RK_ANNULEE = "reservation.annulee";

    /** Échange commun de la plateforme (type topic). */
    @Bean
    public TopicExchange rentifyExchange(@Value("${rentify.events.exchange}") String name) {
        return new TopicExchange(name);
    }

    /** File propre au service Réservation pour l'événement UtilisateurDesactive. */
    @Bean
    public Queue utilisateurDesactiveQueue() {
        return QueueBuilder.durable(QUEUE_UTILISATEUR_DESACTIVE).build();
    }

    @Bean
    public Binding utilisateurDesactiveBinding(Queue utilisateurDesactiveQueue, TopicExchange rentifyExchange) {
        return BindingBuilder.bind(utilisateurDesactiveQueue).to(rentifyExchange).with(RK_UTILISATEUR_DESACTIVE);
    }

    /** Les messages sont échangés en JSON. */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        // le type est déduit du paramètre du @RabbitListener (pas de l'en-tête de l'émetteur)
        converter.setTypePrecedence(TypePrecedence.INFERRED);
        return converter;
    }
}
