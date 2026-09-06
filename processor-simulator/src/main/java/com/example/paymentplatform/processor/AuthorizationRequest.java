package com.example.paymentplatform.processor;

public record AuthorizationRequest(
        String processorKey,
        long amountMinor,
        String currency,
        String paymentMethodToken) {
}
