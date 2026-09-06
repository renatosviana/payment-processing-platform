package com.example.paymentplatform.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentPersistenceService {

    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;

    public PaymentPersistenceService(
            PaymentRepository paymentRepository,
            PaymentAttemptRepository paymentAttemptRepository,
            IdempotencyRecordRepository idempotencyRecordRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentAttemptRepository = paymentAttemptRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
    }

    @Transactional
    public void createAggregate(Payment payment, PaymentAttempt attempt, IdempotencyRecord idempotencyRecord) {
        if (!payment.id().equals(attempt.paymentId()) || !payment.id().equals(idempotencyRecord.paymentId())) {
            throw new IllegalArgumentException("payment relationships must reference the aggregate payment");
        }

        paymentRepository.insert(payment);
        paymentAttemptRepository.insert(attempt);
        idempotencyRecordRepository.insert(idempotencyRecord);
    }
}
