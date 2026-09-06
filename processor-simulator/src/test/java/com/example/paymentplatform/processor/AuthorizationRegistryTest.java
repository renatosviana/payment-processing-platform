package com.example.paymentplatform.processor;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorizationRegistryTest {

    private final AuthorizationRegistry registry = new AuthorizationRegistry();

    @Test
    void identicalRequestsReuseOneOperationAndCountBothRequests() {
        AuthorizationRequest request = approval("processor-001");

        AuthorizationResponse first = registry.authorize(request);
        AuthorizationResponse replay = registry.authorize(request);

        assertThat(replay.operationId()).isEqualTo(first.operationId());
        assertThat(replay.outcome()).isEqualTo(AuthorizationOutcome.APPROVED);
        assertThat(replay.requestCount()).isEqualTo(2);
        assertThat(replay.operationCount()).isEqualTo(1);
    }

    @Test
    void conflictingPayloadReturns409AtServiceBoundary() {
        registry.authorize(approval("processor-002"));

        assertThatThrownBy(() -> registry.authorize(new AuthorizationRequest(
                "processor-002", 20000, "CAD", "tok_visa_success")))
                .isInstanceOf(AuthorizationConflictException.class);

        registry.authorize(approval("processor-002"));

        AuthorizationResponse stored = registry.find("processor-002");
        assertThat(stored.amountMinor()).isEqualTo(10000);
        assertThat(stored.operationCount()).isEqualTo(1);
        assertThat(stored.requestCount()).isEqualTo(3);

    }

    @Test
    void createsTwoOperations() {
        AuthorizationResponse first =
                registry.authorize(approval("processor-001"));

        AuthorizationResponse second =
                registry.authorize(approval("processor-002"));

        assertThat(first.operationId()).isNotEqualTo(second.operationId());

        int totalOperations =
                first.operationCount() + second.operationCount();

        assertThat(totalOperations).isEqualTo(2);
    }

    @Test
    void concurrentIdenticalRequestsCreateOneOperation() throws Exception {
        AuthorizationRequest request = approval("processor-003");
        var executor = Executors.newFixedThreadPool(8);
        try {
            List<Callable<AuthorizationResponse>> calls = java.util.stream.IntStream.range(0, 16)
                    .mapToObj(ignored -> (Callable<AuthorizationResponse>) () -> registry.authorize(request))
                    .toList();

            List<AuthorizationResponse> responses = executor.invokeAll(calls).stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception exception) {
                            throw new RuntimeException(exception);
                        }
                    })
                    .toList();

            assertThat(new HashSet<>(responses.stream().map(AuthorizationResponse::operationId).toList()))
                    .hasSize(1);
        } finally {
            executor.shutdownNow();
        }

        assertThat(registry.find("processor-003").requestCount()).isEqualTo(16);
        assertThat(registry.find("processor-003").operationCount()).isEqualTo(1);
    }

    @Test
    void declineTokenCreatesDeclinedOutcome() {
        AuthorizationResponse response = registry.authorize(
                new AuthorizationRequest("processor-004", 10000, "CAD", "tok_visa_decline"));

        assertThat(response.outcome()).isEqualTo(AuthorizationOutcome.DECLINED);
    }

    private static AuthorizationRequest approval(String processorKey) {
        return new AuthorizationRequest(processorKey, 10000, "CAD", "tok_visa_success");
    }
}
