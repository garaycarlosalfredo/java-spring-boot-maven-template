# Proposal

## Why

Several classes in the `items` package import other classes from the same package (for example `import com.example.template.items.ItemService;` inside `com.example.template.items`). The imports are redundant, add noise, and the template's examples are copied by derived projects, so the pattern would spread. This is the first, mechanical step of stage 1 of the improvement plan (`plans/01-architecture.md`, "Review naming, imports, package visibility...").

## What Changes

- Remove the 15 same-package imports found in 7 files:
  - Main: `CreateItemController`, `ListItemsController`, `ItemService`, `ItemDtoMapper`.
  - Tests: `CreateItemControllerTests`, `ListItemsControllerTests`, `ItemServiceTests`.
- No other code changes: no renames, no reordering of the remaining imports, no behavior changes.

Out of scope for this change (deferred within stage 1): consolidating controllers, documenting conventions, renaming `ItemJpa*`, package visibility, and the duplicated validation.

## Capabilities

### New Capabilities
<!-- None: no spec-level behavior changes. -->

### Modified Capabilities
<!-- None: HTTP contracts and the database schema are unchanged. The change declares skip_specs. -->

## Impact

- Affects only import statements in the files listed above.
- No change to routes, status codes, response bodies, the database schema, or dependencies.
- README and AGENTS.md need no update.
