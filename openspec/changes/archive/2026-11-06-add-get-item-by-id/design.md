## Context

The `items` feature keeps one controller per operation (`CreateItemController`, `ListItemsController`), a shared `ItemService`, a Spring Data `ItemJpaRepository`, `ItemDto`, and the static `ItemDtoMapper`. `ApiExceptionHandler` in the root package extends `ResponseEntityExceptionHandler` and produces `ProblemDetail` responses. Tests use `@WebMvcTest` with a `@MockitoBean` service and Mockito-based service tests; no database.

## Goals / Non-Goals

**Goals:**
- Add lookup by ID following the existing one-controller-per-operation, layered pattern.
- Consistent, typed error responses (`ProblemDetail`) for 404 and 400.

**Non-Goals:**
- Security, caching, lookup by SKU, update/delete.
- New abstractions (interfaces, domain model) or schema changes.

## Decisions

- **New `GetItemController`** mapped to `/api/v1/items/{id}` with `produces = application/json`, matching the other controllers. It calls `service.get(id)` and maps with `ItemDtoMapper.toItemDto`.
- **`ItemService.get(Long id)`** runs `@Transactional(readOnly = true)` and returns `repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item", id))`. Not-found is a business outcome decided in the service, not the controller. Alternative considered: return `Optional` and let the controller build the 404; rejected because every future consumer of the service would need to repeat the decision.
- **Generic `ResourceNotFoundException`** is a plain `RuntimeException` in the root package, next to `ApiExceptionHandler`, with no HTTP dependency. It carries the resource name and the ID (`new ResourceNotFoundException("Item", id)`). Every feature reuses it, so adding a feature does not add an exception class or a handler method. Alternatives considered: one `<Entity>NotFoundException` per feature (rejected: the number of classes and handler imports grows with every feature, and the root package would depend on every feature); `ResponseStatusException` (rejected: puts HTTP concerns in the service). If a feature ever needs distinct handling, it can extend `ResourceNotFoundException` at that point.
- **404 mapping lives in the root `ApiExceptionHandler`** via a single `@ExceptionHandler(ResourceNotFoundException.class)` returning `ProblemDetail.forStatusAndDetail(NOT_FOUND, "<resource> not found")`. AGENTS.md assigns application-wide HTTP error handling to the root package. The root package no longer imports any feature class.
- **Detail message** is built only from the resource name ("Item not found"), never from user input, so responses stay stable and the ID is not echoed.
- **Malformed ID** needs no new code: `ResponseEntityExceptionHandler` already maps `MethodArgumentTypeMismatchException` to a 400 `ProblemDetail`. The test locks this behavior in.

## Risks / Trade-offs

- Persistence behavior (`findById` against PostgreSQL) is not covered by automated tests per project policy → verify manually against local PostgreSQL after implementation and record the result.
- A generic exception loses the per-entity type → acceptable; a feature can subclass it if it needs distinct behavior.
