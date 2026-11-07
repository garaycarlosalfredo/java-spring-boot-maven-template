# Tasks

## 1. Dependencies and configuration

- [x] 1.1 Add `spring-boot-starter-security` (compile) and `spring-boot-starter-security-test` (test) to `pom.xml` with versions managed by the Boot parent; verify dependency resolution and compilation with `./mvnw -q compile` and `./mvnw -q test-compile`.
- [x] 1.2 Add `app.security.username=${APP_SECURITY_USERNAME}` and `app.security.password=${APP_SECURITY_PASSWORD}` to `application.properties` — no defaults, next to a `TEMPLATE:` comment marking the credential source as a derived-project decision; verify the lines follow the existing `${DB_URL}` placeholder pattern.
- [x] 1.3 Add `APP_SECURITY_USERNAME=<api-username>` and `APP_SECURITY_PASSWORD=<api-password>` generic placeholders to `.env.example`; verify the file still contains placeholders only, with no real or default values.

## 2. Security configuration and its tests

- [x] 2.1 Create the application-wide `security` package with `SecurityConfig` defining the lambda-DSL `SecurityFilterChain`: `anyRequest().authenticated()`, `httpBasic` enabled, `sessionManagement` `STATELESS`, `csrf` disabled, no form login; add `TEMPLATE:` comments for the derived-project decisions (authentication mechanism, authorization rules and `403`, CSRF re-evaluation for cookie/browser auth, CORS enablement, security headers, secret handling, operational endpoints); verify the application context compiles and the chain bean is declared.
- [x] 2.2 Add the rejected-request handler in `security` implementing `AuthenticationEntryPoint` and `AccessDeniedHandler`, wired through `exceptionHandling`: `401` `application/problem+json` with the fixed detail "Authentication required" and a `WWW-Authenticate: Basic` challenge, `403` with the fixed detail "Access denied"; never echo exception or configuration values; verify with a unit test asserting both problem bodies, content type, and the challenge header.
- [x] 2.3 Add the single in-memory user: a `BCryptPasswordEncoder` bean and a `UserDetailsService` bean building one `UserDetails` (configured username, password encoded at startup, no authorities), with fail-fast validation that rejects a blank username or password during context startup with a message naming the property, never the value; verify with plain unit tests: blank username fails, blank password fails, and a valid configuration stores a BCrypt hash (`$2` prefix) instead of the plain value.

## 3. HTTP contract under security

- [x] 3.1 Adapt `ItemControllerTests`: run the existing operation groups as an authenticated mock user (`@WithMockUser`), confirm the `@WebMvcTest` slice actually enforces the chain (an unauthenticated request must not pass silently; if the slice does not pick up `SecurityConfig`, `@Import` it and supply dummy `app.security.*` values as inline test properties); verify all pre-existing tests, including the unmapped-root `404`, still pass.
- [x] 3.2 Add a `@Nested` unauthenticated group to `ItemControllerTests`: every item operation and the root, without credentials, return `401` `application/problem+json` with the Basic challenge and do not call the item service; verify the new tests pass and the service mock records no interactions.

## 4. Documentation

- [x] 4.1 Update the README: add the two variables to the configuration table and the `.env` example; add a security section covering HTTP Basic usage and its limits, HTTPS whenever credentials cross a network, IDE-agnostic setup (the `.env` import works from any tool run with the repository root as working directory — terminal, `spring-boot:run`, JAR, IntelliJ IDEA, VS Code — plus where each IDE sets environment variables), when to re-evaluate CSRF, how to enable CORS, and the derived-project review checklist; update "Current API" and the curl examples with `-u`; update the testing-strategy table and counts, and the manual persistence checklist to include credentials; note that CI still needs no secrets; verify each documented command matches the implemented behavior.
- [x] 4.2 Update `docs/adapting-the-template.md`: the `TEMPLATE:` review step and environment-variables step include the security values, and the "Before deployment" security entry changes from planned to implemented with the review points (mechanism, users, roles, CSRF, CORS, HTTPS); verify links and statements match the repository.
- [x] 4.3 Update `AGENTS.md` status and scope lines, `plans/06-security.md` (baseline implemented; the "Options to analyze after the plan" role experiments remain pending follow-up decisions), and `plans/README.md`; verify the stated status matches the implemented behavior.

## 5. Verification

- [x] 5.1 Run `./mvnw clean verify` from the repository root and report which checks executed and how many tests passed.
- [x] 5.2 Manually verify against local PostgreSQL, following the README manual checklist with credentials: starting without the `APP_SECURITY_*` values fails naming the missing property; with them present in the local `.env`, the application starts with no generated password in the log; an unauthenticated `curl` to an item endpoint returns `401` `application/problem+json` with the Basic challenge; an authenticated CRUD round trip (`POST`/`GET`/`PUT`/`DELETE`) succeeds; stop the application by its exact PID and confirm the port is free; record a dated entry in the README "Verification history".

## Workflow follow-up

- Run the `architecture-reviewer` agent per the apply guidance (new dependency, new package, configuration change) and address any findings before finishing.
- Archive the change after the project's review requirements are satisfied.
