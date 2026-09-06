package com.example.paymentplatform.processor;

public class InvalidAuthorizationException extends RuntimeException {

    public InvalidAuthorizationException(String message) {
        super(message);
    }
}
