package com.rentify.reservation.service;

import com.rentify.reservation.client.DisponibiliteResponse;
import com.rentify.reservation.client.FlotteClient;
import com.rentify.reservation.domain.Reservation;
import com.rentify.reservation.domain.StatutReservation;
import com.rentify.reservation.dto.ReservationRequest;
import com.rentify.reservation.event.ReservationEventPublisher;
import com.rentify.reservation.exception.ConflitReservationException;
import com.rentify.reservation.exception.FlotteIndisponibleException;
import com.rentify.reservation.exception.ReservationInvalideException;
import com.rentify.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Tests unitaires de la logique métier (sans base, sans RabbitMQ, sans Flotte). */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock ReservationRepository repository;
    @Mock FlotteClient flotteClient;
    @Mock ReservationEventPublisher publisher;
    @InjectMocks ReservationService service;

    private final UUID vehicule = UUID.randomUUID();
    private final LocalDate debut = LocalDate.now().plusDays(5);
    private final LocalDate fin = LocalDate.now().plusDays(8);

    private ReservationRequest requete() {
        return new ReservationRequest("client-1", vehicule, debut, fin);
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
        ReservationRequest invalide = new ReservationRequest("c", vehicule, fin, debut);
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
        Reservation r = new Reservation("c", vehicule, debut, fin, 100);
        r.confirmer();
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(java.util.Optional.of(r));
        when(repository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        service.annuler(id);
        assertEquals(StatutReservation.ANNULEE, r.getStatut());
        assertThrows(ConflitReservationException.class, () -> service.annuler(id));
    }
}
