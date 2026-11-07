# Design

## Context

`ItemController` exposes create, list, and get on `/api/v1/items`; `ItemService` coordinates them through `ItemRepository`; a missing item is signaled with the shared `ResourceNotFoundException` and mapped to `404` by `ApiExceptionHandler`. See proposal.md for motivation.

## Goals / Non-Goals

**Goals:**
- Add deletion following the documented architecture conventions, reusing the existing not-found handling.

**Non-Goals:**
- Soft deletion, bulk deletion, or cascading rules (the schema has no relations).
- Idempotent `DELETE` semantics (a repeated delete responding `200` as if it had just removed the item).

## Decisions

- **`200` with a minimal confirmation body `{"id": <deleted>}` on success; `404` when absent.** The confirmation names the deleted ID without echoing the state of a resource that no longer exists. A dedicated `DeleteItemResponse` record in `items.dto` carries the confirmation because its shape is not the item representation shared by the other operations. Alternatives: `204` with no body (rejected: no confirmation of the deleted ID), `200` with the removed item as `ItemDto` (rejected after review: returning the full item for a resource that is gone reads as confusing; only the ID is needed), always `204` even when absent (rejected: keeps one not-found behavior across operations).
- **Physical delete.** The row is removed with the repository. No soft-delete column exists and none is needed by the example.
- **`ItemService.delete(Long id)`** is `@Transactional` and returns nothing: it checks `existsById` and throws `ResourceNotFoundException("Item", id)` when absent, then calls `deleteById`. No entity is loaded because the confirmation echoes the validated path `id` (the primary key), which is exactly what the deleted row had.
- **Controller method** `@DeleteMapping("/{id}")` with `produces` set to JSON, returning `ResponseEntity<DeleteItemResponse>` with `200` and the deleted `id`. `ApiExceptionHandler` needs no change; a malformed ID is already handled as `400`.
- **Tests:** a `DeleteItem` `@Nested` group in `ItemControllerTests` with a mocked service, and service tests with a mocked repository. No database.

## Risks / Trade-offs

- Mock-based tests cannot prove the row is removed → verify manually against local PostgreSQL (DELETE returns `200` with the id, then GET returns `404`).
- A concurrent delete between `existsById` and `deleteById` is harmless: `deleteById` on a missing row is a no-op in Spring Data, so the worst case is a `200` confirmation for a race, which is acceptable.
