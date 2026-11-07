# Spec Delta

## MODIFIED Requirements

### Requirement: Every API request requires authentication
The system SHALL require a successfully authenticated principal for every request to the API, including all item operations, with the single exception of the health endpoints defined by the `operational-health` capability, and the behavior described by the other API specifications SHALL apply to authenticated requests.

#### Scenario: Unauthenticated request is rejected
- **WHEN** a client sends any request to `/api/v1/items` or a sub-path without credentials
- **THEN** the response status is `401`
- **AND** the item service is not called

#### Scenario: Authenticated request proceeds
- **WHEN** an authenticated principal sends a valid request to an existing endpoint
- **THEN** the endpoint responds as described by its own specification

#### Scenario: Health endpoints need no credentials
- **WHEN** a client requests a health endpoint without credentials
- **THEN** the request is not rejected for missing authentication

#### Scenario: Every other path stays closed
- **WHEN** a client sends a request without credentials to any path that is not a health endpoint
- **THEN** the response status is `401`
