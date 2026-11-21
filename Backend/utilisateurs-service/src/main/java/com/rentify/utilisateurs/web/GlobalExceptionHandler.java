package com.rentify.utilisateurs.web;

import com.rentify.utilisateurs.exception.BadRequestException;
import com.rentify.utilisateurs.exception.ConflictException;
import com.rentify.utilisateurs.exception.NotFoundException;
import com.rentify.utilisateurs.exception.ServiceIndisponibleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** Transforme les erreurs en réponses JSON claires : 400, 404, 409. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> introuvable(NotFoundException e) {
        return reponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> conflit(ConflictException e) {
        return reponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> invalide(BadRequestException e) {
        return reponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** Un autre microservice est injoignable (arrêté, timeout) : 503. */
    @ExceptionHandler(ServiceIndisponibleException.class)
    public ResponseEntity<Map<String, Object>> serviceIndisponible(ServiceIndisponibleException e) {
        return reponse(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    /** Champs manquants ou invalides (@NotNull, @NotBlank, @Positive...). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .collect(Collectors.joining(" ; "));
        return reponse(HttpStatus.BAD_REQUEST, message);
    }

    /** JSON mal écrit, date ou valeur d'énumération incorrecte. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonIllisible(HttpMessageNotReadableException e) {
        return reponse(HttpStatus.BAD_REQUEST, "Corps de la requête illisible : vérifier le JSON, les dates (AAAA-MM-JJ) et les valeurs");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> typeIncorrect(MethodArgumentTypeMismatchException e) {
        return reponse(HttpStatus.BAD_REQUEST, "Paramètre invalide : " + e.getName());
    }

    private ResponseEntity<Map<String, Object>> reponse(HttpStatus status, String message) {
        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("date", LocalDateTime.now().toString());
        corps.put("status", status.value());
        corps.put("message", message);
        return ResponseEntity.status(status).body(corps);
    }
}
