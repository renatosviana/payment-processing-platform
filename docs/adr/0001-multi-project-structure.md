# ADR 0001: Gradle multi-project laboratory

Status: Proposed. Decision owner: repository owner.

Context: We need one reviewable repository and a genuine network boundary with an external processor, while retaining Java-based learning and Gradle.

Decision: Use payment-service, processor-simulator and integration-tests projects in one Gradle build. Payment logic remains a modular monolith. Simulator is test infrastructure, not another production business service.

Alternatives: An in-process mock is simpler but hides network failure. Separate repositories add release coordination without a present need. Maven conflicts with the owner's explicit Gradle choice.

Tradeoff: Containerized integration tests are slower and require Docker. Benefits are reproducibility and realistic dependency failures.

Revisit when: independently released teams or production components justify repository/service separation. Exact dependency versions are selected with compatibility evidence in T01.
