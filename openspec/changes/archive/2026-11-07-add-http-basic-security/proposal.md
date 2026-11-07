# Proposal

## Why

Stage 6 of the improvement plan (`plans/06-security.md`) asks for a security baseline, and today every endpoint is open: any client can create, read, update, or delete items without credentials. The drafted change kept the baseline closed with no authentication mechanism; the template owner decided instead that the baseline ships HTTP Basic with a single in-memory user read from required environment variables, so the API can actually be tried locally while staying default-closed and credential-free in the repository.

## What Changes

- Add the Spring Security starter and its Boot 4 test starter to `pom.xml`.
- Add an application-wide `security` package (mirroring `exception`) with a `SecurityConfig` that defines the modern (lambda-DSL) `SecurityFilterChain`: every request requires authentication, the API is stateless (no HTTP sessions), HTTP Basic is enabled, form login is not, and CSRF is disabled for this credentials-in-request scenario.
- Configure one in-memory user whose username and password come from `APP_SECURITY_USERNAME` and `APP_SECURITY_PASSWORD` through `application.properties` (no defaults committed). The application fails to start when either value is missing or blank. The password is stored only as a BCrypt hash, never in plain text; Spring Boot's generated default user and its logged password stay suppressed because the custom user replaces it.
- Unauthenticated requests get `401` as `application/problem+json` with a fixed generic detail, keeping the Problem Details contract of `api-error-handling` and the README ("single error format for every error this API produces"). Authorization denials get the same treatment with `403` (no role rules exist yet, so `403` is configured for derived projects but unreachable in this template).
- **BREAKING** All item CRUD endpoints now require authentication; unauthenticated calls that previously succeeded return `401`. The README curl examples and the manual persistence checklist gain credentials.
- Adapt `ItemControllerTests` so existing HTTP behavior tests run as an authenticated mock user, and add automated tests for `401` on every operation, the required-credentials startup failure, and the in-memory BCrypt storage.
- Document in the README: the new variables for terminal, IntelliJ IDEA, and VS Code; HTTP Basic's initial use and its limits (HTTPS required whenever credentials cross a network); when to re-evaluate CSRF (browsers, automatically-sent credentials); how to enable CORS when a project needs it; and what to review when deriving a project. Update the adaptation guide, AGENTS.md, and the planning files.
- Do **not** add: user registration or persistence, JWT/OAuth2, roles or business permissions, CORS by default, or `/health` and Actuator (separate stage-7 task that depends on this baseline).

## Capabilities

### New Capabilities
- `api-security`: Access-control baseline of the HTTP API — which requests need credentials, how Basic authenticates the single configured user, how rejected requests are reported, and what configuration is mandatory at startup.

### Modified Capabilities
<!-- None formally: the existing item specs describe how endpoints respond to valid,
     rejected-by-rule, or malformed requests; api-security adds the authentication
     precondition for all of them ("an authenticated principal") without changing
     any requirement text. -->

## Impact

- New: `com.example.template.security` package (`SecurityConfig` and its rejected-request handler), security test classes; `spring-boot-starter-security` and `spring-boot-starter-security-test` dependencies.
- Modified: `application.properties` and `.env.example` (required `APP_SECURITY_*`), `ItemControllerTests` (authenticated behavior + unauthenticated `401` cases), README ("Current API", quick start, testing strategy, manual checklist), `docs/adapting-the-template.md`, `AGENTS.md`, `plans/06-security.md`, `plans/README.md`.
- Behavior: the running application rejects unauthenticated traffic with `401`; local startups need the two new values in `.env` or the environment or the app refuses to start. Automated tests and CI keep needing no secrets: tests supply their own dummy credentials inline, and the real ones never enter the repository or CI logs.
- Out of scope: the role/permission experiments recorded in `plans/06-security.md` ("Options to analyze after the plan") remain follow-up decisions for after this baseline exists.
