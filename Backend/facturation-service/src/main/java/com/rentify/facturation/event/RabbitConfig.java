package com.rentify.facturation.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_CONTRAT_CLOTURE = "facturation.contrat-cloture";
    public static final String QUEUE_CONDUCTEUR_IDENTIFIE = "facturation.conducteur-identifie";

    @Bean
    public TopicExchange rentifyExchange(@Value("${rentify.events.exchange:rentify.events}") String nom) {
        return new TopicExchange(nom);
    }

    @Bean
    public Queue contratClotureQueue() {
        return new Queue(QUEUE_CONTRAT_CLOTURE, true);
    }

    @Bean
    public Queue conducteurIdentifieQueue() {
        return new Queue(QUEUE_CONDUCTEUR_IDENTIFIE, true);
    }

    @Bean
    public Binding contratClotureBinding(Queue contratClotureQueue, TopicExchange rentifyExchange) {
        return BindingBuilder.bind(contratClotureQueue).to(rentifyExchange).with("contrat.cloture");
    }

    @Bean
    public Binding conducteurIdentifieBinding(Queue conducteurIdentifieQueue, TopicExchange rentifyExchange) {
        return BindingBuilder.bind(conducteurIdentifieQueue).to(rentifyExchange)
                .with("infraction.conducteur-identifie");
    }

    // JSON : le type vient du paramètre de la méthode, pas du nom de classe de l'émetteur
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter convertisseur = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("*");
        convertisseur.setJavaTypeMapper(typeMapper);
        return convertisseur;
    }
}