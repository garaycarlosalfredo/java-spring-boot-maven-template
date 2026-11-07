## 1. Main code

- [x] 1.1 Create the `exception` package: move `ApiExceptionHandler` and `ResourceNotFoundException` there and fix their imports
- [x] 1.2 Rename and move `ItemJpaEntity` to `items.entity.Item` and `ItemJpaRepository` to `items.repository.ItemRepository`, keeping `@Table(name = "items")`
- [x] 1.3 Move `ItemDto`, `CreateItemRequest`, and `ItemDtoMapper` to `items.dto`, `ItemService` to `items.service`, and `ItemController` to `items.controller`; update package declarations, references to the renamed classes, and the now-required cross-package imports

## 2. Tests

- [x] 2.1 Move `ItemControllerTests` to `items.controller`, `ItemServiceTests` to `items.service`, and `CreateItemRequestTests` to `items.dto`; update them for the renamed and moved classes without changing any assertion
- [x] 2.2 Move `keepsRootUnmapped` from the `CreateItem` group to the outer class of `ItemControllerTests`

## 3. Documentation

- [x] 3.1 Update the README: describe the structure as implemented (remove the wording that says the example is aligned in a later change), and fix mentions of moved or renamed classes
- [x] 3.2 Update AGENTS.md: remove the sentence that tells new code to follow the README target until the restructure, keeping the rules
- [x] 3.3 Update `plans/01-architecture.md`: mark stage 1 as done and note which items of its work list are resolved

## 4. Verification

- [x] 4.1 Confirm no reference to the old names or packages remains (`grep -rn "ItemJpa" src README.md AGENTS.md`, and no class left in the old locations)
- [x] 4.2 Run `./mvnw clean verify` and confirm the same 39 tests pass; report which checks executed
- [x] 4.3 Smoke-test the running application against local PostgreSQL: `POST /api/v1/items`, `GET /api/v1/items`, `GET /api/v1/items/{id}`, a missing ID (`404`), and a non-numeric ID (`400`); then stop the application by its exact PID and confirm the port is free
- [x] 4.4 Run the `architecture-reviewer` agent on the result and address violations
