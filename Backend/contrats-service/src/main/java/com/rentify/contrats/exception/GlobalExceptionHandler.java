package com.rentify.contrats.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduit les erreurs en codes HTTP : 400, 404, 409, 501, 503. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContratNotFoundException.class)
    public ResponseEntity<ProblemDetail> notFound(ContratNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> conflict(IllegalStateException e) {
        return build(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> illegalArgument(IllegalArgumentException e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> badRequest(MethodArgumentNotValidException e) {
        return build(HttpStatus.BAD_REQUEST, "Requête invalide");
    }

    @ExceptionHandler(ServiceIndisponibleException.class)
    public ResponseEntity<ProblemDetail> unavailable(ServiceIndisponibleException e) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ProblemDetail> notImplemented(UnsupportedOperationException e) {
        return build(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
    }

    private ResponseEntity<ProblemDetail> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, message));
    }
}
