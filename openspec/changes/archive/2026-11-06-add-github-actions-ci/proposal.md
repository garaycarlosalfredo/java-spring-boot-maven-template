# Proposal

## Why

Stage 5 of the template improvement plan asks for a repeatable automated build. Today the only verification is the developer running `./mvnw verify` locally; the repository has no `.github` directory and no CI. Derived projects would inherit no safety net.

## What Changes

- Add a GitHub Actions workflow `.github/workflows/ci.yml` that runs `./mvnw --batch-mode verify` on pushes to `development` and on pull requests, using JDK 25 (the `java.version` in `pom.xml`) and Maven dependency caching.
- The workflow needs no secrets and no database: automated tests use `@WebMvcTest` and mocks (see `document-testing-strategy`), so CI runs exactly what developers run locally.
- Document in the README the provider, Java version, command, triggers, and the fact that CI-specific prerequisites are none.
- Record the decision not to add formatting, static analysis, or dependency checks now, with the reason, so derived projects can add them.

Assumption: the provider is GitHub Actions, because the `origin` remote is a GitHub repository. Recorded in design.md.

## Capabilities

### New Capabilities
<!-- None: build automation only; no spec delta (skip_specs). -->

### Modified Capabilities
<!-- None. -->

## Impact

- New: `.github/workflows/ci.yml`.
- Modified docs: README.
- No application code, dependency, or credential changes.
