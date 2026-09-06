package com.example.paymentplatform.processor;

public record AuthorizationResponse(
        String processorKey,
        String operationId,
        AuthorizationOutcome outcome,
        long amountMinor,
        String currency,
        int requestCount,
        int operationCount) {
}
