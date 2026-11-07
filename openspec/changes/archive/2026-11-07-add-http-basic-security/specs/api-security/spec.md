## Purpose

Defines the access-control baseline of the HTTP API: which requests need credentials, how the single configured user authenticates over HTTP Basic, how rejected requests are reported, and what security configuration is mandatory at startup, so derived projects begin closed and adaptable instead of open.

## ADDED Requirements

### Requirement: Every API request requires authentication
The system SHALL require a successfully authenticated principal for every request to the API, including all item operations, and the behavior described by the other API specifications SHALL apply to authenticated requests.

#### Scenario: Unauthenticated request is rejected
- **WHEN** a client sends any request to `/api/v1/items` or a sub-path without credentials
- **THEN** the response status is `401`
- **AND** the item service is not called

#### Scenario: Authenticated request proceeds
- **WHEN** an authenticated principal sends a valid request to an existing endpoint
- **THEN** the endpoint responds as described by its own specification

### Requirement: HTTP Basic with one configured user
The system SHALL authenticate requests using HTTP Basic credentials checked against a single user held in memory, and SHALL NOT offer login forms, token endpoints, or registration.

#### Scenario: Correct credentials
- **WHEN** a request carries an `Authorization` header with the configured username and password
- **THEN** the request proceeds as an authenticated request

#### Scenario: Wrong password
- **WHEN** a request carries the configured username with a different password
- **THEN** the response status is `401`

#### Scenario: Only the configured user exists
- **WHEN** a request presents credentials for any other username
- **THEN** the response status is `401`

### Requirement: Credentials are mandatory startup configuration
The system SHALL read the username and the password from two required configuration values with no default, and SHALL fail to start when either value is absent or blank, before serving any request.

#### Scenario: A required value is missing
- **WHEN** the application starts with one of the two values not provided
- **THEN** startup fails
- **AND** no port is opened for requests

#### Scenario: A required value is blank
- **WHEN** the application starts with one of the two values provided as empty or whitespace
- **THEN** startup fails

### Requirement: No credential ships or is generated
The system SHALL NOT include default credentials, committed secret values, or a startup-generated password, and SHALL log no generated credential at startup.

#### Scenario: Repository and startup stay credential-free
- **WHEN** the template is checked out and started with valid locally supplied credentials
- **THEN** the repository contains only generic placeholders for the two values
- **AND** the startup log does not print any generated password or default account notice

### Requirement: The password is stored only as a hash
The system SHALL store the configured password in memory only as a cryptographically hashed value and SHALL authenticate against that hash, so the plain configured value is never kept as the stored credential.

#### Scenario: Hashed storage is verified
- **WHEN** the application starts with valid credentials
- **THEN** the in-memory user holds a hash of the password rather than the plain value

### Requirement: Rejected requests use Problem Details
The system SHALL report a rejected request as `application/problem+json` with a fixed generic detail that does not reveal internal information: unauthenticated requests as `401` with an HTTP Basic authentication challenge, and requests denied by an authorization rule as `403`.

#### Scenario: Unauthenticated problem response
- **WHEN** an unauthenticated request is rejected
- **THEN** the response status is `401`
- **AND** the content type is `application/problem+json`
- **AND** the response includes a `WWW-Authenticate: Basic` challenge
- **AND** the body contains no stack trace, exception message, or configuration value

#### Scenario: Authorized-rule denial
- **WHEN** an authenticated request is denied by an authorization rule
- **THEN** the response status is `403`
- **AND** the content type is `application/problem+json`

### Requirement: Authentication is stateless
The system SHALL NOT create or use an HTTP session to remember an authenticated request; every request must present its credentials on its own.

#### Scenario: Repeated authenticated requests
- **WHEN** a client sends two requests with valid credentials and does not reuse any cookie
- **THEN** both requests authenticate independently
- **AND** no session cookie is issued on the first response

### Requirement: CSRF protection is disabled for the baseline
The system SHALL NOT require a CSRF token for requests that carry explicit credentials, because authentication is per-request and no browser-session cookie is involved.

#### Scenario: State-changing request without a token
- **WHEN** an authenticated client sends a state-changing request without any CSRF token
- **THEN** the request is processed normally

### Requirement: CORS is disabled by default
The system SHALL NOT grant cross-origin browser access by default: it SHALL answer preflight and cross-origin requests without access-control grant headers.

#### Scenario: Cross-origin request gets no grant
- **WHEN** a browser sends a request with an `Origin` header to an item endpoint
- **THEN** the response does not include an `Access-Control-Allow-Origin` grant
