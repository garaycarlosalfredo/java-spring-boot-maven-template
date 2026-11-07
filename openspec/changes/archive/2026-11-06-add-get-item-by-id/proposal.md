# Proposal

## Why

Items can be created and listed, but a single item cannot be fetched by its identifier. The `Location` header returned by `POST /api/v1/items` already points to `/api/v1/items/{id}`, so that URL must resolve. Lookup by ID is the remaining pending item in the `items` feature before security work.

## What Changes

- Add `GET /api/v1/items/{id}` returning `200` with the existing `ItemDto` JSON.
- Return `404` as a `ProblemDetail` response when no item has the given ID.
- Return `400` as a `ProblemDetail` response when `{id}` is not a valid `Long` (Spring default handling for type mismatch).
- Add `ItemService.get(Long id)` (read-only transaction) that throws `ResourceNotFoundException` when the item is absent.
- Add a generic `ResourceNotFoundException` (resource name and ID) in the root package so every feature reuses one not-found exception and one 404 handler instead of defining one class per entity.
- Add a mocked-service `@WebMvcTest` and a mocked-repository service test; no database required.

## Capabilities

### New Capabilities
- `item-retrieval`: Retrieving a single item by ID over HTTP, including the not-found and invalid-ID outcomes.

### Modified Capabilities
<!-- None: openspec/specs/ has no existing capabilities. -->

## Impact

- New: `items/GetItemController`, root `ResourceNotFoundException`, tests for the controller.
- Modified: `ItemService` (new `get` method), root `ApiExceptionHandler` (maps `ResourceNotFoundException` to 404), `ItemServiceTests`.
- No schema change, no new dependency, no migration. Existing endpoints are unchanged.
- README and AGENTS.md currently list lookup by ID as pending; they need updating once verified.
