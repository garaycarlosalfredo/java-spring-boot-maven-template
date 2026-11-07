# Design

## Context

Each operation of the `items` feature is a separate `@RestController` with its own `ItemService` constructor injection and a full path in each mapping annotation. Tests mirror that: one `@WebMvcTest` class per controller (25 controller test cases in total). `ApiExceptionHandler` is a `@RestControllerAdvice` in the root package. See proposal.md for the motivation.

## Goals / Non-Goals

**Goals:**
- One controller class per resource, with the base path declared once.
- Identical external behavior and identical test coverage.

**Non-Goals:**
- New endpoints, new error handling, or renames outside the controller and its tests.
- Splitting the controller by sub-resource; there is only one resource today.

## Decisions

- **Class name `ItemController`**, named after the resource, in the `items` package. Alternative: keep an operation-style suffix; rejected because the class no longer represents one operation.
- **Base path on the class** with `@RequestMapping("/api/v1/items")`; methods declare only what differs (`@PostMapping`, `@GetMapping`, `@GetMapping("/{id}")`). Each method keeps its existing `consumes` and `produces` so content negotiation (including `415`) does not change.
- **Method bodies move unchanged.** The `Location` header keeps using `ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")`, which resolves to the same URL. Mapping to `ItemDto` stays in `ItemDtoMapper`.
- **One `ItemControllerTests` with `@Nested` classes** (`CreateItem`, `ListItems`, `GetItem`, named after the operation so `List` does not shadow `java.util.List`), annotated with `@WebMvcTest(ItemController.class)`; `MockMvc` and the `@MockitoBean` `ItemService` live in the outer class, and the stubbing currently in `CreateItemControllerTests`'s `@BeforeEach` moves into the `CreateItem` group so it does not leak into the others. Every existing test case is preserved by name and assertion. Alternative: three test classes all targeting `ItemController`; viable fallback if `@Nested` causes problems with the Spring test context, at the cost of test classes named after controllers that no longer exist.
- **No `@Import(ApiExceptionHandler.class)` is assumed necessary.** `@WebMvcTest` already registers `@ControllerAdvice` beans, and the existing create tests assert the advice's error format without importing it. The `@Import` in `GetItemControllerTests` was added defensively and has been dropped; the tests pass without it.

## Risks / Trade-offs

- A larger controller class → acceptable now (about 40 lines); revisit if it grows large, for example by splitting by sub-resource.
- Mapping ambiguity or a changed route could slip through in the move → the preserved MVC tests cover each route, method, and error response, and a manual smoke run of the real application covers startup wiring, which the `@WebMvcTest` slice does not.
- `@Nested` with Spring test annotations → if it misbehaves, fall back to separate test classes (see Decisions).
