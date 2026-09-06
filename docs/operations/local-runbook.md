# Deployment and monitoring plan

Status: planned, not operational. Executable commands/configuration arrive in T05–T08.

Initial deployment means disposable localhost Docker Compose. Before running, check available ports, Docker readiness, pinned images and adequate resources. Start PostgreSQL, simulator, Toxiproxy and payment service with health-based readiness. Publish only necessary localhost ports. Never expose fault control to the internet.

Planned demo: start stack → authorize success → verify DB/GET → add downstream latency → authorize with a new key → observe UNKNOWN → inquire on simulator → replay original key and confirm unchanged call count → remove toxic → verify a fresh approval. Always remove toxics after a failed experiment.

Observe structured events for attempt submitted, processor result observed and outcome persisted; include payment/attempt correlation IDs, outcome and duration. Do not include request bodies, token aliases or keys. Proposed metrics: authorization outcomes, processor latency/timeouts, and age/count of PROCESSING and UNKNOWN records. Keep payment IDs out of metric labels to avoid unbounded cardinality. Health endpoints expose minimal status; DB readiness and process liveness are distinct.

When an outcome is UNKNOWN or stranded PROCESSING, preserve evidence and do not resubmit manually. Iteration 1 has no recovery command; record the limitation and use a fresh synthetic order only for unrelated demos. Agent log review remains read-only and uses sanitized evidence supplied by the owner.

Rollback: remove faults first, collect logs, stop or return to the prior compatible application image while preserving the database volume. Never assume an older image is compatible with a changed schema. Do not automatically reverse migrations or run volume-deleting Compose commands. Destructive reset requires explicit owner authorization.

CI is a verification gate, not production deployment. A future remote release requires a separate design for authentication, secret management, deployment approval, rollback compatibility and operational ownership.
