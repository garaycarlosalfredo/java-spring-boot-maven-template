# Design

## Context

The build is a single Maven module using the Maven Wrapper, Java 25, and tests that need neither PostgreSQL nor Docker. `.env` is local and not committed; the application is not started by the tests. See proposal.md.

## Goals / Non-Goals

**Goals:**
- Run the same command as local verification on every push to `development` and every pull request.
- Keep the workflow small and free of secrets.

**Non-Goals:**
- Deployment, release publishing, or container images.
- Integration tests with a database (not adopted by the testing strategy).
- Formatting checks, static analysis, or dependency vulnerability scanning in this change.

## Decisions

- **GitHub Actions.** The remote is on GitHub, so no extra service is needed. Alternatives (GitLab CI, others) are left to derived projects.
- **Triggers:** `push` to `development` and `pull_request` unfiltered, so every pull request is checked before merging. Manual dispatch is omitted to keep it minimal. Updated on 2026-11-06 by user decision: `development` is the deploy branch, and pushes to `main` should not run the check.
- **Java:** `actions/setup-java` with the `temurin` distribution, version 25, and `cache: maven`. Pin the action to a major version tag.
- **Command:** `./mvnw --batch-mode verify`, the same as local, with no profile.
- **Permissions:** `contents: read` only.
- **No extra quality tools now.** Each adds configuration to maintain and opinions to derived projects; the plan asks to include one only if useful and maintainable, and none solves a current need. Revisit with the next stage that needs it.

## Risks / Trade-offs

- A local build does not prove CI works, and I cannot run GitHub Actions from here → validation requires the user to push a branch and confirm the run; the change is not complete until that result is recorded.
- The `development` branch does not exist yet locally or on `origin` (the default branch is `main`) → the push trigger stays idle until the branch is created and pushed; pull requests run the check regardless.
- JDK 25 availability on the runner could change → the setup action fails clearly; adjust the version then.
