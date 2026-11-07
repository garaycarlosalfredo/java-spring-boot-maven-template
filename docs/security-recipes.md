# Security recipes

The template ships one security baseline: HTTP Basic with a single in-memory user (see the README "Security" section and `SecurityConfig`). A derived project is expected to replace or extend it. These recipes show where to make each common change.

**Status: guidance, unverified.** None of these recipes is implemented or tested in this repository. Each one names the template parts it touches; confirm dependency names, properties, and APIs against the Spring Boot and Spring Security versions in your `pom.xml` before relying on it, and add tests for the behavior you adopt.

All recipes keep the template's rules: Problem Details for every rejection (`ProblemDetailSecurityHandler`), no credentials or secrets in code or committed files, and tests that need no database or real secrets.

## 1. Roles and authorization rules (makes `403` reachable)

The baseline requires only authentication, so the `403` handler is configured but no request can trigger it.

1. Give users authorities. In `SecurityConfig.userDetailsService`, replace `.roles()` with the roles each user needs (for example `.roles("USER")`, and a second user with `.roles("ADMIN")` whose credentials also come from required configuration).
2. Add rules before `anyRequest().authenticated()` in the `authorizeHttpRequests` block, for example: `GET` and `POST` on `/api/v1/items/**` for any authenticated user, `PUT` and `DELETE` only for `hasRole("ADMIN")`. Keep `anyRequest().authenticated()` last so new paths stay closed by default.
3. For per-method rules close to the service, add `@EnableMethodSecurity` and `@PreAuthorize` on service methods.
4. Test each rule with `@WithMockUser(roles = ...)`: expect `200`/`201` for allowed roles and `403` `application/problem+json` for the rest, and assert the service mock is not called on `403`.

Per-owner rules (only the creator may modify an item) need a persistence change (an owner column through a new Flyway version) and service logic; keep that logic in the service, not in the controller.

## 2. JWT resource server (tokens issued elsewhere)

Use this when an identity provider issues signed JWTs and the API only validates them.

1. Add the Spring Boot OAuth2 resource server starter (the artifact name changed between Boot generations; check yours).
2. Configure the issuer or JWK set through the standard `spring.security.oauth2.resourceserver.jwt.*` properties, supplied by environment like the other required values, with a `TEMPLATE:` comment and a matching `.env.example` placeholder.
3. In `SecurityConfig`, replace `httpBasic(...)` with `oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))`, and wire the same `ProblemDetailSecurityHandler` as `authenticationEntryPoint` and `accessDeniedHandler` for the resource server so `401`/`403` keep the single error format.
4. Remove the in-memory `UserDetailsService`, the `PasswordEncoder`, `APP_SECURITY_USERNAME`, `APP_SECURITY_PASSWORD`, and their tests, README rows, and `.env.example` lines.
5. Keep the chain stateless; CSRF can stay disabled because tokens are sent explicitly per request.
6. In `@WebMvcTest` tests, authenticate with the security test support for JWTs instead of a real token; do not call the identity provider from tests.

## 3. Per-user credentials in a database

Replace the in-memory user with a `UserDetailsService` backed by a users table: a Flyway migration for the table, a repository, and a service in a `users` feature that the `security` package uses only through its service. Store only password hashes (`BCryptPasswordEncoder` is already a bean). Do not seed default users in a migration. This imposes a mechanism, which is why the template does not include it.

## 4. Browser clients: cookies, sessions, and CSRF

The baseline disables CSRF because clients send credentials explicitly on every request. **Re-enable CSRF as soon as authentication relies on cookies or sessions, or a browser sends credentials automatically.**

1. Remove `csrf.disable()` and choose a token repository suited to the frontend (for a single-page application, a cookie-based token readable by the script).
2. Change `sessionCreationPolicy` from `STATELESS` only if the chosen mechanism needs a session, and set secure cookie attributes for the deployment.
3. Add tests proving that state-changing requests without a valid token are rejected with the Problem Details format.

## 5. CORS for a frontend on another origin

CORS is not enabled. When a browser frontend from a different origin must call the API:

1. Declare a `CorsConfigurationSource` bean listing the exact allowed origins, methods, and headers; read the origins from configuration, never `*` together with credentials.
2. Enable it in the chain with `http.cors(Customizer.withDefaults())`.
3. Test that a preflight request from an allowed origin succeeds and one from another origin does not.

## 6. Operational endpoints

When Actuator is added, give its endpoints explicit rules before `anyRequest().authenticated()`, for example allowing only the health probes without credentials and keeping every other management path authenticated. The `TEMPLATE:` note in `SecurityConfig` marks this decision.
