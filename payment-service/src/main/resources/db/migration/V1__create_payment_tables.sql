CREATE TABLE payment (
    id UUID PRIMARY KEY,
    merchant_id VARCHAR(128) NOT NULL,
    order_id VARCHAR(128) NOT NULL,
    amount_minor BIGINT NOT NULL CHECK (amount_minor > 0 AND amount_minor <= 100000000),
    currency VARCHAR(3) NOT NULL CHECK (currency = 'CAD'),
    status VARCHAR(16) NOT NULL CHECK (status IN ('PROCESSING', 'AUTHORIZED', 'DECLINED', 'UNKNOWN')),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE payment_attempt (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL UNIQUE REFERENCES payment(id),
    processor_key VARCHAR(128) NOT NULL UNIQUE,
    status VARCHAR(16) NOT NULL CHECK (status IN ('SUBMITTED', 'SUCCEEDED', 'DECLINED', 'UNKNOWN')),
    processor_reference VARCHAR(128),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE idempotency_record (
    merchant_id VARCHAR(128) NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    request_fingerprint CHAR(64) NOT NULL,
    payment_id UUID NOT NULL REFERENCES payment(id),
    created_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (merchant_id, idempotency_key)
);
