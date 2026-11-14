package com.rentify.reservation.web;

import com.rentify.reservation.dto.ModifierDatesRequest;
import com.rentify.reservation.dto.ReservationRequest;
import com.rentify.reservation.dto.ReservationResponse;
import com.rentify.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/** API REST du service Réservation. */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    /** 201 créée, 400 données invalides, 409 conflit. */
    @PostMapping
    public ResponseEntity<ReservationResponse> creer(@Valid @RequestBody ReservationRequest req) {
        ReservationResponse body = ReservationResponse.from(service.creer(req));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    /** 200 trouvée, 404 introuvable. */
    @GetMapping("/{id}")
    public ReservationResponse trouver(@PathVariable UUID id) {
        return ReservationResponse.from(service.trouver(id));
    }

    /** Liste (pratique pour les tests et le frontend). */
    @GetMapping
    public List<ReservationResponse> lister() {
        return service.lister().stream().map(ReservationResponse::from).toList();
    }

    /** 200 modifiée, 400 dates invalides, 409 conflit. */
    @PutMapping("/{id}")
    public ReservationResponse modifier(@PathVariable UUID id, @Valid @RequestBody ModifierDatesRequest req) {
        return ReservationResponse.from(service.modifierDates(id, req));
    }

    /** 204 annulée, 404 introuvable, 409 déjà annulée/terminée. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void annuler(@PathVariable UUID id) {
        service.annuler(id);
    }
}
