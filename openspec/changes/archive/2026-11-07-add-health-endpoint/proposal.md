# Proposal

## Why

The application has no way to report whether it is alive or ready, so a container platform or load balancer cannot decide whether to route traffic to it, and cannot tell that PostgreSQL is unreachable. The security baseline this depends on now exists, so the health access rule can be added to it safely.

## What Changes

- Add Spring Boot Actuator and expose only the `health` endpoint, with liveness and readiness probes enabled and no detail in any response.
- Make readiness depend on database availability: the application reports `DOWN` with status `503` when PostgreSQL is unreachable, and recovers when it is back.
- Allow unauthenticated access to the health endpoints only, so probes work without credentials. Every other path, including every other management path, keeps requiring authentication.
- Report no component details, connection information, or exception messages in health responses.
- Mark with `TEMPLATE:` comments in `application.properties` and `SecurityConfig` the decisions derived projects must take: which endpoints to expose, detail level, probes, separate management port, and what to protect.
- Document in the README an operations section: health endpoints and probes, logging defaults, and metrics and tracing as optional derived-project decisions, without adding a monitoring platform.
- Move the drafted plan out of `plans/`: remove `plans/07-health.md` and update the references to it.

## Capabilities

### New Capabilities
- `operational-health`: Liveness and readiness reporting for platforms that monitor the application, with no credentials and no detail leakage.

### Modified Capabilities
- `api-security`: The rule that every request requires authentication gains one explicit exception, the health endpoints.

## Impact

- New: Actuator dependency, health configuration in `application.properties`, tests.
- Modified: `SecurityConfig` (access rule and `TEMPLATE:` note), `docs/adapting-the-template.md`, README (operations section, configuration, testing strategy counts, verification history, planned work), AGENTS.md status line, `plans/README.md`; removed: `plans/07-health.md`.
- No schema change and no change to the item routes. Unauthenticated health responses are the only new unauthenticated surface.
