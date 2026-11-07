# Design

## Context

`ApiExceptionHandler` extends `ResponseEntityExceptionHandler`, which already produces Problem Details for Spring MVC exceptions, and adds handlers for validation, unreadable bodies, and `ResourceNotFoundException`. No handler covers other exceptions. See proposal.md.

## Goals / Non-Goals

**Goals:**
- One error format (Problem Details) for every error the API produces.
- Never leak internal details in a response.

**Non-Goals:**
- Pagination, sorting, and OpenAPI (separate changes).
- Authentication or authorization errors (security stage).
- A `409 Conflict` mapping: the example schema has no client-supplied unique value (`sku` is generated), so no conflict can occur yet. Document it when a feature needs it.

## Decisions

- **`@ExceptionHandler(Exception.class)`** returns `ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, "Unexpected error")` and logs with the class logger (`log.error`). Spring MVC exceptions keep their own handlers because more specific handlers win.
- **Fixed detail text,** like the `404` handler, so responses are predictable and carry no input.
- **Document statuses in the README** as a table per operation instead of a new spec per existing endpoint; the existing capability specs already cover each endpoint's outcomes.

## Open Questions (need user decision; not part of this change)

- `DELETE` returns `200` with `{id}` while `POST` returns `201`. Keep as implemented or move to `204`? Changing it is a compatibility decision.
- Pagination: add `page`, `size`, and `sort` parameters to `GET /api/v1/items`, and how to keep the current array response compatible.
- OpenAPI: adopt springdoc or not, and how to keep it synchronized.

## Risks / Trade-offs

- A catch-all handler can hide framework exceptions that should keep their status → mitigated because `ResponseEntityExceptionHandler` handles those first.
