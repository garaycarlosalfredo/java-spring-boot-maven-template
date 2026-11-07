# Proposal

## Why

Items can be created, listed, and retrieved by ID, but not modified. A client that needs to correct a name, description, or price has no way to do it. Updating is the next operation of the `items` example and the remaining endpoint of stage 2 of the improvement plan (`plans/02-endpoints.md`). It also exercises the architecture conventions on a write that touches an existing entity.

## What Changes

- Add `PUT /api/v1/items/{id}` that fully replaces `name`, `description`, and `price` of an existing item and responds `200` with the updated item as `ItemDto` JSON.
- The request body has the same fields and validation rules as item creation; `id` and `sku` are generated and cannot be changed.
- Respond `404` (`application/problem+json`, via the shared `ResourceNotFoundException`) when the item does not exist, `400` for an invalid body or a non-numeric ID, and `415` for an unsupported content type.
- Add `ItemService.update`, an update method on the `Item` entity that keeps its invariants, an operation-specific `UpdateItemRequest`, and tests with mocks.
- Existing endpoints and the database schema are unchanged; no migration is needed.

## Capabilities

### New Capabilities
- `item-update`: Updating an existing item over HTTP, including validation, the not-found outcome, and unsupported or malformed requests.

### Modified Capabilities
<!-- None: `item-retrieval` and the creation and listing behavior are unchanged. -->

## Impact

- New: `items.dto.UpdateItemRequest`; an update method on `items.entity.Item`; `ItemService.update`; a `PUT` method in `ItemController`; tests for each.
- Modified docs: README (API table, errors, item service), AGENTS.md status line, `plans/02-endpoints.md`.
- No new dependencies, routes other than the new one, or schema changes.
