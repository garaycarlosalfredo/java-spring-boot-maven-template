# Proposal

## Why

Stage 1 of the improvement plan (`plans/01-architecture.md`) asks that the chosen conventions be documented so derived projects can follow them. The decisions made so far (one controller per resource, a shared not-found exception) exist only in code and in archived changes, and the target structure for a template that is meant to grow has not been written down. README and AGENTS.md describe the layers but say nothing about how a feature package is organized, where shared exceptions live, who owns DTOs, or how entities and repositories are named.

## What Changes

- Add an "Architecture conventions" section to the root README (under "Project structure and architecture") covering:
  - feature packages with layer subpackages from the start: `items/controller`, `service`, `repository`, `entity`, `dto`;
  - naming without the `Jpa` suffix (`Item`, `ItemRepository`), with classes `public` because they live in different packages;
  - application-wide error handling and shared exceptions in an `exception/` package (`ApiExceptionHandler`, `ResourceNotFoundException`);
  - one controller per resource, with the base path on the class and one test class with `@Nested` groups per operation;
  - the shared `ResourceNotFoundException` and its mapping to `404`;
  - DTO ownership per feature, and transactions declared on service methods;
  - the two validation roles: Bean Validation on the request produces the `400` with field errors, and the entity constructor guards invariants.
- Update AGENTS.md with the matching concise rules, replacing the rule that application-wide error handling lives in the root package.
- Update `plans/01-architecture.md`: record `restructure-item-packages` as the next change, which aligns the example code with these conventions, and list moving `keepsRootUnmapped` as the only remaining pending item.
- State in the README that the example code is aligned with these conventions in that next change, and do not describe it as already aligned.
- No code, behavior, database, or test changes.

## Capabilities

### New Capabilities
<!-- None: documentation only. -->

### Modified Capabilities
<!-- None: no spec-level behavior changes. The change declares skip_specs. -->

## Impact

- Files: `README.md`, `AGENTS.md`, `plans/01-architecture.md`.
- Security, configuration, and service clients are out of scope; they are documented when they are implemented (stages 6 and 8).
- Every statement about existing behavior must be traceable to the current code; conventions that the code does not follow yet are labeled as the target.
- No effect on routes, status codes, persistence, or dependencies.
