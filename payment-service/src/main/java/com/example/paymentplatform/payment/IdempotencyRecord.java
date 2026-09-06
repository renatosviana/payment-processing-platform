package com.example.paymentplatform.payment;

import java.time.Instant;
import java.util.UUID;

public record IdempotencyRecord(
        String merchantId,
        String idempotencyKey,
        String requestFingerprint,
        UUID paymentId,
        Instant createdAt) {
}
