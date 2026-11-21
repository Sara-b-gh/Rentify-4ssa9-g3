package com.rentify.utilisateurs.keycloak;

import com.rentify.utilisateurs.domain.Role;
import com.rentify.utilisateurs.exception.BadRequestException;
import com.rentify.utilisateurs.exception.ConflictException;
import com.rentify.utilisateurs.exception.NotFoundException;
import com.rentify.utilisateurs.exception.ServiceIndisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.*;
import java.util.function.Supplier;

/**
 * Appelle l'API d'administration de Keycloak (REST) pour gérer les comptes.
 * Authentification : client "utilisateurs-service" (client_credentials, compte de service).
 * Keycloak garde le login, le mot de passe et les rôles ; notre base garde le profil métier.
 */
@Component
public class KeycloakAdminClient {

    private final RestClient http;
    private final String url;
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final String adminUser;       // administrateur Keycloak (realm master)
    private final String adminPassword;

    public KeycloakAdminClient(RestClient.Builder builder,
                               @Value("${keycloak.url}") String url,
                               @Value("${keycloak.realm}") String realm,
                               @Value("${keycloak.admin-client-id}") String clientId,
                               @Value("${keycloak.admin-client-secret}") String clientSecret,
                               @Value("${keycloak.admin-username:admin}") String adminUser,
                               @Value("${keycloak.admin-password:admin}") String adminPassword) {
        this.adminUser = adminUser;
        this.adminPassword = adminPassword;
        this.http = builder.build();
        this.url = url;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /** Crée le compte dans Keycloak avec son mot de passe et son rôle ; retourne l'id Keycloak (claim "sub" du token). */
    public String creerUtilisateur(String email, String prenom, String nom, String motDePasse, String role) {
        Map<String, Object> corps = new HashMap<>();
        corps.put("username", email);
        corps.put("email", email);
        corps.put("firstName", prenom);
        corps.put("lastName", nom);
        corps.put("enabled", true);
        corps.put("emailVerified", true);
        corps.put("credentials", List.of(Map.of("type", "password", "value", motDePasse, "temporary", false)));

        String id = executer(() -> {
            ResponseEntity<Void> reponse = http.post()
                    .uri(admin("/users"))
                    .header(HttpHeaders.AUTHORIZATION, bearer())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(corps)
                    .retrieve()
                    .toBodilessEntity();
            String location = reponse.getHeaders().getFirst(HttpHeaders.LOCATION);   // .../users/{id}
            return location.substring(location.lastIndexOf('/') + 1);
        });
        try {
            changerRole(id, role);
        } catch (RuntimeException e) {
            supprimer(id);          // on ne laisse pas un compte sans rôle
            throw e;
        }
        return id;
    }

    /** Remplace le rôle realm de l'utilisateur (CLIENT, AGENT ou ADMIN). */
    public void changerRole(String keycloakId, String role) {
        executer(() -> {
            List<Map<String, Object>> tous = new ArrayList<>();
            for (Role r : Role.values()) {
                tous.add(representationRole(r.name()));
            }
            String chemin = admin("/users/" + keycloakId + "/role-mappings/realm");
            http.method(HttpMethod.DELETE).uri(chemin)                      // retire les anciens rôles
                    .header(HttpHeaders.AUTHORIZATION, bearer())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(tous)
                    .retrieve().toBodilessEntity();
            http.post().uri(chemin)                                         // ajoute le nouveau
                    .header(HttpHeaders.AUTHORIZATION, bearer())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of(representationRole(role)))
                    .retrieve().toBodilessEntity();
            return null;
        });
    }

    /** Active ou désactive le compte (un compte désactivé ne peut plus se connecter). */
    public void activer(String keycloakId, boolean actif) {
        executer(() -> {
            http.put().uri(admin("/users/" + keycloakId))
                    .header(HttpHeaders.AUTHORIZATION, bearer())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("enabled", actif))
                    .retrieve().toBodilessEntity();
            return null;
        });
    }

    /** Supprime le compte Keycloak. */
    public void supprimer(String keycloakId) {
        executer(() -> {
            http.delete().uri(admin("/users/" + keycloakId))
                    .header(HttpHeaders.AUTHORIZATION, bearer())
                    .retrieve().toBodilessEntity();
            return null;
        });
    }

    // ------------------------------------------------------------------ outils internes

    @SuppressWarnings("unchecked")
    private Map<String, Object> representationRole(String nom) {
        return http.get().uri(admin("/roles/" + nom))
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .retrieve()
                .body(Map.class);
    }

    private String admin(String chemin) {
        return url + "/admin/realms/" + realm + chemin;
    }

    /** Token d'accès du compte de service (grant client_credentials). */
    @SuppressWarnings("unchecked")
    private String bearer() {
        MultiValueMap<String, String> formulaire = new LinkedMultiValueMap<>();
        // Connexion de l'administrateur Keycloak (realm master, client admin-cli) : méthode la plus fiable en local
        formulaire.add("grant_type", "password");
        formulaire.add("client_id", "admin-cli");
        formulaire.add("username", adminUser);
        formulaire.add("password", adminPassword);
        Map<String, Object> reponse;
        try {
            reponse = http.post()
                    .uri(url + "/realms/master/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formulaire)
                    .retrieve()
                    .body(Map.class);
        } catch (HttpClientErrorException e) {
            // Étape 1 en échec : Keycloak refuse d'émettre le token du client technique
            throw new ServiceIndisponibleException("[ETAPE 1 - connexion admin Keycloak] HTTP "
                    + e.getStatusCode().value() + " : " + e.getResponseBodyAsString());
        }
        return "Bearer " + reponse.get("access_token");
    }

    /** Traduit les erreurs Keycloak en erreurs HTTP de ce service (404, 409, 400, 503). */
    private <T> T executer(Supplier<T> appel) {
        try {
            return appel.get();
        } catch (HttpClientErrorException e) {
            int statut = e.getStatusCode().value();
            if (statut == 409) {
                throw new ConflictException("Ce compte existe déjà dans Keycloak");
            }
            if (statut == 404) {
                throw new NotFoundException("Élément introuvable dans Keycloak (compte ou rôle)");
            }
            if (statut == 401 || statut == 403) {
                throw new ServiceIndisponibleException(
                        "[ETAPE 2 - API d'administration] Keycloak refuse le client « " + clientId + " » (HTTP " + statut + ") : "
                                + e.getResponseBodyAsString() + " — vérifier son secret et ses droits (voir GUIDE_KEYCLOAK)");
            }
            throw new BadRequestException("Keycloak a refusé la requête (HTTP " + statut + ")");
        } catch (RestClientException e) {
            throw new ServiceIndisponibleException("Keycloak est indisponible, réessayer plus tard");
        }
    }
}