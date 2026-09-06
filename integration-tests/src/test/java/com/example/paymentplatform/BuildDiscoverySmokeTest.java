package com.example.paymentplatform;

import com.example.paymentplatform.payment.PaymentServiceApplication;
import com.example.paymentplatform.processor.ProcessorSimulatorApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.assertj.core.api.Assertions.assertThat;

class BuildDiscoverySmokeTest {

    @Test
    void discoversBothApplicationEntryPoints() {
        assertThat(PaymentServiceApplication.class)
                .hasAnnotation(SpringBootApplication.class);
        assertThat(ProcessorSimulatorApplication.class)
                .hasAnnotation(SpringBootApplication.class);
    }
}
