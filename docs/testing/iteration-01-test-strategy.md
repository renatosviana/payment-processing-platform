# Test strategy

Status: planned; no application tests exist in this package.

Use Java/JUnit for invariants, real PostgreSQL/Flyway through Testcontainers for persistence, and containerized services/Toxiproxy for black-box scenarios. Do not replace PostgreSQL with H2 for constraint evidence. Each scenario uses unique keys and clears its toxics in a finally/cleanup block; serialize shared-proxy fault tests or give each test its own proxy.

| Case | Requirements | Required evidence |
| --- | --- | --- |
| Approval | R01–R02, R05–R07 | 201 AUTHORIZED; SUCCEEDED attempt; one simulator request and operation |
| Decline | R05, R09 | 201 DECLINED; matching attempt; explicit simulator decline |
| Invalid input | R01 | 400; zero new DB records and processor calls; includes decimal/overflow amount, unsupported alias, currency and unknown PAN/CVV fields |
| Sequential replay | R04, R06 | Stable payment/attempt IDs, unchanged counts; JSON field ordering does not affect fingerprint |
| Key conflict | R04 | Changed amount/token/order gives 409; original payment unchanged; no extra call |
| Merchant key isolation | R04 | Same key for two synthetic merchants produces independent payments |
| Processor idempotency | R09 | Two direct simulator POSTs with same key produce one operation; changed payload gives 409 |
| Ambiguous approval | R07, R10 | 1500 ms downstream delay vs 500 ms timeout → 202 UNKNOWN; independent inquiry proves APPROVED; DB attempt UNKNOWN |
| Replay UNKNOWN | R04, R07 | Same IDs, 202 UNKNOWN; no additional processor POST |
| Transport/protocol errors | R07 | Reset/unavailable processor, malformed response or 5xx maps to UNKNOWN, never DECLINED; no retries |
| Payment restart | R08 | Restart only payment container, preserve DB; GET returns same state and IDs |
| Transaction boundaries | R06, R08 | During delayed response, independent DB connection sees committed SUBMITTED; no transaction held over HTTP; DB outage before creation causes zero dispatches |
| Result-write failure | R08 | After simulator records approval, fail result persistence; no false success, original identity preserved, no dispatch on replay |
| Container smoke/CI | R11 | Clean clone build; all containers healthy; Docker suite executed with zero unexpected skips |

The result-write failure can use a deterministic repository failpoint in test configuration; never expose that failpoint in a normal runtime profile. A code review must also verify transaction placement and disabled retries because timing tests alone cannot establish these properties.

Target command after implementation: `./gradlew clean test`. The root test task must include integration-tests and build required images before running them. CI must fail when Docker is missing, not silently skip integration tests. Local Docker limitations may be reported as BLOCKED but never satisfy completion. Publish JUnit XML and sanitized container logs on CI failure.

Latency test: create the downstream toxic before POST; wait for application UNKNOWN and poll simulator evidence with a bounded deadline. Assert statuses and counts, not exact elapsed milliseconds. Always remove the toxic and verify a normal request afterward. A timeout alone does not prove the processor acted.

Iteration 2: 20 simultaneous identical requests, conflicting concurrent payloads, crash-point recovery and idempotent processor-status inquiry. Keep these explicitly pending.
