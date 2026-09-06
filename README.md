# Payment processing platform

Planning baseline — Iteration 1: reliable authorization through an unreliable processor.

This repository is a learning project for payment architecture and agent-assisted engineering. This package contains planning documents only: application code, Gradle Wrapper, containers and CI will be implemented in bounded tasks after specification approval. No build or runtime tests have been run for this package.

## Start here

1. Read [vision](docs/vision.md) and [authorization specification](docs/specs/iteration-01-authorization.md).
2. Review [architecture](docs/architecture/iteration-01.md), [test strategy](docs/testing/iteration-01-test-strategy.md) and [threat model](docs/security/threat-model.md).
3. Use the [review checklist](docs/reviews/iteration-01-approval.md) to accept or amend the proposed decisions.
4. After approving the baseline, give an agent the [first-task prompt](docs/agents/first-task.md). It authorizes only T01 (Task 01).
5. Review each task's diff and evidence before starting the next task in the [execution plan](docs/plans/iteration-01-execution-plan.md).

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
