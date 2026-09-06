# Execution plan

Status: all tasks NOT STARTED. Specification approval pending. Execute one task per owner assignment. Dependencies refer to completed and reviewed tasks.

| Task | Dependency | Deliverable / expected files | Acceptance and owner explanation |
| --- | --- | --- | --- |
| T01 Build skeleton | Approved baseline | settings.gradle, build.gradle, three module build files, Gradle Wrapper, minimal application entrypoints, docs/architecture/dependency-baseline.md | Clean wrapper build compiles both apps and discovers a minimal JUnit test. Pin compatible versions/checksum with official-source evidence. Explain module boundaries. |
| T02 Simulator | T01 | processor-simulator source/tests | Approval/decline/inquiry, atomic key registry, request vs operation counts, conflict test. Explain why simulator idempotency differs from merchant idempotency. |
| T03 Persistence | T01 | payment-service migration, model/repositories, DB integration tests | Real PostgreSQL validates all constraints and atomic rollback; attempt visible after commit. Explain crash gaps. |
| T04 Authorization | T02,T03 | payment-service API, service, HTTP adapter/tests | R01–R08 including sequential replay/conflict and guarded transaction boundaries. Mocked HTTP tests allowed here; do not claim end-to-end acceptance. Explain UNKNOWN. |
| T05 Containers | T04 | service Dockerfiles, compose.yaml, proxy configuration, integration harness | Compose healthy; API traffic through proxy; local-only control ports; payment restart preserves DB. Explain direct harness inquiry. |
| T06 Fault proof | T05 | integration-tests scenarios and reports | Test-strategy matrix passes, including recorded approval + delayed response, UNKNOWN replay and result-write failure. Explain independent evidence. |
| T07 CI | T06 | .github/workflows/ci.yml and build wiring | Root clean test runs all suites/images on Docker-capable runner; zero hidden skips; attach actual run URL/results when available. Explain CI limitations. |
| T08 Operations/handoff | T07 | health/metrics config, sanitized structured logs, operational runbook updates, final review | Local demo and rollback/restart documented; full acceptance evidence and owner teach-back. No cloud release. |

Expected commits follow actual reviewed work, e.g. `build: create Gradle multi-project skeleton`, `feat: add deterministic processor simulator`, `feat: persist authorization state`, `feat: implement idempotent authorization`, `test: prove ambiguous processor outcome`, `ci: enforce container verification`. Do not split or backdate history to imply a learning process that did not occur.

Task completion is not automatic authorization for the next task. Ordinary fixes required to meet the assigned task are included. If a task requires changing an approved invariant, propose the change before implementing it.
