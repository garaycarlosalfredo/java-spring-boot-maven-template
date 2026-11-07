# Design

## Context

AGENTS.md states that automated tests use mocks and `@WebMvcTest` and must not require PostgreSQL or Testcontainers, and that persistence is verified manually against the external local PostgreSQL. The pom has only the web MVC test starter and JUnit; there is no Testcontainers dependency. See proposal.md.

## Goals / Non-Goals

**Goals:**
- One place that says what is automated and what is manual.
- A reproducible manual checklist for persistence.

**Non-Goals:**
- Adding Testcontainers, an in-memory database, or Docker to the build.
- Running tests in CI (stage 5).

## Decisions

- **Keep the policy, document the extension point.** The user chose "document only". The README explains, for derived projects, how an integration test would be separated from `verify` (a dedicated profile or naming convention such as `*IntegrationTests`), uses its own disposable database, and cleans up after itself. This is advice, not code.
- **Manual checklist lives in the README** next to the verification commands, not in a script, because AGENTS.md forbids custom startup scripts and the checks need the user's local database.
- **Gap review before writing the table.** Compare each behavior in the item specs with the tests that exercise it, so the table reflects reality rather than the docs.

## Risks / Trade-offs

- Mock-based tests cannot catch mapping or migration errors → accepted and stated explicitly; the manual checklist is the mitigation.
- A documented but unautomated checklist can go stale → each persistence-affecting change must record its manual verification, as AGENTS.md already requires.
