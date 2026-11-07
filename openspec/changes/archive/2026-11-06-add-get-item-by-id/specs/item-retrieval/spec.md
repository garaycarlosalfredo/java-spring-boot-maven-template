## ADDED Requirements

### Requirement: Retrieve an item by ID
The system SHALL expose `GET /api/v1/items/{id}` that returns the item with that ID as JSON using the same representation (`id`, `sku`, `name`, `description`, `price`) as item creation and listing.

#### Scenario: Item exists
- **WHEN** a client requests `GET /api/v1/items/1` and item 1 exists
- **THEN** the response status is `200`
- **AND** the content type is `application/json`
- **AND** the body contains the `id`, `sku`, `name`, `description`, and `price` of item 1

### Requirement: Report a missing item
The system SHALL respond with `404` and a `ProblemDetail` JSON body when no item exists for the requested ID.

#### Scenario: Item does not exist
- **WHEN** a client requests `GET /api/v1/items/999` and no item 999 exists
- **THEN** the response status is `404`
- **AND** the content type is `application/problem+json`

### Requirement: Reject a malformed ID
The system SHALL respond with `400` and a `ProblemDetail` JSON body when the `{id}` path segment is not a valid 64-bit integer, without invoking the item service.

#### Scenario: Non-numeric ID
- **WHEN** a client requests `GET /api/v1/items/abc`
- **THEN** the response status is `400`
- **AND** the item service is not called
