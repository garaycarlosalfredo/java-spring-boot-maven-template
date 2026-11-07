---
name: verify
description: Run the Maven verification for this project and report which checks were actually executed. Use after changing Java code, tests, pom.xml, or migrations, or when asked to verify, build, or test.
---

# Verify the project

Run from the repository root with the Maven Wrapper.

1. Run `./mvnw verify`. Use `./mvnw clean verify` only if a clean build is needed.
2. Report the outcome faithfully:
   - Which command ran, and whether it passed or failed.
   - On failure, quote the failing test or compiler error. Do not claim success.
3. Distinguish executed checks from expected results. Never say something works because it should.
4. Remember the limits of the automated tests: they use `@WebMvcTest` with mocked services. They do not prove application startup, database connectivity, or Flyway migrations.
5. If the change touched persistence or migrations, say that manual verification against local PostgreSQL is still pending. Do not mark the feature complete until it is done.

Do not open, print, or copy the local `.env` file or any credentials.
