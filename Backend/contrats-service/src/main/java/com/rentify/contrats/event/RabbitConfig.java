package com.rentify.contrats.event;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "rentify.events";

    @Bean
    public TopicExchange rentifyExchange() {
        return new TopicExchange(EXCHANGE);
    }

    /** Les événements sont envoyés en JSON. */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
