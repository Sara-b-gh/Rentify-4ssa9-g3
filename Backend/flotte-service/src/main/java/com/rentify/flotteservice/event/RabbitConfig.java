
package com.rentify.flotteservice.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper.TypePrecedence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_RESERVATION =
            "flotte.reservation";

    public static final String RK_CONFIRMEE =
            "reservation.confirmee";

    public static final String RK_ANNULEE =
            "reservation.annulee";

    @Bean
    public TopicExchange rentifyExchange(
            @Value("${rentify.events.exchange}") String name) {
        return new TopicExchange(name);
    }

    @Bean
    public Queue reservationQueue() {
        return QueueBuilder.durable(QUEUE_RESERVATION).build();
    }

    @Bean
    public Binding reservationConfirmeeBinding(
            Queue reservationQueue,
            TopicExchange rentifyExchange) {
        return BindingBuilder.bind(reservationQueue)
                .to(rentifyExchange)
                .with(RK_CONFIRMEE);
    }

    @Bean
    public Binding reservationAnnuleeBinding(
            Queue reservationQueue,
            TopicExchange rentifyExchange) {
        return BindingBuilder.bind(reservationQueue)
                .to(rentifyExchange)
                .with(RK_ANNULEE);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter =
                new Jackson2JsonMessageConverter(objectMapper);
        converter.setTypePrecedence(TypePrecedence.INFERRED);
        return converter;
    }
}
