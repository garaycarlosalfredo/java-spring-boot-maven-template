# Proposal

## Why

The architecture conventions documented in the README and AGENTS.md describe the target structure for a template that is meant to grow: layer subpackages inside each feature, entities and repositories without the `Jpa` suffix, and an `exception` package. The example code does not follow them yet: the ten `items` classes live in one package, the entity and repository are named `ItemJpaEntity` and `ItemJpaRepository`, and the shared error handling is in the root package. This change aligns the code with the documented conventions, which closes stage 1 of the improvement plan (`plans/01-architecture.md`).

## What Changes

- Move the main code into the documented structure:
  - `items.controller`: `ItemController`
  - `items.service`: `ItemService`
  - `items.repository`: `ItemRepository` (renamed from `ItemJpaRepository`)
  - `items.entity`: `Item` (renamed from `ItemJpaEntity`)
  - `items.dto`: `ItemDto`, `CreateItemRequest`, `ItemDtoMapper`
  - `exception`: `ApiExceptionHandler`, `ResourceNotFoundException`
- Move the tests to the matching packages and update them for the renamed classes.
- Move the `keepsRootUnmapped` test out of the create group to the outer class of `ItemControllerTests`.
- Update README, AGENTS.md, and `plans/01-architecture.md` so they describe the code as aligned, removing the wording that says the code will be aligned in this change.
- No behavior change: no route, status code, header, response body, database schema, or dependency changes.

## Capabilities

### New Capabilities
<!-- None: no spec-level behavior changes. -->

### Modified Capabilities
<!-- None: the existing `item-retrieval` requirements are unchanged. The change declares skip_specs. -->

## Impact

- Code: all classes under `src/main/java/com/example/template/` except the application class, and their tests.
- Docs: `README.md`, `AGENTS.md`, `plans/01-architecture.md`.
- Unchanged: routes, status codes, error format, table name (`items`), Flyway migrations, and dependencies.
