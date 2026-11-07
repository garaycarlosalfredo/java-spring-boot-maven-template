# Design

## Context

All `items` classes share one package, the entity and repository carry a `Jpa` suffix, and `ApiExceptionHandler` and `ResourceNotFoundException` sit in the root package. The documented target is in the README ("Architecture conventions"). All classes are already `public`, so moving them across packages needs only import changes. See proposal.md for motivation.

## Goals / Non-Goals

**Goals:**
- The code matches the documented structure and naming exactly.
- Identical external behavior and identical test coverage (39 tests).

**Non-Goals:**
- New behavior, new endpoints, or changes to validation rules.
- Splitting the single controller or service; there is one resource.

## Decisions

- **Rename and move together, per class.** `ItemJpaEntity` becomes `Item` in `items.entity`; `ItemJpaRepository` becomes `ItemRepository` in `items.repository`. The entity keeps `@Table(name = "items")`, so the table mapping and Flyway migrations are untouched.
- **Move with plain file moves, not `git mv`,** so the working tree changes without staging anything; Git still detects the renames when the user commits.
- **Imports become explicit across packages.** The previously redundant same-package imports were removed in `clean-item-imports`; after the move, imports between the layer packages are required, not redundant.
- **Component and entity scanning needs no configuration.** `SpringBootTemplateApplication` stays in the root package, and `@SpringBootApplication` scans its subpackages, including entity and repository packages.
- **Tests mirror the main packages:** `ItemControllerTests` in `items.controller`, `ItemServiceTests` in `items.service`, `CreateItemRequestTests` in `items.dto`.
- **`keepsRootUnmapped` goes to the outer class** of `ItemControllerTests`, because it checks the application's routing, not the create operation.
- **Documentation is updated in the same change** so README and code are never out of sync after it.

## Risks / Trade-offs

- Mapping, entity scanning, or repository wiring could break at startup in a way that `@WebMvcTest` slices do not show → run a smoke test of the real application against local PostgreSQL after the move.
- A large mechanical move can hide an accidental edit → compare the test count (39) and review the diff with the `architecture-reviewer`, which applies to structural changes.
- Mocking an entity from another package → the entity and its accessors are `public`, so the existing mock-based tests keep working.
