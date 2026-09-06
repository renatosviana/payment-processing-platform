# Vision and scope

Build an explainable payment authorization platform incrementally. The technical owner decides requirements and tradeoffs; the coding agent implements bounded tasks and supplies evidence. Success is the owner's ability to explain behavior under failure, not the quantity of generated code.

Iteration 1 is a single-instance, local laboratory. It includes authorization, durable payment/attempt/idempotency records, an external simulator, deterministic tests, Toxiproxy, CI and local operational evidence.

Excluded: capture, refunds, ledger, Kafka/outbox, webhooks, multiple processors, reconciliation/recovery workers, merchant onboarding, real payment rails, cloud deployment, Chaos Mesh, AWS FIS, and instance termination.

A future design exercise may consider 100 million payments/day (about 1,157 average requests/second, rounded to 1,200) and 12,000 peak requests/second. These are hypothetical capacity targets, not measurements, promises, or Iteration 1 acceptance gates.

Learning outcomes: distinguish a logical payment from an external attempt; explain database versus network atomicity; understand idempotency and ambiguous outcomes; direct an agent through planning, execution, review and operational feedback.
