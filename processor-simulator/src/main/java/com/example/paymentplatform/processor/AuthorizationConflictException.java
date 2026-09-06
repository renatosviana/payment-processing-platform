package com.example.paymentplatform.processor;

public class AuthorizationConflictException extends RuntimeException {

    public AuthorizationConflictException(String message) {
        super(message);
    }
}
