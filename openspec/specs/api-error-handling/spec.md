# api-error-handling Specification

## Purpose

Defines the common shape of error responses so clients and derived projects can rely on one format, including for failures the server did not anticipate.

## Requirements

### Requirement: Unexpected failures return a generic problem response
The system SHALL respond with `500` and an `application/problem+json` body whose detail is a fixed generic message when a request fails with an unexpected exception, and SHALL NOT expose the exception message, type, stack trace, or rejected request payload in the response; the standard `instance` field carries only the request path.

#### Scenario: Service throws an unexpected exception
- **WHEN** a request reaches the service and the service throws an unexpected runtime exception
- **THEN** the response status is `500`
- **AND** the content type is `application/problem+json`
- **AND** the body does not contain the exception message

### Requirement: Unexpected failures are logged on the server
The system SHALL log an unexpected exception at error level with its stack trace, so that the generic response can be traced by operators.

#### Scenario: Failure is logged
- **WHEN** an unexpected exception is mapped to `500`
- **THEN** the exception is logged on the server
