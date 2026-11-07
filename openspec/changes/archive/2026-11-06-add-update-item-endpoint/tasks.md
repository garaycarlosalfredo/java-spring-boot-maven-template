## 1. Domain and service

- [x] 1.1 Add `Item.update(name, description, price)` and move the invariant checks into one private method shared with the constructor, without adding setters
- [x] 1.2 Add `ItemService.update(id, name, description, price)` (read-write transaction, `ResourceNotFoundException` when absent, `saveAndFlush`)
- [x] 1.3 Add tests with a mocked repository: update replaces the fields and returns the saved entity, a missing ID throws `ResourceNotFoundException` and saves nothing, invalid values are rejected; add an `Item.update` check

## 2. HTTP

- [x] 2.1 Add `UpdateItemRequest` in `items.dto` with the creation constraints and name stripping, and a test for the normalization
- [x] 2.2 Add `PUT /{id}` to `ItemController` (`consumes` and `produces` JSON, `@Valid @RequestBody`, returns `200` with `ItemDto`)
- [x] 2.3 Add an `UpdateItem` `@Nested` group to `ItemControllerTests` (mocked service): `200` with the body, `404` problem response, `400` for invalid fields with no service call, `400` for malformed or missing body, `400` for a non-numeric ID, `415` for `text/plain`

## 3. Documentation

- [x] 3.1 Update the README: API table, errors section (update `404`), item service section, and the verification note
- [x] 3.2 Update the AGENTS.md status line (update is implemented) and `plans/02-endpoints.md` (second endpoint decided and implemented)

## 4. Verification

- [x] 4.1 Run `./mvnw clean verify` and report which checks executed
- [x] 4.2 Manually verify against local PostgreSQL: PUT then GET shows the new values with the same `id` and `sku`, omitting `description` clears it, a missing ID returns `404`, invalid bodies return `400`, `text/plain` returns `415`; then stop the application by its exact PID and confirm the port is free
- [x] 4.3 Run the `architecture-reviewer` agent and address violations
