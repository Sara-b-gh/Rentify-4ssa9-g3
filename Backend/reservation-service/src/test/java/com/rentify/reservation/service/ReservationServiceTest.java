package com.rentify.reservation.service;

import com.rentify.reservation.client.DisponibiliteResponse;
import com.rentify.reservation.client.FlotteClient;
import com.rentify.reservation.client.UtilisateurClient;
import com.rentify.reservation.client.UtilisateurResponse;
import com.rentify.reservation.domain.Reservation;
import com.rentify.reservation.domain.StatutReservation;
import com.rentify.reservation.dto.ReservationRequest;
import com.rentify.reservation.event.ReservationEventPublisher;
import com.rentify.reservation.exception.ConflitReservationException;
import com.rentify.reservation.exception.FlotteIndisponibleException;
import com.rentify.reservation.exception.ReservationInvalideException;
import com.rentify.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Tests unitaires de la logique métier (sans base, sans RabbitMQ, sans Flotte). */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock ReservationRepository repository;
    @Mock FlotteClient flotteClient;
    @Mock UtilisateurClient utilisateurClient;
    @Mock ReservationEventPublisher publisher;
    @InjectMocks ReservationService service;

    private final Long vehicule = 1L;
    private final LocalDate debut = LocalDate.now().plusDays(5);
    private final LocalDate fin = LocalDate.now().plusDays(8);

    /** Par défaut, le service Utilisateurs (mock) répond : client 1 actif. */
    @BeforeEach
    void clientActif() {
        lenient().when(utilisateurClient.trouver(1L)).thenReturn(new UtilisateurResponse(1L, "CLIENT", true));
    }

    @Test
    void creer_clientDesactive_leve409SansAppelFlotte() {
        when(utilisateurClient.trouver(1L)).thenReturn(new UtilisateurResponse(1L, "CLIENT", false));
        assertThrows(ConflitReservationException.class, () -> service.creer(requete()));
        verifyNoInteractions(flotteClient);
        verify(repository, never()).save(any());
    }

    private ReservationRequest requete() {
        return new ReservationRequest(1L, vehicule, debut, fin);
    }

    @Test
    void creer_ok_calculeMontantEtPublieEvenement() {
        when(repository.existeChevauchement(any(), any(), any(), any(), any())).thenReturn(false);
        when(flotteClient.verifierDisponibilite(vehicule, debut, fin)).thenReturn(new DisponibiliteResponse(true, 50.0));
        when(repository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation r = service.creer(requete());

        assertEquals(StatutReservation.CONFIRMEE, r.getStatut());
        assertEquals(150.0, r.getMontantEstime());   // 3 jours x 50
        verify(publisher).reservationConfirmee(r);
    }

    @Test
    void creer_datesInvalides_leve400() {
        ReservationRequest invalide = new ReservationRequest(1L, vehicule, fin, debut);
        assertThrows(ReservationInvalideException.class, () -> service.creer(invalide));
        verifyNoInteractions(flotteClient);
    }

    @Test
    void creer_chevauchement_leve409() {
        when(repository.existeChevauchement(any(), any(), any(), any(), any())).thenReturn(true);
        assertThrows(ConflitReservationException.class, () -> service.creer(requete()));
        verify(repository, never()).save(any());
    }

    @Test
    void creer_vehiculeIndisponible_leve409() {
        when(flotteClient.verifierDisponibilite(vehicule, debut, fin)).thenReturn(new DisponibiliteResponse(false, 0));
        assertThrows(ConflitReservationException.class, () -> service.creer(requete()));
        verify(repository, never()).save(any());
    }

    @Test
    void creer_flotteIndisponible_refuseSansEnregistrer() {
        when(flotteClient.verifierDisponibilite(any(), any(), any())).thenThrow(new FlotteIndisponibleException("down"));
        assertThrows(FlotteIndisponibleException.class, () -> service.creer(requete()));
        verify(repository, never()).save(any());
    }

    @Test
    void annuler_deuxFois_leve409() {
        Reservation r = new Reservation(1L, vehicule, debut, fin, 100);
        r.confirmer();
        Long id = 1L;
        when(repository.findById(id)).thenReturn(java.util.Optional.of(r));
        when(repository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        service.annuler(id);
        assertEquals(StatutReservation.ANNULEE, r.getStatut());
        assertThrows(ConflitReservationException.class, () -> service.annuler(id));
    }
}
