package com.rentify.facturation.event;

import com.rentify.facturation.client.FlotteClient;
import com.rentify.facturation.dto.ContratClotureEvent;
import com.rentify.facturation.dto.TarifResponse;
import com.rentify.facturation.exception.TarifIndisponibleException;
import com.rentify.facturation.service.FactureService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FactureEventListenerTest {

    @Mock
    private FactureService factureService;

    @Mock
    private FlotteClient flotteClient;

    @InjectMocks
    private FactureEventListener listener;

    private ContratClotureEvent evenement() {
        return new ContratClotureEvent(UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
    }

    @Test
    void contratCloture_calculeLeMontantAvecLeTarifDeFlotte() {
        ContratClotureEvent event = evenement();
        when(flotteClient.getTarif(event.vehiculeId())).thenReturn(new TarifResponse(100.0));

        listener.onContratCloture(event);

        // 3 jours x 100 = 300
        verify(factureService).creerFacture(eq(event.contratId()), eq(300.0));
    }

    @Test
    void flotteIndisponible_aucuneFactureCreeeEtLeMessageSeraRejoue() {
        ContratClotureEvent event = evenement();
        when(flotteClient.getTarif(any()))
                .thenThrow(new TarifIndisponibleException("Flotte en panne"));

        assertThrows(TarifIndisponibleException.class, () -> listener.onContratCloture(event));

        verify(factureService, never()).creerFacture(any(), anyDouble());
    }
}