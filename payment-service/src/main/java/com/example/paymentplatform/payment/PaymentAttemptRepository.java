package com.example.paymentplatform.payment;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentAttemptRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentAttemptRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(PaymentAttempt attempt) {
        jdbcTemplate.update("""
                INSERT INTO payment_attempt
                    (id, payment_id, processor_key, status, processor_reference, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                attempt.id(),
                attempt.paymentId(),
                attempt.processorKey(),
                attempt.status().name(),
                attempt.processorReference(),
                Timestamp.from(attempt.createdAt()),
                Timestamp.from(attempt.updatedAt()));
    }

    public Optional<PaymentAttempt> findById(UUID id) {
        return jdbcTemplate.query("""
                SELECT id, payment_id, processor_key, status, processor_reference, created_at, updated_at
                FROM payment_attempt
                WHERE id = ?
                """, (resultSet, rowNum) -> new PaymentAttempt(
                resultSet.getObject("id", UUID.class),
                resultSet.getObject("payment_id", UUID.class),
                resultSet.getString("processor_key"),
                AttemptStatus.valueOf(resultSet.getString("status")),
                resultSet.getString("processor_reference"),
                resultSet.getTimestamp("created_at").toInstant(),
                resultSet.getTimestamp("updated_at").toInstant()), id).stream().findFirst();
    }
}
