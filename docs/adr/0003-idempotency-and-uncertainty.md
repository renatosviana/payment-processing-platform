# ADR 0003: Preserve uncertainty and operation identity

Status: Proposed. Decision owner: repository owner.

Context: DB commit and external authorization are not atomic. Repeating an ambiguous authorization can create duplicate financial effects.

Decision: Persist payment/attempt/key first; call once with a stable processor key outside a DB transaction; atomically persist the outcome afterward. Replay returns existing identity/current state. Inconclusive outcomes become UNKNOWN. Unfinished attempts after crashes are left for later recovery, with no blind redispatch.

Alternatives: Holding a transaction across HTTP increases lock duration and still cannot roll back the remote effect. Retrying under a new key is unsafe. Claiming exactly-once processing masks crash gaps.

Tradeoff: Availability is deliberately limited: some payments remain PROCESSING or UNKNOWN until Iteration 2. Simulator storage is volatile, so its guarantees apply to its current process lifetime only.

Revisit when: implementing inquiry/recovery and durable simulator state. Preserve the distinction between request counts and created operations; provider idempotency does not justify undisclosed client retries.
