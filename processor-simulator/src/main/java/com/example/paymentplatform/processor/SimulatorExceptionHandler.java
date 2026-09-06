package com.example.paymentplatform.processor;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@RestControllerAdvice
public class SimulatorExceptionHandler {

    @ExceptionHandler(InvalidAuthorizationException.class)
    public ResponseEntity<ErrorResponse> invalidAuthorization(InvalidAuthorizationException exception) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> unreadableRequest(HttpMessageNotReadableException exception) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse("request body is invalid"));
    }

    @ExceptionHandler(AuthorizationConflictException.class)
    public ResponseEntity<ErrorResponse> authorizationConflict(AuthorizationConflictException exception) {
        return ResponseEntity.status(CONFLICT).body(new ErrorResponse(exception.getMessage()));
    }

    public record ErrorResponse(String error) {
    }
}
