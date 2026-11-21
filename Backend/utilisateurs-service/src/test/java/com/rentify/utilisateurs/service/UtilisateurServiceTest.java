package com.rentify.utilisateurs.service;

import com.rentify.utilisateurs.domain.*;
import com.rentify.utilisateurs.dto.*;
import com.rentify.utilisateurs.exception.*;
import com.rentify.utilisateurs.keycloak.KeycloakAdminClient;
import com.rentify.utilisateurs.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Tests unitaires Utilisateurs (Keycloak simulé par un mock, sans base de données). */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UtilisateurServiceTest {

    @Mock UtilisateurRepository repository;
    @Mock KeycloakAdminClient keycloak;
    @InjectMocks UtilisateurService service;

    private final UtilisateurRequest req =
            new UtilisateurRequest("Ben Ali", "Sara", "sara@exemple.tn", null, Role.CLIENT, "secret123");

    private Utilisateur utilisateur(boolean actif) {
        Utilisateur u = new Utilisateur();
        u.setId(1L);
        u.setKeycloakId("kc-1");
        u.setRole(Role.CLIENT);
        u.setActif(actif);
        return u;
    }

    @Test
    void creer_ok_creeLeCompteKeycloakPuisLeProfil() {
        when(keycloak.creerUtilisateur("sara@exemple.tn", "Sara", "Ben Ali", "secret123", "CLIENT")).thenReturn("kc-1");
        when(repository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        Utilisateur u = service.creer(req);

        assertEquals("kc-1", u.getKeycloakId());
        assertEquals(Role.CLIENT, u.getRole());
        assertTrue(u.getActif());
    }

    @Test
    void creer_emailDejaUtilise_leve409SansToucherKeycloak() {
        when(repository.existsByEmailIgnoreCase("sara@exemple.tn")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.creer(req));
        verifyNoInteractions(keycloak);
    }

    @Test
    void creer_sansMotDePasse_leve400() {
        UtilisateurRequest sansMdp = new UtilisateurRequest("Ben Ali", "Sara", "sara@exemple.tn", null, Role.CLIENT, null);

        assertThrows(BadRequestException.class, () -> service.creer(sansMdp));
        verifyNoInteractions(keycloak);
    }

    @Test
    void creer_keycloakArrete_leve503EtNEnregistreRien() {
        when(keycloak.creerUtilisateur(any(), any(), any(), any(), any()))
                .thenThrow(new ServiceIndisponibleException("Keycloak est indisponible"));

        assertThrows(ServiceIndisponibleException.class, () -> service.creer(req));
        verify(repository, never()).save(any());
    }

    @Test
    void inscrire_forceLeRoleClient() {
        when(keycloak.creerUtilisateur(any(), any(), any(), any(), any())).thenReturn("kc-2");
        when(repository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        Utilisateur u = service.inscrire(new InscriptionRequest("Ben Ali", "Sara", "sara@exemple.tn", null, "secret123"));

        assertEquals(Role.CLIENT, u.getRole());
        verify(keycloak).creerUtilisateur("sara@exemple.tn", "Sara", "Ben Ali", "secret123", "CLIENT");
    }

    @Test
    void desactiver_desactiveDansKeycloak_puisDeuxiemeFoisLeve409() {
        Utilisateur u = utilisateur(true);
        when(repository.findById(1L)).thenReturn(Optional.of(u));
        when(repository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        service.desactiver(1L);

        assertFalse(u.getActif());
        verify(keycloak).activer("kc-1", false);
        assertThrows(ConflictException.class, () -> service.desactiver(1L));
    }

    @Test
    void changerRole_ok_metAJourKeycloakEtLeProfil() {
        Utilisateur u = utilisateur(true);
        when(repository.findById(1L)).thenReturn(Optional.of(u));
        when(repository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(Role.AGENT, service.changerRole(1L, Role.AGENT).getRole());
        verify(keycloak).changerRole("kc-1", "AGENT");
    }

    @Test
    void trouver_inconnu_leve404() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.trouver(9L));
    }
}
