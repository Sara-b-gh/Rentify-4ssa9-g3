package com.rentify.infractions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** Vérifie que le service démarre (base H2 en mémoire, sans MySQL ni RabbitMQ). */
@SpringBootTest
@ActiveProfiles("h2")
class InfractionsServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
