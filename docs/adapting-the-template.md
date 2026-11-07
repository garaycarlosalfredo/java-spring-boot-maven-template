# Adapting the Template

A checklist to create a project derived from this repository, from clone to first deployable service. Follow the steps in order: the package rename happens right after the Maven coordinates and before touching configuration. Labels in "Before deployment" reflect this repository at the time of writing (2026-11-07).

## Adaptation checklist

1. **Maven coordinates.** In `pom.xml` change `<groupId>com.example</groupId>` to your domain and `<artifactId>spring-boot-template</artifactId>` to your project name, and fill the empty `<name>`, `<description>`, `<url>`, and `<developers>` entries. Keep `<version>`. The Spring Boot parent manages dependency versions; do not pin versions it controls. The packaged JAR is named `target/<artifactId>-<version>.jar`, so update the README startup commands with the new name.
2. **Application name and main class.** Set `spring.application.name` in `src/main/resources/application.properties` (it appears in logs and future observability). Rename `SpringBootTemplateApplication` — file, class, package line, and the `SpringApplication.run(...)` reference — to `<YourProject>Application`.
3. **Java package rename.** Move both `src/main/java/com/example/template` and `src/test/java/com/example/template` to your base package directories, update every `package` declaration and `import` in all classes and tests, and confirm with `./mvnw clean verify`.
4. **Configuration and `TEMPLATE:` comments.** Review every `# TEMPLATE:` comment in `application.properties` and `.env.example`: the optional `.env` config import, the datasource values, and the security credential values. Review the `TEMPLATE:` notes in `SecurityConfig`: authentication mechanism, authorization rules and roles, CSRF, CORS, security headers, secret handling, and operational endpoints. Keep `spring.jpa.hibernate.ddl-auto=none` — Flyway owns the schema.
5. **Environment variables.** Keep `DB_URL`, `DB_USERNAME`, and the required `DB_PASSWORD`, plus the required `APP_SECURITY_USERNAME` and `APP_SECURITY_PASSWORD`, supplied by environment or by a local ignored `.env`; never commit credentials or real values. The application fails to start when any required value is missing or blank. When you add or rename a variable, update `.env.example` with generic placeholders.
6. **Database and schema ownership.** Point the application at a fresh database or schema for your project; Flyway records applied versions in `flyway_schema_history`. Never rewrite an applied migration: change the schema or drop the example table by adding a new version (`V2__...sql`). If another service shares the database, decide the single owner that runs migrations.
7. **The example `items` feature.** Choose one:
   - **Keep** it as a working reference of the architecture conventions.
   - **Rename** it into your first real resource: copy the feature package, adapt entity, DTOs, tests, and the migration, then remove the example.
   - **Remove** it: delete the feature packages and tests, and delete `V1__create_items.sql` only if no database has ever applied it; against a preserved database, add a new migration that drops the table instead.
8. **Repository documentation and metadata.** Update the README title, badges, quick start, current API, and package tree, and the AGENTS.md status lines, to match your project. The dry run on a throwaway copy showed one more decision the checklist had missed: the template's own planning material — the `openspec/` folder and this guide — should be deleted or reset in the derived repository, since it describes this template's history, not your project's. Replace the MIT notice or choose your own license only if that is your intent (see License).

## Before deployment (open choices)

These are decisions this template deliberately leaves to each derived project. Implemented means usable today in this repository; planned means documented but not built.

- **Security — implemented as an adaptable baseline.** Every endpoint requires HTTP Basic authentication against one in-memory user built from the required `APP_SECURITY_USERNAME`/`APP_SECURITY_PASSWORD`, except the Actuator health paths; missing or blank credential values fail startup. The API is stateless, CSRF is disabled, CORS is not enabled, and rejections return Problem Details (`401`/`403`). Before deployment, review the `TEMPLATE:` decisions in `SecurityConfig` and the README "Security" section: replace the mechanism and the shared user, add roles and authorization rules, re-enable CSRF for cookie/browser authentication, enable CORS for browser frontends, and require HTTPS whenever credentials cross a network. Recipes for those changes are in [security-recipes.md](security-recipes.md) (guidance, unverified); role and authorization options remain follow-up decisions.
- **Health and operations — implemented and manually verified on 2026-11-07.** Actuator exposes only health, with credential-free liveness and database-backed readiness probes. Health responses show only the overall status, and every other path remains authenticated. Before deployment, review endpoint exposure, detail policy, probe groups, and the management port or network policy in the README "Operations" section and the `TEMPLATE:` comments in `application.properties` and `SecurityConfig`.
- **Service integrations — guidance documented, unverified.** Conventions for a first external client (placement, configuration, error translation, mapping, tests, resilience) are in the README section "External service clients"; review them against the concrete case when you add the first real integration.
- **Continuous integration — implemented.** `.github/workflows/ci.yml` runs `./mvnw --batch-mode verify` on pushes to `development` and on every pull request (deliberately not on `main`; adjust the triggers to your own branching model), with Temurin JDK 25 and no secrets. The first real run is validated with the first test deployment.
- **Logging — implemented (defaults).** Console logging at `INFO` from Spring Boot defaults. Adjust levels per environment and make sure no credential or request value is logged.

## Running the application and tests

- **Build and automated tests:** `./mvnw clean verify` (JDK 25 in your terminal; no Maven installation needed). Tests use `@WebMvcTest` and mocks with inline dummy credentials: they need no PostgreSQL, no Docker, no network, and no real secrets.
- **Run locally:** start the external PostgreSQL, copy `.env.example` to `.env` with your local values, then from the repository root either:

  ```bash
  ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.address=127.0.0.1
  # or, after ./mvnw verify:
  java -jar target/<artifactId>-<version>.jar --server.address=127.0.0.1
  ```

- **Checks that need external services and are not automated:** Flyway migration behavior against a real PostgreSQL, JPA mappings and constraints, persistence through HTTP end-to-end, and durability across restarts. They are performed manually against the local database and recorded as dated notes in the README "Verification history". Derived projects may add automated database integration tests according to their needs.

## License

This template is distributed under the MIT License — see [LICENSE](../LICENSE) and the README "License" section. Derived projects keep the required copyright notice or choose their own license.
