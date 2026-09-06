# First supervised exercise: T01

Use this prompt only after reviewing and approving the planning baseline. Sending it with approval authorizes T01; this file alone is not authorization.

> I approve the Iteration 1 planning baseline at the current repository revision. Execute T01 only. Read AGENTS.md and the execution plan. Before editing, list intended files and verification commands. Create the Gradle multi-project skeleton for payment-service, processor-simulator and integration-tests using Java 17, Spring Boot and Java/JUnit. Verify compatible stable dependency/build versions against official documentation; pin the Gradle Wrapper distribution checksum and record the decision and sources in docs/architecture/dependency-baseline.md. Include a minimal startup entrypoint for each app and one meaningful build-discovery smoke test. Do not implement payment business logic, SQL migrations, simulator endpoints, containers, fault experiments or CI yet. Run the clean build available for this skeleton and report exact results and limitations. Review your diff for scope creep. Stop with a reviewable handoff after T01; do not start T02.

Owner review: Can you explain why there are three projects, why a separate simulator matters, what the smoke test proves, and what it cannot prove? If not, ask the agent to walk through the relevant files before assigning another task.
