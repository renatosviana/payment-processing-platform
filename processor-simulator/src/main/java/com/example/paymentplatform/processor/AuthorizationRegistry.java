package com.example.paymentplatform.processor;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AuthorizationRegistry {

    private static final long MAX_AMOUNT_MINOR = 100_000_000L;
    private static final String SUPPORTED_CURRENCY = "CAD";
    private static final String APPROVAL_TOKEN = "tok_visa_success";
    private static final String DECLINE_TOKEN = "tok_visa_decline";

    private final ConcurrentMap<String, StoredAuthorization> authorizations = new ConcurrentHashMap<>();

    public AuthorizationResponse authorize(AuthorizationRequest request) {
        validate(request);

        StoredAuthorization authorization = authorizations.computeIfAbsent(
                request.processorKey(),
                ignored -> new StoredAuthorization(request));

        authorization.requestCount.incrementAndGet();
        if (!authorization.matches(request)) {
            throw new AuthorizationConflictException("processor key is already bound to a different payload");
        }

        return authorization.toResponse();
    }

    public AuthorizationResponse find(String processorKey) {
        StoredAuthorization authorization = authorizations.get(processorKey);
        return authorization == null ? null : authorization.toResponse();
    }

    public void clear() {
        authorizations.clear();
    }

    private void validate(AuthorizationRequest request) {
        if (request == null) {
            throw new InvalidAuthorizationException("request body is required");
        }
        if (request.processorKey() == null || request.processorKey().isBlank() || request.processorKey().length() > 128) {
            throw new InvalidAuthorizationException("processorKey must contain 1 to 128 characters");
        }
        if (request.amountMinor() <= 0 || request.amountMinor() > MAX_AMOUNT_MINOR) {
            throw new InvalidAuthorizationException("amountMinor must be between 1 and 100000000");
        }
        if (!SUPPORTED_CURRENCY.equals(request.currency())) {
            throw new InvalidAuthorizationException("currency must be CAD");
        }
        if (!APPROVAL_TOKEN.equals(request.paymentMethodToken()) && !DECLINE_TOKEN.equals(request.paymentMethodToken())) {
            throw new InvalidAuthorizationException("paymentMethodToken is unsupported");
        }
    }

    private static AuthorizationOutcome outcomeFor(String paymentMethodToken) {
        return APPROVAL_TOKEN.equals(paymentMethodToken)
                ? AuthorizationOutcome.APPROVED
                : AuthorizationOutcome.DECLINED;
    }

    private static final class StoredAuthorization {
        private final AuthorizationRequest request;
        private final String operationId = UUID.randomUUID().toString();
        private final AuthorizationOutcome outcome;
        private final AtomicInteger requestCount = new AtomicInteger();

        private StoredAuthorization(AuthorizationRequest request) {
            this.request = request;
            this.outcome = outcomeFor(request.paymentMethodToken());
        }

        private boolean matches(AuthorizationRequest candidate) {
            return request.amountMinor() == candidate.amountMinor()
                    && request.currency().equals(candidate.currency())
                    && request.paymentMethodToken().equals(candidate.paymentMethodToken());
        }

        private AuthorizationResponse toResponse() {
            return new AuthorizationResponse(
                    request.processorKey(),
                    operationId,
                    outcome,
                    request.amountMinor(),
                    request.currency(),
                    requestCount.get(),
                    1);
        }
    }
}
