package com.example.paymentplatform.payment;

import java.time.Instant;
import java.util.UUID;

public record Payment(
        UUID id,
        String merchantId,
        String orderId,
        long amountMinor,
        String currency,
        PaymentStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
