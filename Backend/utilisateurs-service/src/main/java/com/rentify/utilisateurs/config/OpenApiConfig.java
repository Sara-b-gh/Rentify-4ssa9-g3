package com.rentify.utilisateurs.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Ajoute le bouton « Authorize » dans Swagger pour coller le token JWT de Keycloak. */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Microservice Utilisateurs").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .components(new Components().addSecuritySchemes("bearer-jwt",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
