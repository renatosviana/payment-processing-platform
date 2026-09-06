# Payment processing platform

Planning baseline — Iteration 1: reliable authorization through an unreliable processor.

This repository is a learning project for payment architecture and agent-assisted engineering. The Iteration 1 planning baseline is now being implemented in bounded, reviewable tasks. The implementation is intentionally incomplete: authorization orchestration, containers, fault-injection scenarios and CI remain planned work.

## Start here

1. Read [vision](docs/vision.md), the [authorization specification](docs/specs/iteration-01-authorization.md), and the [execution plan](docs/plans/iteration-01-execution-plan.md).
2. Review [architecture](docs/architecture/iteration-01.md), [test strategy](docs/testing/iteration-01-test-strategy.md) and [threat model](docs/security/threat-model.md).
3. Review the implementation status below and inspect each task's diff and evidence before starting the next task.

## Implementation status

Completed:

- T01: Gradle multi-project skeleton with Java 17 and Spring Boot.
- T02: HTTP processor simulator with approval, decline, conflict and idempotency behavior.
- T03: PostgreSQL/Flyway payment persistence with transactional aggregate creation, uniqueness constraints and Testcontainers integration tests.

Remaining planned work:

- T04: Payment authorization API and service orchestration.
- T05-T06: Docker Compose, Toxiproxy fault experiments and end-to-end evidence.
- T07-T08: CI enforcement, operations documentation and final review.

## Local verification

Run the unit and module tests with:

```powershell
.\gradlew.bat test
```

The PostgreSQL persistence tests use Testcontainers and require Docker:

```powershell
.\gradlew.bat :payment-service:test --tests "*PaymentPersistencePostgresTest"
```

The current persistence tests verify that the payment, payment attempt and idempotency records are queryable after commit, and that constraint violations roll back the aggregate. These tests do not yet prove the full authorization flow or Docker-backed acceptance scenarios.

## Planned stack

Java 17, Spring Boot, Gradle multi-project build, Spring JDBC, PostgreSQL/Flyway, JUnit, Testcontainers and Toxiproxy. Java 17 preserves the most recent implementation baseline; a Java upgrade requires an explicit ADR (Architecture Decision Record). Exact framework, build, dependency and image versions are intentionally deferred to T01 compatibility verification. No claim is made that earlier archive versions are current or validated here.

Planned modules: `payment-service`, `processor-simulator`, `integration-tests`.

## First GitHub commit

Extract this archive into a new, empty directory. Run these commands from the directory containing this README. Create an empty GitHub repository first and replace YOUR_USERNAME below.

```bash
git init
git add .
git diff --cached --stat
git commit -m "docs: define payment platform planning baseline"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/payment-processing-platform.git
git push -u origin main
```

Review staged files before committing. If you already have a repository, use a new branch and merge these documents deliberately; do not reinitialize or overwrite existing work. This package creates no commits and does not push to GitHub.

The previous generated implementation may be consulted as reference. Do not manufacture a historical sequence of implementation commits: new commits should describe work actually performed and reviewed.
