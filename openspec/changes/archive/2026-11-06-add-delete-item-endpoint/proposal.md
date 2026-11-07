# Proposal

## Why

Items can be created, listed, and retrieved by ID (and updated in the separate `add-update-item-endpoint` change), but not removed. Deleting is the last endpoint of stage 2 of the improvement plan (`plans/02-endpoints.md`) and completes the CRUD example that derived projects will copy.

## What Changes

- Add `DELETE /api/v1/items/{id}` that removes the item and responds `200` with a JSON confirmation body containing only the deleted `id`.
- Respond `404` (`application/problem+json`, via the shared `ResourceNotFoundException`) when the item does not exist, so a repeated delete is reported rather than silently accepted.
- Respond `400` when the `{id}` path segment is not a valid 64-bit integer.
- Add `ItemService.delete` (existence check then removal, returning nothing), the `DeleteItemResponse` confirmation DTO, and tests with mocks.
- The deletion is physical (the row is removed). Existing endpoints and the schema are unchanged; no migration is needed.
- This change is independent of `add-update-item-endpoint`; both add a method to `ItemController` and a `@Nested` group to `ItemControllerTests`, and can be applied in either order.

## Capabilities

### New Capabilities
- `item-deletion`: Deleting an item by ID over HTTP, including the not-found and malformed-ID outcomes.

### Modified Capabilities
<!-- None. -->

## Impact

- New: `items.dto.DeleteItemResponse`, `ItemService.delete`, a `DELETE` method in `ItemController`, and tests for each.
- Modified docs: README (API table, errors, item service), AGENTS.md status line, `plans/02-endpoints.md`.
- No new dependencies or schema changes.
