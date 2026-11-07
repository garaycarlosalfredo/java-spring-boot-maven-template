# Proposal

## Why

Stage 9 of the template improvement plan asks that it be clear how to create and maintain a project derived from this repository. Observed today:

- `pom.xml` still uses the Initializr coordinates (`com.example`, `spring-boot-template`) and has an empty `<license />`; the repository has no `LICENSE` file.
- The Java package `com.example.template`, the example `items` feature, the Flyway migrations, and the `DB_*` environment variables all need adapting, but no document lists them.
- The README mixes current behavior with historical verification notes and does not say which security, operations, and integration choices a derived project must make before deployment.

## What Changes

- Add an "Adapting the template" section (or a dedicated `docs/` page linked from the README) with a checklist covering: Maven coordinates and artifact name, application name and main class, Java package rename, `application.properties` and `TEMPLATE:` comments to review, environment variables and `.env.example`, database and schema ownership, and what to do with the example `items` feature (keep as reference, rename, or remove, including its migrations and tests).
- Add a "Before deployment" list that links the open choices of the other stages: security (authentication mechanism, roles, CSRF/CORS, secrets), operations (health exposure, logging, metrics), and service integrations, without claiming they are implemented.
- Explain how to run the application and tests and which checks need external services (PostgreSQL) or are not automated, linking to the testing strategy.
- Review the README so that current behavior, historical verification notes, and links to the final conventions are separated and accurate.
- **License:** the owner chose the MIT license. Add a `LICENSE` file with the MIT text and update the `<licenses>` metadata in `pom.xml` and the README to match. The notice reads `Copyright (c) 2026 Carlos Garay`, as confirmed by the owner.
- Walk through the checklist on a disposable derived copy (outside the repository) and correct steps that are missing or misleading.

## Capabilities

### New Capabilities
<!-- None: documentation and metadata only; no spec delta (skip_specs). -->

### Modified Capabilities
<!-- None. -->

## Impact

- Modified docs: README, AGENTS.md (pointer); possibly a new `docs/` page.
- New `LICENSE` file (MIT) and `<licenses>` in `pom.xml`, with the confirmed copyright holder.
- No application code, dependencies, or schema changes.
