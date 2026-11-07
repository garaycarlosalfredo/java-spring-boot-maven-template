## 1. Service

- [x] 1.1 Add `ItemService.delete(id)` (read-write transaction, `ResourceNotFoundException` when absent, then `deleteById`)
- [x] 1.2 Add service tests with a mocked repository: an existing ID is deleted, a missing ID throws `ResourceNotFoundException` and deletes nothing

## 2. HTTP

- [x] 2.1 Add `DELETE /{id}` to `ItemController` returning `204` with no body
- [x] 2.2 Add a `DeleteItem` `@Nested` group to `ItemControllerTests` (mocked service): `204` with an empty body, `404` problem response, `400` for a non-numeric ID with no service call

## 3. Documentation

- [x] 3.1 Update the README: API table, errors section (delete `404`), item service section, and the verification note
- [x] 3.2 Update the AGENTS.md status line and `plans/02-endpoints.md` (stage 2 endpoints implemented)

## 4. Verification

- [x] 4.1 Run `./mvnw clean verify` and report which checks executed
- [x] 4.2 Manually verify against local PostgreSQL: DELETE returns `204`, a later GET returns `404`, a repeated DELETE returns `404`, a non-numeric ID returns `400`; then stop the application by its exact PID and confirm the port is free
- [x] 4.3 Run the `architecture-reviewer` agent and address violations

## 5. Revision: confirmation response (revised spec 2026-11-06)

The `add-delete-item-endpoint` planning was revised after implementation: `DELETE` returns `200` with the removed item as `ItemDto` (confirming the deleted `id`) instead of `204` with no body. These tasks carry the delta into code, tests, docs, and verification. Superseded by section 6 (id-only confirmation).

- [x] 5.1 Change `ItemService.delete(id)` to return the removed entity: load with `findById`, throw `ResourceNotFoundException` when absent, remove it, and return it
- [x] 5.2 Update service tests: an existing ID is removed and the removed entity is returned; a missing ID throws `ResourceNotFoundException` and deletes nothing
- [x] 5.3 Change the `DELETE /{id}` controller handler to return `200` with the removed item as `ItemDto` (remove `@ResponseStatus`/`void` and unused imports)
- [x] 5.4 Update the `DeleteItem` `@Nested` group: `200` with the removed item body, `404` problem response, `400` for a non-numeric ID with no service call
- [x] 5.5 Documentation: README (API table, controller paragraph, errors, item service section, and the DELETE verification note), AGENTS.md status line, and `plans/02-endpoints.md` reflect the confirmation-response contract
- [x] 5.6 Run `./mvnw clean verify` and report which checks executed
- [x] 5.7 Manually verify against local PostgreSQL: DELETE returns `200` with the removed item, a later GET returns `404`, a repeated DELETE returns `404`, a non-numeric ID returns `400`; then stop the application by its exact PID and confirm the port is free
- [x] 5.8 Run the architecture review and address violations

## 6. Revision: id-only confirmation (2026-11-06)

After reviewing section 5, returning the full removed item read as confusing for a resource that no longer exists: `DELETE` now responds `200` with `DeleteItemResponse`, a body containing only the deleted `id`, and the service goes back to `existsById` + `deleteById` without loading the entity.

- [x] 6.1 Add `DeleteItemResponse(Long id)` in `items.dto` as the delete confirmation body
- [x] 6.2 Change `ItemService.delete(id)` to return nothing: read-write transaction, `ResourceNotFoundException` when `existsById` is false, then `deleteById`
- [x] 6.3 Update service tests: an existing ID deletes (`verify existsById` + `deleteById`), a missing ID throws `ResourceNotFoundException` and `never deleteById`
- [x] 6.4 Change the `DELETE /{id}` controller handler to return `200` with `DeleteItemResponse` for the validated path `id` and declare `produces` JSON
- [x] 6.5 Update the `DeleteItem` `@Nested` group: `200` with a body containing only the deleted `id`, `404` problem response, `400` for a non-numeric ID with no service call
- [x] 6.6 Documentation: README (API table, controller paragraph, item service section, tests sentence, and the DELETE verification note), AGENTS.md status line, and `plans/02-endpoints.md` reflect the id-only confirmation
- [x] 6.7 Run `./mvnw clean verify` and report which checks executed
- [x] 6.8 Manually verify against local PostgreSQL: DELETE returns `200` with the deleted id, a later GET returns `404`, a repeated DELETE returns `404`, a non-numeric ID returns `400`; then stop the application by its exact PID and confirm the port is free
- [x] 6.9 Run the architecture review and address violations
