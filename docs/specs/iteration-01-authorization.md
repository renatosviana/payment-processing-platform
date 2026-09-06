# Iteration 1 authorization specification

Status: PROPOSED — owner approval pending. Requirement IDs are stable review references.

## Contract

R01. `POST /v1/payments` requires `Idempotency-Key` (1–128 characters) and exactly these JSON fields:

```json
{
  "merchantId": "merchant-001",
  "orderId": "order-8271",
  "amountMinor": 10000,
  "currency": "CAD",
  "paymentMethodToken": "tok_visa_success"
}
```

Amounts are positive signed 64-bit integers, with a laboratory upper bound of 100000000 minor units. CAD is the only supported currency this iteration. IDs are nonempty, bounded to 128 characters; whitespace is not silently normalized. Reject unknown JSON fields and unsupported aliases. Only `tok_visa_success` and `tok_visa_decline` are accepted; network latency comes from Toxiproxy, not token-triggered sleep. Reject invalid input with 400 before creating records or contacting the processor.

R02. A new completed authorization returns 201 and `Location: /v1/payments/{paymentId}`. A new payment with an ambiguous result returns 202 with status UNKNOWN and the same Location form. Decline is a business outcome returned as 201 with DECLINED, not an HTTP server error. Response fields: paymentId, attemptId, merchantId, orderId, amountMinor, currency, status, createdAt, updatedAt. Do not expose the payment token or internal error details.

R03. `GET /v1/payments/{paymentId}` returns 200 with the current persisted representation or 404. GET never calls the processor. Iteration 1 is localhost-only with a trusted synthetic caller; merchant identity supplied in a body is not authentication. Remote deployment is blocked until authenticated merchant scope applies to both endpoints.

R04. Key scope is `(merchantId, idempotencyKey)`. Canonicalize validated field values in fixed field order before hashing, independent of JSON whitespace/key order. All five fields participate. Same key and same fingerprint returns the same payment and attempt, with current persisted state, and makes no new processor call. Return 200 for an existing AUTHORIZED/DECLINED payment and 202 for PROCESSING/UNKNOWN. Idempotency guarantees stable operation identity, not identical HTTP status on every replay. Different fingerprint returns 409. Keys do not expire in this iteration.

## State and invariants

R05. Payment: PROCESSING → AUTHORIZED | DECLINED | UNKNOWN. Attempt: SUBMITTED → SUCCEEDED | DECLINED | UNKNOWN. Persist matching outcomes atomically. UNKNOWN is not terminal in the future platform but has no automated outgoing transition in Iteration 1.

R06. One payment has one authorization attempt in this iteration. Payment, SUBMITTED attempt and idempotency record are committed in one short transaction before any external call. Only the request that creates that attempt dispatches it. A duplicate request reads the existing state. Unique constraints protect `(merchantId, key)`, `attempt.paymentId` and `attempt.processorKey`. Roll back a uniqueness conflict and load the winner in a new transaction; never continue using an aborted PostgreSQL transaction.

R07. External request uses a stable processor key derived from the immutable attempt ID. No automatic HTTP client retries, redirects or fallback routing. An inconclusive timeout, reset, malformed response or 5xx is UNKNOWN; an explicit valid processor decline is DECLINED. One key must never create two processor operations.

R08. Restarting the payment service preserves database records. If the process dies after committing SUBMITTED, or after processor success but before recording the result, records may remain PROCESSING/SUBMITTED. Replays do not dispatch them again. Document and observe these stranded states; automatic recovery and status inquiry by the payment service are Iteration 2 work. Do not claim exactly-once delivery across systems.

## Simulator and fault boundary

R09. Simulator exposes `POST /authorizations` with processorKey, amountMinor, currency, paymentMethodToken; `GET /authorizations/{processorKey}` returns the stored outcome, or 404. POST must atomically record APPROVED or DECLINED before replying. Same key/body replays the stored outcome; different body returns 409. Keep per-key request and created-operation counts available to the test harness through the inquiry response. Registry may be process-local, thread-safe and reset on container recreation: restart durability of the simulator is explicitly not guaranteed.

R10. All payment-service-to-processor traffic uses Toxiproxy. A test harness may inquire directly on the isolated simulator control path to establish external evidence; the payment application must never use that bypass. Client response timeout: 500 ms; injected downstream response latency: 1500 ms with zero jitter. Timeout must produce UNKNOWN; simulator inquiry must independently prove one recorded APPROVED operation.

R11. Iteration 1 acceptance requires all cases in the test strategy to pass, including Docker-backed tests in CI. Earlier implementation archives are not verification evidence for this baseline.
