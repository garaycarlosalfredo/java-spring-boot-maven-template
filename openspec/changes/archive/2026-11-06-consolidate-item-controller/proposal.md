# Proposal

## Why

The `items` feature has one controller class per operation (`CreateItemController`, `ListItemsController`, `GetItemController`). Each repeats the same constructor, the same `ItemService` dependency, and the same `/api/v1/items` base path. Every new operation (update, delete) would add another file, another test class, and another copy of that boilerplate. The template is copied by derived projects, so the pattern it shows should scale. This is the second step of stage 1 of the improvement plan (`plans/01-architecture.md`, "Decide whether the example should have one controller per resource"), decided during exploration: one controller per resource.

## What Changes

- Replace `CreateItemController`, `ListItemsController`, and `GetItemController` with a single `ItemController` that declares the base path `/api/v1/items` once.
- Keep every route, method, `consumes`/`produces` setting, status code, header, and response body exactly as they are.
- Replace the three controller test classes with a single `ItemControllerTests` that keeps one `@Nested` group per operation and preserves every existing test case.
- No behavior change: **no HTTP contract, database, or dependency change**.

Out of scope (later in stage 1): documenting conventions, renaming `ItemJpa*`, package visibility, and the duplicated validation.

## Capabilities

### New Capabilities
<!-- None: no spec-level behavior changes. -->

### Modified Capabilities
<!-- None: the existing `item-retrieval` requirements are unchanged. The change declares skip_specs. -->

## Impact

- Code: three controllers in `items/` become one; three test classes become one.
- Unchanged: routes, status codes, `Location` header, error format, service, repository, entity, DTOs, `ApiExceptionHandler`, database schema.
- Docs: README and AGENTS.md do not name the old classes; their general wording ("item controllers", "the controllers") stays correct, so no update is needed. The conventions write-up belongs to the next change of the stage.
