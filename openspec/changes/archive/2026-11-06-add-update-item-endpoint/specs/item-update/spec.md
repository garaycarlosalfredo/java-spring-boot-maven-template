## Purpose

Defines how an existing item is updated over HTTP: which fields can be replaced, the validation that applies, and the responses for an item that does not exist or a request that is invalid.

## ADDED Requirements

### Requirement: Update an item by ID
The system SHALL expose `PUT /api/v1/items/{id}` that replaces the `name`, `description`, and `price` of the item with that ID using the same fields and validation rules as item creation, keeps its `id` and `sku`, and returns the updated item as JSON.

#### Scenario: Item exists and the body is valid
- **WHEN** a client sends `PUT /api/v1/items/1` with a valid JSON body and item 1 exists
- **THEN** the response status is `200`
- **AND** the content type is `application/json`
- **AND** the body contains the new `name`, `description`, and `price` and the unchanged `id` and `sku`
- **AND** a later `GET /api/v1/items/1` returns the same values

#### Scenario: Description is omitted
- **WHEN** a client sends a valid `PUT` body without `description`
- **THEN** the stored description is cleared, because the request fully replaces the item

### Requirement: Reject an invalid update body
The system SHALL respond with `400` and a `ProblemDetail` JSON body that lists the invalid fields when the body breaks a creation validation rule or is malformed, and SHALL NOT modify the item.

#### Scenario: Invalid field values
- **WHEN** a client sends `PUT /api/v1/items/1` with a blank `name` or a negative `price`
- **THEN** the response status is `400`
- **AND** the content type is `application/problem+json`
- **AND** the item service is not called

#### Scenario: Malformed or missing body
- **WHEN** a client sends malformed JSON or no body
- **THEN** the response status is `400` with `application/problem+json`

### Requirement: Report a missing item on update
The system SHALL respond with `404` and a `ProblemDetail` JSON body when no item exists for the requested ID.

#### Scenario: Item does not exist
- **WHEN** a client sends a valid `PUT /api/v1/items/999` and no item 999 exists
- **THEN** the response status is `404`
- **AND** the content type is `application/problem+json`

### Requirement: Reject a malformed ID on update
The system SHALL respond with `400` and a `ProblemDetail` JSON body when the `{id}` path segment is not a valid 64-bit integer, without invoking the item service.

#### Scenario: Non-numeric ID
- **WHEN** a client sends `PUT /api/v1/items/abc` with a valid body
- **THEN** the response status is `400`
- **AND** the item service is not called

### Requirement: Reject unsupported content types on update
The system SHALL respond with `415` when the request content type is not `application/json`.

#### Scenario: Plain text body
- **WHEN** a client sends `PUT /api/v1/items/1` with content type `text/plain`
- **THEN** the response status is `415`
