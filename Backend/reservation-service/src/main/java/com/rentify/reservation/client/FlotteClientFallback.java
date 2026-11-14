package com.rentify.reservation.client;

import com.rentify.reservation.exception.FlotteIndisponibleException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Comportement quand Flotte ne répond pas : on REFUSE la réservation (rien n'est enregistré),
 * comme décrit dans le document technique.
 */
@Component
public class FlotteClientFallback implements FlotteClient {

    @Override
    public DisponibiliteResponse verifierDisponibilite(UUID vehiculeId, LocalDate debut, LocalDate fin) {
        throw new FlotteIndisponibleException(
                "Service Flotte indisponible : la réservation est refusée, veuillez réessayer plus tard");
    }
}
