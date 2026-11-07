# Design

## Context

See proposal.md for motivation and specs/ for requirements.

- The application has no Actuator. `SecurityConfig` (package `security`) owns one `SecurityFilterChain` with `anyRequest().authenticated()`, HTTP Basic, stateless sessions, and a `TEMPLATE:` note saying that operational endpoints need explicit rules when Actuator arrives.
- Rejections use `ProblemDetailSecurityHandler`. Health responses are produced by Actuator itself, not by that handler or `ApiExceptionHandler`, so their format is Actuator's own and is kept minimal on purpose.
- Automated tests must not need PostgreSQL, secrets, or the network (README "Testing strategy"). `ItemControllerTests` runs `@WebMvcTest(ItemController.class)` with the real `SecurityConfig` imported and inline dummy credentials; that slice does not load Actuator.
- The local PostgreSQL is an external Docker environment shared with the Node backend.
- Configuration follows the existing pattern: properties in `application.properties` with `TEMPLATE:` comments.

## Goals / Non-Goals

**Goals:**
- Probes usable by container platforms without credentials.
- No information disclosure from any health response.
- A small, auditable exception to the closed-by-default rule.

**Non-Goals:**
- Metrics, tracing, dashboards, or a monitoring platform (documented as options only).
- Authenticating the health endpoints, custom health indicators, or graceful-shutdown tuning.
- A separate management port (documented as a derived-project decision).

## Decisions

- **Actuator exposing only `health`.** `management.endpoints.web.exposure.include=health`, so `env`, `beans`, and the rest are never exposed. Alternative: expose more and protect it; rejected as speculative and riskier.
- **Probes enabled with a database-backed readiness group.** `management.endpoint.health.probes.enabled=true`; liveness has no dependency checks; the readiness group includes `readinessState` and `db`, so readiness is `DOWN` (`503`) while PostgreSQL is unreachable and recovers on its own.
- **No details.** `management.endpoint.health.show-details=never` (and components follow it), so a response never reveals the database host or an error.
- **Same port.** No separate management port; a `TEMPLATE:` comment tells derived projects to consider a separate port or a network rule in production.
- **Access rule as an explicit path matcher.** In the existing chain, permit `/actuator/health/**` before `anyRequest().authenticated()`. Alternative: Actuator's `EndpointRequest` matcher, which follows a changed base path; rejected because it needs Actuator's beans, which the `@WebMvcTest` slice does not load, and the explicit path keeps the rule testable in the slice. A `TEMPLATE:` note says to update it if the management base path changes. Any other management path falls through to `authenticated()`.
- **Unauthenticated health on the same chain.** HTTP Basic stays enabled; permitted paths simply skip authentication. No second filter chain, to keep one place for all rules.
- **Tests.** In the web slice, with `SecurityConfig` imported: an unauthenticated request to `/actuator/health/**` is not rejected with `401` (the slice has no Actuator endpoint, so the path is not found rather than unauthorized, which proves the rule), unauthenticated `/actuator/env` and item requests still return `401`, and the Problem Details format is unchanged. Whether the live health body has no details, whether readiness includes the database, and the `503` transition cannot be proven without Actuator and a database; they are verified manually and stated as such in the README.
- **Logging.** Keep Spring Boot defaults (console, `INFO`); document how to adjust levels and that log content must never include credentials or request values. No logging framework change.
- **Documentation placement.** A README "Operations" section for the endpoints, probes, logging, and optional metrics and tracing; `TEMPLATE:` comments carry the code-level decisions; `plans/07-health.md` is removed once the change exists.

## Risks / Trade-offs

- [Readiness `DOWN` while the database restarts can make a platform stop routing traffic] → intended and documented.
- [Unauthenticated health is a new open surface] → only `health` is exposed, with no details, and the spec requires every other path to stay closed.
- [Automated tests cannot prove the database indicator] → verified manually and recorded; the gap is stated in the README.
- [The explicit matcher can drift from the Actuator base path] → `TEMPLATE:` note and a README line.
- [Manual verification needs the shared PostgreSQL container stopped, which also affects the Node backend] → the user performs or approves that step; the automation never stops the container on its own.

## Migration Plan

No data or schema migration. Behavior change: new unauthenticated paths under `/actuator/health`. Rollback is reverting the change; nothing persists.
