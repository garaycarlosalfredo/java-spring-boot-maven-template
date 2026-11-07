# Design

## Context

`ItemController` has create, list, and get methods on `/api/v1/items`; `ItemService` coordinates them through `ItemRepository`; the `Item` entity validates its invariants in its constructor and exposes read-only accessors; `CreateItemRequest` carries Bean Validation rules and strips the name. Errors are mapped by `ApiExceptionHandler`, and a missing item is signaled with `ResourceNotFoundException`. See proposal.md for motivation.

## Goals / Non-Goals

**Goals:**
- Add a full-replacement update that follows the documented architecture conventions.
- Keep a single definition of the entity invariants for creation and update.

**Non-Goals:**
- Partial updates (`PATCH`), optimistic locking, or concurrency control.
- Changing `id` or `sku`, or any schema change.

## Decisions

- **`PUT` with full replacement,** responding `200` with the updated `ItemDto` (confirmed with the user). An omitted `description` clears it, which is what full replacement means. Alternatives: `204` without body (rejected: the client would need another call), `PATCH` (out of scope).
- **`UpdateItemRequest` record** in `items.dto`, with the same fields, constraints, and name stripping as `CreateItemRequest`. The conventions make request DTOs specific to an operation. Trade-off: the constraints are duplicated between the two records; sharing a single request type would couple the two operations. Revisit if a third operation needs the same fields.
- **`Item.update(name, description, price)`** changes the fields and enforces the same invariants as the constructor. The checks move into one private method used by both, so there is one definition of the rules. The entity stays free of setters.
- **`ItemService.update(id, name, description, price)`** runs in a read-write transaction: it loads the item (throwing `ResourceNotFoundException("Item", id)` when absent), calls `item.update`, and returns the saved entity via `saveAndFlush`, matching how `create` returns a flushed entity.
- **Controller method** `PUT /{id}` with `consumes` and `produces` set to JSON, `@Valid @RequestBody UpdateItemRequest`, and `@PathVariable Long id`; it maps the result with `ItemDtoMapper`. The `404`, `400`, and `415` responses come from the existing handler and Spring defaults, so `ApiExceptionHandler` needs no change.
- **Tests:** a `@Nested` group in `ItemControllerTests`, service tests with a mocked repository, an `Item.update` check, and `UpdateItemRequest` normalization, all without a database.

## Risks / Trade-offs

- Duplicated constraints between the two request records → accepted; documented in the code review and revisited if more operations appear.
- Persistence of the change cannot be proven by the mock-based tests → verify manually against local PostgreSQL (PUT then GET) after implementation.
- Concurrent updates overwrite each other (last write wins) → out of scope, noted as a known limitation.
