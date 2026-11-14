package com.rentify.reservation.web;

import com.rentify.reservation.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.Instant;

/** Transforme les exceptions métier en codes HTTP cohérents (400, 404, 409, 503). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(int status, String message, Instant timestamp) { }

    @ExceptionHandler({ReservationInvalideException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> badRequest(Exception e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .reduce((a, b) -> a + " ; " + b).orElse("Requête invalide");
        return build(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(ReservationIntrouvableException.class)
    public ResponseEntity<ErrorResponse> notFound(ReservationIntrouvableException e) {
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ConflitReservationException.class)
    public ResponseEntity<ErrorResponse> conflict(ConflitReservationException e) {
        return build(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(FlotteIndisponibleException.class)
    public ResponseEntity<ErrorResponse> flotteDown(FlotteIndisponibleException e) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), message, Instant.now()));
    }
}
