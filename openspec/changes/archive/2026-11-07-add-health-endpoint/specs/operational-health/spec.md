# Spec Delta

## Purpose
Defines how the application reports whether it is alive and ready to serve traffic, so deployment platforms can monitor it without credentials and without leaking internal details.

## ADDED Requirements

### Requirement: Liveness probe
The system SHALL expose a liveness endpoint that reports `UP` while the application process is running, without requiring credentials.

#### Scenario: Application running
- **WHEN** a client requests `GET /actuator/health/liveness` without credentials
- **THEN** the response status is `200`
- **AND** the body reports status `UP`

### Requirement: Readiness probe depends on the database
The system SHALL expose a readiness endpoint, available without credentials, that reports `UP` only when the application can serve traffic, including that the database is reachable, and reports `DOWN` with status `503` otherwise.

#### Scenario: Database reachable
- **WHEN** a client requests `GET /actuator/health/readiness` without credentials and the database is reachable
- **THEN** the response status is `200`
- **AND** the body reports status `UP`

#### Scenario: Database unreachable
- **WHEN** a client requests `GET /actuator/health/readiness` and the database is not reachable
- **THEN** the response status is `503`
- **AND** the body reports status `DOWN`

#### Scenario: Database recovers
- **WHEN** the database becomes reachable again
- **THEN** the readiness endpoint reports `UP` with status `200` without restarting the application

### Requirement: Health responses expose no details
The system SHALL NOT include component details, connection information, or exception messages in any health response.

#### Scenario: Response body
- **WHEN** a client requests any health endpoint
- **THEN** the body contains only the overall status

### Requirement: Only health is exposed
The system SHALL expose no management endpoint other than health, and every path other than the health endpoints SHALL remain subject to the authentication required by the security baseline.

#### Scenario: Other management endpoint without credentials
- **WHEN** a client requests `GET /actuator/env` or `GET /actuator/beans` without credentials
- **THEN** the response status is `401`

#### Scenario: Other management endpoint with credentials
- **WHEN** an authenticated client requests `GET /actuator/env` or `GET /actuator/beans`
- **THEN** the endpoint is not available and exposes no data
