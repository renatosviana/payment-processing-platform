package com.example.paymentplatform.payment;

import java.time.Instant;
import java.util.UUID;

public record PaymentAttempt(
        UUID id,
        UUID paymentId,
        String processorKey,
        AttemptStatus status,
        String processorReference,
        Instant createdAt,
        Instant updatedAt) {
}
