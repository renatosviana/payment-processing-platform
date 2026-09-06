# ADR 0002: Deterministic network fault injection

Status: Proposed. Decision owner: repository owner.

Context: A processor can act successfully while the caller times out. Tests need reproducible evidence at that boundary.

Decision: Use Testcontainers to orchestrate isolated dependencies and Toxiproxy to inject downstream latency and transport failures. Use a 1500 ms zero-jitter response delay with a 500 ms response timeout for the core experiment. Remove Chaos Monkey for Spring Boot entirely.

Alternatives: Exceptions or sleeps inside business methods miss real transport behavior. Random instance termination is useful later but does not establish this specific invariant. FIT/ChAP are conceptual references, not dependencies to install.

Tradeoff: More containers, network configuration and cleanup. TCP faults do not reproduce every provider/protocol or infrastructure failure. Do not describe TCP toxics as a complete packet-level network simulator.

Revisit when: deployed infrastructure and redundancy justify AWS FIS or Kubernetes-specific experiments. Keep deterministic tests even then.
