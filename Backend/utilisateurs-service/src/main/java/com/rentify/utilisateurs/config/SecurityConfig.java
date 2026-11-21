package com.rentify.utilisateurs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Sécurité : le service est un "resource server" OAuth2. Chaque requête doit porter un token JWT
 * émis par Keycloak (en-tête Authorization: Bearer ...). Les rôles viennent de "realm_access.roles".
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())                                   // API sans session : pas de CSRF
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Publics : Swagger, santé, inscription d'un client
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs/inscription", "/api/auth/login").permitAll()
                        // Lectures INTERNES appelées par les autres microservices (Feign) : à protéger via la Gateway plus tard
                        .requestMatchers(HttpMethod.GET, "/api/permis/par-utilisateur/*").permitAll()
                        // Mon profil : tout utilisateur connecté (la règle "moi" doit précéder la règle {id})
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs/moi").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs/*").permitAll()
                        // Liste : agents et administrateurs
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs").hasAnyRole("ADMIN", "AGENT")
                        // Création / modification / rôle / désactivation / suppression : administrateur
                        .requestMatchers("/api/utilisateurs/**").hasRole("ADMIN")
                        // Permis : agents et administrateurs
                        .requestMatchers("/api/permis/**").hasAnyRole("ADMIN", "AGENT")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(convertisseur())));
        return http.build();
    }

    /** Transforme les rôles realm du token (CLIENT, AGENT, ADMIN) en autorités Spring ROLE_CLIENT, ROLE_AGENT, ROLE_ADMIN. */
    @Bean
    JwtAuthenticationConverter convertisseur() {
        Converter<Jwt, Collection<GrantedAuthority>> roles = jwt -> {
            List<GrantedAuthority> autorites = new ArrayList<>();
            Object realmAccess = jwt.getClaim("realm_access");
            if (realmAccess instanceof Map<?, ?> acces && acces.get("roles") instanceof Collection<?> liste) {
                for (Object role : liste) {
                    autorites.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }
            return autorites;
        };
        JwtAuthenticationConverter convertisseur = new JwtAuthenticationConverter();
        convertisseur.setJwtGrantedAuthoritiesConverter(roles);
        return convertisseur;
    }
}