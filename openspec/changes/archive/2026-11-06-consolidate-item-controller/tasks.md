## 1. Controller

- [x] 1.1 Create `ItemController` with a class-level `@RequestMapping("/api/v1/items")` and the three existing operations (create, list, get by ID), preserving each method's `consumes`, `produces`, validation, and `Location` handling
- [x] 1.2 Delete `CreateItemController`, `ListItemsController`, and `GetItemController`

## 2. Tests

- [x] 2.1 Create `ItemControllerTests` (`@WebMvcTest(ItemController.class)`) with `@Nested` groups `CreateItem`, `ListItems`, and `GetItem`, moving all existing cases without changing assertions; keep the create stubbing inside the `CreateItem` group
- [x] 2.2 Delete `CreateItemControllerTests`, `ListItemsControllerTests`, and `GetItemControllerTests`; drop the `@Import(ApiExceptionHandler.class)` if the tests pass without it

## 3. Verification

- [x] 3.1 Run `./mvnw verify` and confirm the same 39 tests pass (25 of them controller tests); report which checks executed
- [x] 3.2 Confirm no reference to the removed controller classes remains (`grep -rn "CreateItemController\|ListItemsController\|GetItemController" src README.md AGENTS.md`)
- [x] 3.3 Manually smoke-test the running application against local PostgreSQL: `POST /api/v1/items`, `GET /api/v1/items`, `GET /api/v1/items/{id}`, a missing ID (`404`), and a non-numeric ID (`400`); then stop the application by its exact PID and confirm the port is free
