## 1. Error handling

- [x] 1.1 Add the `Exception` handler to `ApiExceptionHandler` returning `500` Problem Details with a fixed detail and logging the exception
- [x] 1.2 Add `@WebMvcTest` tests: a service throwing a runtime exception yields `500`, `application/problem+json`, and a body without the exception message; existing `400`, `404`, and `415` tests still pass

## 2. Documentation

- [x] 2.1 Update the README errors section with a status table per operation and the `500` behavior, and note the `409` and pagination limitations

## 3. Verification

- [x] 3.1 Run `./mvnw clean verify` and report which checks executed
- [x] 3.2 Run the `architecture-reviewer` agent and address violations
