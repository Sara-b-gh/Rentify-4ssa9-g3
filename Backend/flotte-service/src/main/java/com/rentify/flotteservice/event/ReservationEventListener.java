
package com.rentify.flotteservice.event;

import com.rentify.flotteservice.Entity.PeriodeIndisponible;
import com.rentify.flotteservice.Entity.Vehicule;
import com.rentify.flotteservice.Repository.PeriodeIndisponibleRepository;
import com.rentify.flotteservice.Repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final PeriodeIndisponibleRepository periodeRepository;
    private final VehiculeRepository vehiculeRepository;

    @RabbitListener(queues = RabbitConfig.QUEUE_RESERVATION)
    @Transactional
    public void recevoirEvenement(ReservationEvent event) {

        if ("ReservationConfirmee".equals(event.type())) {
            confirmerReservation(event);
        } else if ("ReservationAnnulee".equals(event.type())) {
            annulerReservation(event);
        }
    }

    private void confirmerReservation(ReservationEvent event) {

        if (periodeRepository.findByReservationId(
                event.reservationId()).isPresent()) {
            return;
        }

        Vehicule vehicule = vehiculeRepository
                .findById(event.vehiculeId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Vehicule introuvable"));

        PeriodeIndisponible periode = new PeriodeIndisponible();
        periode.setReservationId(event.reservationId());
        periode.setDateDebut(event.dateDebut());
        periode.setDateFin(event.dateFin());
        periode.setMotif("Reservation");
        periode.setVehicule(vehicule);

        periodeRepository.save(periode);
    }

    private void annulerReservation(ReservationEvent event) {
        periodeRepository.findByReservationId(event.reservationId())
                .ifPresent(periodeRepository::delete);
    }
}
