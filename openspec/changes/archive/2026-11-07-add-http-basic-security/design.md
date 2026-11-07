# Design

## Context

See proposal.md — Why, and specs/api-security/spec.md — Requirements.

Constraints that shape the approach:

- Spring Boot 4.1.2 with Spring Security 7: the lambda DSL is the only API (non-lambda overloads were removed), `InMemoryUserDetailsManager` is current and documented for non-persistent users, and Boot 4 split test support into modular starters — `@WithMockUser` and the MockMvc↔security integration require `spring-boot-starter-security-test`, not the old plain `spring-security-test`.
- The repository's error contract states Problem Details is the single error format for every error the API produces (README, `api-error-handling` spec). Spring Security rejections happen in the filter chain, before MVC, so `ApiExceptionHandler` never sees them.
- Automated tests must not need PostgreSQL, secrets, or the network (README "Testing strategy"); CI runs `./mvnw verify` with no environment variables. Today `@WebMvcTest(ItemController.class)` with a mocked service covers 45 HTTP tests, including an unmapped-root `404`.
- Local configuration follows the existing pattern: required values in `application.properties` as unresolved placeholders (`${DB_URL}` style) supplied by environment or the optional `.env` import; `.env.example` carries generic placeholders only.
- The `exception` package is the precedent for application-wide, non-feature code; `plans/06-security.md` drafted the same placement for security.

## Goals / Non-Goals

**Goals:**
- Closed by default and immediately usable locally: one Basic user from required environment values, fail-fast when they are absent or blank.
- One error format everywhere, including filter-chain rejections (`401`/`403` as `application/problem+json`).
- Every decision a derived project must revisit is visible as a `TEMPLATE:` comment in the code and as README guidance.
- Tests keep the no-secrets/no-database policy: dummy credentials inline, real ones never enter the repository, CI, or logs.

**Non-Goals:**
- Roles, permissions, `403`-producing rules, user persistence, JWT/OAuth2, CORS enablement, Actuator/`/health` (stage 7 depends on this baseline).
- Making the `403` path reachable: the handler is configured and unit-tested, but no rule can deny an authenticated request yet. The role experiments stay recorded in `plans/06-security.md` as follow-up decisions.

## Decisions

- **Application-wide `security` package, one `SecurityConfig` class.** Mirrors `exception`: security is cross-cutting configuration, not a feature. `SecurityConfig` owns the filter chain, the rejected-request handler wiring, the `PasswordEncoder`, and the in-memory `UserDetailsService`. Alternative considered: splitting user provisioning into a second configuration class so the web slice could load the chain without credential properties — rejected as a speculative split (AGENTS.md: abstractions only for concrete needs); tests can supply dummy properties inline instead.
- **Filter chain:** `authorizeHttpRequests` with `anyRequest().authenticated()`, `httpBasic` enabled, `sessionManagement` `STATELESS`, `csrf` disabled, form login not enabled. `TEMPLATE:` comments mark each derived-project decision: authentication mechanism, authorization rules and `403` handling, CSRF re-evaluation (required again for cookie/session or browser-credential auth), CORS enablement, security headers, secret handling, and operational endpoints when Actuator arrives.
- **Rejected-request handler:** one small class in `security` implementing both `AuthenticationEntryPoint` and `AccessDeniedHandler`, wired through `exceptionHandling`. It writes a fixed-detail `ProblemDetail` (`401` "Authentication required" with a `WWW-Authenticate: Basic realm=...` header; `403` "Access denied") and never echoes exception or configuration values. Alternative considered: leaving Spring Security's defaults (empty-body `401`/`403`) — rejected because it breaks the single-error-format contract.
- **Custom `UserDetailsService` instead of `spring.security.user.*`.** Boot's property-based user uses the deprecated `User.withDefaultPasswordEncoder()` (no BCrypt) and its auto-configuration generates and logs a random password when nothing is configured — both violate the agreed requirements. A `UserDetailsService` bean also suppresses that auto-configuration. `InMemoryUserDetailsManager` with one `UserDetails`: username as configured, password encoded with a `BCryptPasswordEncoder` bean at startup, no authorities (no roles exist). The plain value is never stored; only the hash lives in memory.
- **Required credentials:** `app.security.username=${APP_SECURITY_USERNAME}` and `app.security.password=${APP_SECURITY_PASSWORD}` in `application.properties`, no defaults, next to a `TEMPLATE:` comment. Missing value → unresolved placeholder fails startup exactly like `DB_URL` does today. Blank value → explicit validation when the user bean is created, throwing with a message naming the property, never the value. Validated in the bean factory path so the failure happens during context startup, before the port opens.
- **Tests:**
  - `ItemControllerTests`: add `spring-boot-starter-security-test`; existing operation groups run under `@WithMockUser` (class-level) so the 45 contract tests keep passing, including root `404`. Verify at apply whether the `@WebMvcTest` slice picks up `SecurityConfig` automatically; if not, `@Import(SecurityConfig.class)` and supply dummy `app.security.*` values as inline test properties. A new `@Nested` group asserts that every operation — and the root — without credentials returns `401` `application/problem+json` with the Basic challenge and never calls the service.
  - New `SecurityConfigTests` (plain JUnit / `ApplicationContextRunner`, no web layer, no database): blank username or password fails bean creation; a valid configuration stores a BCrypt hash (`$2` prefix), not the plain value; the handler writes the fixed problem bodies.
  - The missing-variable case stays covered by the placeholder mechanism plus manual live verification — the same approach `DB_URL` uses today, keeping tests free of environment dependencies.
