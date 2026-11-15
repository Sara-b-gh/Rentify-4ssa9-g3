package com.rentify.facturation.exception;

import com.rentify.facturation.dto.ErreurResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GestionnaireExceptions {

    // 404
    @ExceptionHandler(FactureIntrouvableException.class)
    public ResponseEntity<ErreurResponse> introuvable(FactureIntrouvableException e) {
        return reponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 409
    @ExceptionHandler(OperationInvalideException.class)
    public ResponseEntity<ErreurResponse> conflit(OperationInvalideException e) {
        return reponse(HttpStatus.CONFLICT, e.getMessage());
    }

    // 400 : champ invalide (ex : montant négatif, mode vide)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurResponse> donneesInvalides(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(erreur -> erreur.getField() + " : " + erreur.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return reponse(HttpStatus.BAD_REQUEST, message);
    }

    // 400 : corps JSON absent ou mal formé
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurResponse> jsonInvalide(HttpMessageNotReadableException e) {
        return reponse(HttpStatus.BAD_REQUEST, "Corps de la requête invalide");
    }

    private ResponseEntity<ErreurResponse> reponse(HttpStatus status, String message) {
        ErreurResponse corps = new ErreurResponse(
                status.value(), status.getReasonPhrase(), message, LocalDateTime.now());
        return ResponseEntity.status(status).body(corps);
    }
}

