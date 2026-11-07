## Purpose

Defines how an item is deleted by its ID over HTTP, including the responses for an item that does not exist and for a malformed ID.

## ADDED Requirements

### Requirement: Delete an item by ID
The system SHALL expose `DELETE /api/v1/items/{id}` that removes the item with that ID and responds with `200` and a JSON confirmation body that contains only the deleted `id`.

#### Scenario: Item exists
- **WHEN** a client sends `DELETE /api/v1/items/1` and item 1 exists
- **THEN** the response status is `200`
- **AND** the content type is `application/json`
- **AND** the body is a JSON object containing only the deleted item's `id`
- **AND** a later `GET /api/v1/items/1` returns `404`

### Requirement: Report a missing item on delete
The system SHALL respond with `404` and a `ProblemDetail` JSON body when no item exists for the requested ID.

#### Scenario: Item does not exist
- **WHEN** a client sends `DELETE /api/v1/items/999` and no item 999 exists
- **THEN** the response status is `404`
- **AND** the content type is `application/problem+json`

### Requirement: Reject a malformed ID on delete
The system SHALL respond with `400` and a `ProblemDetail` JSON body when the `{id}` path segment is not a valid 64-bit integer, without invoking the item service.

#### Scenario: Non-numeric ID
- **WHEN** a client sends `DELETE /api/v1/items/abc`
- **THEN** the response status is `400`
- **AND** the item service is not called
