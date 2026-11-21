package com.rentify.utilisateurs.web;

import com.rentify.utilisateurs.exception.BadRequestException;
import com.rentify.utilisateurs.exception.ServiceIndisponibleException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Connexion pratique pour les tests Swagger : on envoie email + mot de passe,
 * le service les transmet à Keycloak (client public "rentify-swagger") et renvoie le token.
 * Le mot de passe n'est ni enregistré ni journalisé.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification")
public class AuthController {

    public record LoginRequest(@NotBlank String email, @NotBlank String motDePasse) { }

    private final RestClient http;
    private final String tokenUrl;

    public AuthController(RestClient.Builder builder,
                          @Value("${keycloak.url}") String url,
                          @Value("${keycloak.realm}") String realm) {
        this.http = builder.build();
        this.tokenUrl = url + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    @PostMapping("/login")
    @Operation(summary = "Obtenir un token (à coller dans le bouton Authorize)")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest req) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "rentify-swagger");
        form.add("username", req.email());
        form.add("password", req.motDePasse());
        try {
            Map<?, ?> r = http.post().uri(tokenUrl).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form).retrieve().body(Map.class);
            return Map.of("access_token", r.get("access_token"), "expires_in", r.get("expires_in"));
        } catch (HttpClientErrorException e) {
            throw new BadRequestException("Email ou mot de passe incorrect");
        } catch (RestClientException e) {
            throw new ServiceIndisponibleException("Keycloak indisponible");
        }
    }
}