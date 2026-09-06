# Initial threat model

This is an isolated local laboratory, not a PCI-compliant payment product. Accept synthetic aliases only and reject unknown JSON fields. A field whitelist helps reject card fields but does not make arbitrary free text safe to log. Never log request bodies, aliases, idempotency keys, authorization headers or raw exceptions containing payloads.

| Threat/boundary | Iteration 1 mitigation | Before remote use |
| --- | --- | --- |
| Impersonated merchant / cross-merchant GET | Trusted synthetic callers; bind published ports to 127.0.0.1 | Authentication; derive merchant from identity; tenant-scoped reads/writes |
| Duplicate operation | DB uniqueness, fingerprint comparison, stable processor key, no retries | Concurrency/crash proof and recovery |
| Lost processor response | UNKNOWN with independently verified simulator outcome | Status inquiry/reconciliation |
| Card or secret leakage | Strict DTO/alias validation; sanitized errors; no request-body logs | Data classification, audited secret management and compliance review |
| Fault control abuse | Toxiproxy control port isolated/local; never publicly exposed | Separate authenticated experiment control with scoped authorization |
| Dependency/container supply chain | T01 pins compatible versions; wrapper checksum; CI minimal permissions | Ongoing vulnerability/upgrade process |
| SQL injection | Parameterized JDBC; schema constraints | Review all new query paths |
| Resource abuse | Bounded request fields, finite timeouts, no unbounded retries | Authentication, rate/concurrency limits, quotas |

Use local-only development credentials clearly labeled as such. Exclude .env and secrets from Git; a future .env.example may contain placeholders. CI uses ephemeral containers and requires no real provider secrets. No external telemetry exporters or production data are needed.

Port publication and container networking must make a processor bypass unavailable to the payment service's configured adapter. Direct harness inquiry is allowed only for evidence. Do not publish management endpoints broadly.
