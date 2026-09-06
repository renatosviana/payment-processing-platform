package com.example.paymentplatform.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class PaymentPersistencePostgresTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private PaymentPersistenceService persistenceService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentAttemptRepository paymentAttemptRepository;

    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void clearTables() {
        jdbcTemplate.execute("DELETE FROM idempotency_record");
        jdbcTemplate.execute("DELETE FROM payment_attempt");
        jdbcTemplate.execute("DELETE FROM payment");
    }

    @Test
    void committedAggregateMakesPaymentAttemptAndKeyVisible() {
        Aggregate aggregate = aggregate("merchant-001", "key-001", "processor-001");

        persistenceService.createAggregate(aggregate.payment(), aggregate.attempt(), aggregate.idempotencyRecord());

        assertThat(paymentRepository.findById(aggregate.payment().id())).contains(aggregate.payment());
        assertThat(paymentAttemptRepository.findById(aggregate.attempt().id())).contains(aggregate.attempt());
        assertThat(idempotencyRecordRepository.findByKey("merchant-001", "key-001"))
                .contains(aggregate.idempotencyRecord());
    }

    @Test
    void merchantAndKeyUniquenessRollsBackTheWholeAggregate() {
        Aggregate first = aggregate("merchant-002", "key-002", "processor-002");
        Aggregate conflicting = aggregate("merchant-002", "key-002", "processor-003");
        persistenceService.createAggregate(first.payment(), first.attempt(), first.idempotencyRecord());

        assertThatThrownBy(() -> persistenceService.createAggregate(
                conflicting.payment(), conflicting.attempt(), conflicting.idempotencyRecord()))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(paymentRepository.findById(conflicting.payment().id())).isEmpty();
        assertThat(paymentAttemptRepository.findById(conflicting.attempt().id())).isEmpty();
        assertThat(idempotencyRecordRepository.findByKey("merchant-002", "key-002"))
                .contains(first.idempotencyRecord());
    }

    @Test
    void processorKeyUniquenessRollsBackPaymentAndAttempt() {
        Aggregate first = aggregate("merchant-003", "key-003", "processor-004");
        Aggregate conflicting = aggregate("merchant-004", "key-004", "processor-004");
        persistenceService.createAggregate(first.payment(), first.attempt(), first.idempotencyRecord());

        assertThatThrownBy(() -> persistenceService.createAggregate(
                conflicting.payment(), conflicting.attempt(), conflicting.idempotencyRecord()))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(paymentRepository.findById(conflicting.payment().id())).isEmpty();
        assertThat(paymentAttemptRepository.findById(conflicting.attempt().id())).isEmpty();
        assertThat(idempotencyRecordRepository.findByKey("merchant-004", "key-004")).isEmpty();
    }

    @Test
    void onePaymentCannotHaveTwoAttempts() {
        Aggregate aggregate = aggregate("merchant-005", "key-005", "processor-005");
        persistenceService.createAggregate(aggregate.payment(), aggregate.attempt(), aggregate.idempotencyRecord());

        PaymentAttempt secondAttempt = new PaymentAttempt(
                UUID.randomUUID(),
                aggregate.payment().id(),
                "processor-006",
                AttemptStatus.SUBMITTED,
                null,
                aggregate.createdAt(),
                aggregate.createdAt());

        assertThatThrownBy(() -> paymentAttemptRepository.insert(secondAttempt))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private static Aggregate aggregate(String merchantId, String idempotencyKey, String processorKey) {
        UUID paymentId = UUID.randomUUID();
        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        Payment payment = new Payment(paymentId, merchantId, "order-001", 10000, "CAD", PaymentStatus.PROCESSING, now, now);
        PaymentAttempt attempt = new PaymentAttempt(
                UUID.randomUUID(), paymentId, processorKey, AttemptStatus.SUBMITTED, null, now, now);
        IdempotencyRecord idempotencyRecord = new IdempotencyRecord(
                merchantId, idempotencyKey, "a".repeat(64), paymentId, now);
        return new Aggregate(payment, attempt, idempotencyRecord, now);
    }

    private record Aggregate(
            Payment payment,
            PaymentAttempt attempt,
            IdempotencyRecord idempotencyRecord,
            Instant createdAt) {
    }
}