- **Documentation:** README gains the two variables in the database-configuration table style, a security section (Basic usage and limits, HTTPS whenever credentials cross a network, IDE-agnostic setup: the `.env` import works from any tool that runs with the repository root as working directory — terminal, `spring-boot:run`, JAR, IntelliJ IDEA, VS Code — plus per-IDE environment-variable fields), CSRF re-evaluation triggers, CORS enablement guidance, and the derived-project review checklist; curl examples gain `-u`. `docs/adapting-the-template.md`, `AGENTS.md`, `plans/06-security.md`, and `plans/README.md` are updated to reflect the implemented baseline.

## Risks / Trade-offs

- [`@WebMvcTest` slice behavior with Boot 4 security is version-sensitive: without the test starter the chain is silently skipped; with it, auto-configuration applies] → the test starter is a declared dependency, and the apply step verifies the slice actually enforces the chain (an unauthenticated test must fail if it does not); `@Import` is the documented fallback.
- [Every local startup now fails until `.env` carries the two new values — an intentional fail-fast, but a friction point for anyone mid-migration] → `.env.example`, README quick start, and the adaptation guide are updated in the same change; the failure message names the missing property.
- [Basic credentials travel base64-encoded, not encrypted] → README states HTTPS is required whenever credentials cross a network; the baseline is for local/derivable use, with `TEMPLATE:` comments pointing at stronger mechanisms.
- [Stateless + CSRF disabled is wrong for cookie/session browser apps] → the `TEMPLATE:` comment and README section state exactly when to re-enable CSRF and why the baseline disables it.
- [Single shared user: no per-user attribution, no revocation granularity] → accepted for a template; user persistence and roles are explicit follow-up options in `plans/06-security.md`.
- [`403` handler is configured but unreachable, so it can only be unit-tested] → stated as a limitation in README; the role option in the plan makes it reachable later.

## Migration Plan

No data or schema migration. Behavior change for running deployments: unauthenticated requests start returning `401`. Rollout within this change: update `.env.example` and docs first-class in the same commit set; local operators add `APP_SECURITY_USERNAME`/`APP_SECURITY_PASSWORD` to their ignored `.env` (never committed). Rollback is reverting the change; nothing persists.

## Open Questions

None that affect the specs, approach, or tasks. Wording details (realm string, exact problem titles) follow the fixed-detail style of `ApiExceptionHandler` and can be settled at apply.
