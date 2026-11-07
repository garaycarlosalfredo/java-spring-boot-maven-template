# Design

## Context

README has a short "Project structure and architecture" paragraph and AGENTS.md has layering bullets that say the feature classes stay together in one package "while its size permits" and that application-wide error handling lives in the root package. The code today keeps the `items` classes in one package, uses the `Jpa` suffix on the entity and repository, and keeps shared error handling in the root package. The project context asks for a template designed as if it were already large, so the documented target is more structured than the code. See proposal.md for motivation.

## Goals / Non-Goals

**Goals:**
- Document the target conventions for a template that is expected to grow.
- Label clearly which parts the example code does not follow yet.
- Keep README and AGENTS.md consistent, with no duplicated long text.

**Non-Goals:**
- Moving or renaming code; that is the next change, `restructure-item-packages`.
- Security, configuration, and service-client structure (stages 6 and 8); they are documented when implemented.

## Decisions

- **Placement:** full explanation in README ("Architecture conventions" subsection); AGENTS.md gets one-line rules that point to it. Alternative: a new `docs/` file; rejected because README is already the reference named by AGENTS.md.
- **Feature package with layer subpackages:** `items/controller`, `service`, `repository`, `entity`, `dto`, from the start. Package-by-feature is the mainstream choice for applications that grow; layers inside each feature are a common hybrid, although some community guides suggest starting with a flat feature package and adding layers later. The template adopts the final structure now because it is meant to be copied and extended. These are community conventions, not official Spring guidance. A package is never created empty.
- **Naming and visibility:** `Item` in `entity`, `ItemRepository` in `repository`; no `Jpa` suffix, because there is no separate domain model and the package already identifies the layer. Classes are `public` because the layers are in different packages.
- **`exception/` package** for `ApiExceptionHandler` and `ResourceNotFoundException`, replacing the root-package rule in AGENTS.md. The root package keeps the application class.
- **One controller per resource** with the base path on the class and one `@Nested` group per operation in a single test class.
- **Not-found errors:** one shared `ResourceNotFoundException(resource, id)` thrown by services and mapped to `404` with the fixed detail "<resource> not found". No per-entity exception classes.
- **DTO ownership:** DTOs belong to the feature that exposes them; a feature does not import another feature's DTOs or entities and goes through that feature's service. An external service's response gets its own representation (details in stage 8).
- **Transactions:** declared on service methods (`@Transactional`, `readOnly = true` for reads), never on controllers.
- **Validation roles:** Bean Validation on the request gives the `400` with field errors; the entity constructor guards invariants and a failure there indicates a bug.
- **Timing:** the README labels these as target conventions and states that the example is aligned in `restructure-item-packages`.

## Risks / Trade-offs

- The documented structure is ahead of the code until the next change → the README says so explicitly, and the next change is recorded in the plan.
- Layer subpackages inside a feature are debated in the community → documented as a deliberate choice for a growing template, with the alternative noted.
- README and AGENTS.md drifting apart → AGENTS.md carries short rules that point to the README section.
