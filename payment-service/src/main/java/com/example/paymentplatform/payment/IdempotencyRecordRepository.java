package com.example.paymentplatform.payment;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

public class IdempotencyRecordRepository {

    private final JdbcTemplate jdbcTemplate;

    public IdempotencyRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(IdempotencyRecord record) {
        jdbcTemplate.update("""
                INSERT INTO idempotency_record
                    (merchant_id, idempotency_key, request_fingerprint, payment_id, created_at)
                VALUES (?, ?, ?, ?, ?)
                """,
                record.merchantId(),
                record.idempotencyKey(),
                record.requestFingerprint(),
                record.paymentId(),
                Timestamp.from(record.createdAt()));
    }

    public Optional<IdempotencyRecord> findByKey(String merchantId, String idempotencyKey) {
        return jdbcTemplate.query("""
                SELECT merchant_id, idempotency_key, request_fingerprint, payment_id, created_at
                FROM idempotency_record
                WHERE merchant_id = ? AND idempotency_key = ?
                """, (resultSet, rowNum) -> new IdempotencyRecord(
                resultSet.getString("merchant_id"),
                resultSet.getString("idempotency_key"),
                resultSet.getString("request_fingerprint"),
                resultSet.getObject("payment_id", java.util.UUID.class),
                resultSet.getTimestamp("created_at").toInstant()), merchantId, idempotencyKey)
                .stream().findFirst();
    }
}
