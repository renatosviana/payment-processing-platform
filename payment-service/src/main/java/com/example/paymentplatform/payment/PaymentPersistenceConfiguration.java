package com.example.paymentplatform.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class PaymentPersistenceConfiguration {

    @Bean
    public IdempotencyRecordRepository idempotencyRecordRepository(JdbcTemplate jdbcTemplate) {
        return new IdempotencyRecordRepository(jdbcTemplate);
    }
}
