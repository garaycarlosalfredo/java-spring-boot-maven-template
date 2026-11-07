# Tasks

## 1. Configuration

- [x] 1.1 Add `spring-boot-starter-actuator` to `pom.xml` with the version managed by the Boot parent; verify with `./mvnw -q compile` and `./mvnw -q test-compile`.
- [x] 1.2 Configure `application.properties`: expose only `health`, enable probes, add `db` to the readiness group, and set `show-details=never`, each with a `TEMPLATE:` comment on what derived projects decide (exposure, detail level, probes, separate management port); verify the keys are present and no other endpoint is exposed.
- [x] 1.3 Permit `/actuator/health/**` without authentication in `SecurityConfig`, before `anyRequest().authenticated()`, and update the `TEMPLATE:` note on operational endpoints (base-path drift, separate port); verify the chain still compiles and all other requests remain authenticated.

## 2. Tests

- [x] 2.1 Add tests to the web slice with `SecurityConfig` imported: an unauthenticated request to a health path is not rejected with `401`, while unauthenticated `/actuator/env` and item requests still return `401` `application/problem+json`; verify they pass with no database and no real secrets.
- [x] 2.2 State in the README testing strategy what automated tests cannot cover (health body without details, database-backed readiness, the `503` transition) and that it is verified manually; verify the new counts match the Surefire reports.

## 3. Documentation

- [x] 3.1 Add a README "Operations" section: health endpoints and probes, no-detail rule, logging defaults, optional metrics and tracing as derived-project decisions, and deployment items left open (secret injection, management port, infrastructure); verify each documented URL and property matches the implementation.
- [x] 3.2 Update `docs/adapting-the-template.md` (health entry from planned to implemented, with the review points), the AGENTS.md status and scope lines, and the README planned-work list; the user confirmed `plans/README.md` and `plans/07-health.md` were intentionally removed. Remove remaining documentation links to those files and verify no link points to them.

## 4. Verification

- [x] 4.1 Run `./mvnw clean verify` from the repository root and report which checks executed and how many tests passed.
- [x] 4.2 Manually verify against local PostgreSQL: liveness and readiness return `200` `UP` without credentials with a body holding only the status; `/actuator/env` returns `401` without credentials and is not available with them; an item path still returns `401`; with the user's approval, stop and start the PostgreSQL container to confirm readiness changes to `503` `DOWN` and recovers; stop the application by its exact PID, confirm the port is free, and record a dated entry in the README "Verification history".
