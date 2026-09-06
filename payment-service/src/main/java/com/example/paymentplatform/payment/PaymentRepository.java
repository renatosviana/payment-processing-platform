package com.example.paymentplatform.payment;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Payment payment) {
        jdbcTemplate.update("""
                INSERT INTO payment (id, merchant_id, order_id, amount_minor, currency, status, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                payment.id(),
                payment.merchantId(),
                payment.orderId(),
                payment.amountMinor(),
                payment.currency(),
                payment.status().name(),
                Timestamp.from(payment.createdAt()),
                Timestamp.from(payment.updatedAt()));
    }

    public Optional<Payment> findById(UUID id) {
        return jdbcTemplate.query("""
                SELECT id, merchant_id, order_id, amount_minor, currency, status, created_at, updated_at
                FROM payment
                WHERE id = ?
                """, (resultSet, rowNum) -> new Payment(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("merchant_id"),
                resultSet.getString("order_id"),
                resultSet.getLong("amount_minor"),
                resultSet.getString("currency"),
                PaymentStatus.valueOf(resultSet.getString("status")),
                resultSet.getTimestamp("created_at").toInstant(),
                resultSet.getTimestamp("updated_at").toInstant()), id).stream().findFirst();
    }
}
