package com.rentify.reservation.repository;

import com.rentify.reservation.domain.Reservation;
import com.rentify.reservation.domain.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    /**
     * Y a-t-il déjà une réservation confirmée de ce véhicule qui chevauche [debut, fin] ?
     * "exclu" permet d'ignorer la réservation en cours de modification.
     */
    @Query("""
           select count(r) > 0 from Reservation r
           where r.vehiculeId = :vehiculeId
             and r.statut = :statut
             and r.id <> :exclu
             and r.dateDebut < :fin
             and r.dateFin > :debut
           """)
    boolean existeChevauchement(@Param("vehiculeId") UUID vehiculeId,
                                @Param("statut") StatutReservation statut,
                                @Param("debut") LocalDate debut,
                                @Param("fin") LocalDate fin,
                                @Param("exclu") UUID exclu);

    /** Réservations d'un client à venir (pour l'annulation après désactivation du compte). */
    List<Reservation> findByClientIdAndStatutAndDateDebutAfter(String clientId,
                                                               StatutReservation statut,
                                                               LocalDate date);
}
