# Proposal

## Why

Stage 3 of the template improvement plan asks for consistent HTTP conventions. A review of the current endpoints found these facts:

- Validation, malformed bodies, missing items (`404`), non-numeric IDs, and unsupported media types already return `application/problem+json`.
- Nothing handles an unexpected exception, so the response for a server failure is not defined by the template and may not be Problem Details.
- The conventions for success statuses are only implied by the code: `POST` returns `201` with `Location`, `GET` and `PUT` return `200`, and `DELETE` returns `200` with a body containing only the deleted `id`.

A derived project copies whatever the example does, so these rules should be explicit and tested.

## What Changes

- Add a handler in `ApiExceptionHandler` that maps any unexpected exception to `500` with `application/problem+json` and a fixed generic detail, logging the exception on the server without exposing it to the client.
- Document in the README the success-status and error conventions for all example endpoints, including known limitations.
- Add tests for the `500` response through `@WebMvcTest` with a mocked service that throws.
- No change to existing statuses or response bodies, so no compatibility impact.
- Out of scope, planned as separate changes: pagination and sorting of the list endpoint, and OpenAPI documentation. Both need user decisions (see design.md).

## Capabilities

### New Capabilities
- `api-error-handling`: The shape of error responses across the API, starting with unexpected failures.

### Modified Capabilities
<!-- None: existing specs describe their own 404 and 400 outcomes and remain valid. -->

## Impact

- Modified: `exception.ApiExceptionHandler`, `ItemControllerTests` (or a dedicated handler test), README.
- No new dependencies, endpoints, or schema changes.
