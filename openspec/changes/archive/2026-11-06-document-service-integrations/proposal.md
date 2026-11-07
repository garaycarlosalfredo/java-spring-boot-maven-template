# Proposal

## Why

Stage 8 of the template improvement plan asks for guidance on how derived projects add microservice clients, without adding a fictional integration to the template. Today the repository only says that clients "can be added when an integration requires them" (README architecture section and AGENTS.md) and that service clients are "documented when implemented". A derived project that needs its first client has no convention for where it goes, how it is configured, how errors are translated, or how it is tested.

## What Changes

- Add an "External service clients" section to the README architecture conventions covering:
  - **Placement:** a client used by one feature lives in that feature (`<feature>/client`, with its own response DTOs); an integration shared by several features goes in a common package named after the external service, used through its own service-level API and never through another feature's DTOs or entities.
  - **Configuration:** service address, credentials, and timeouts as externalized properties (environment variables, never committed), with `TEMPLATE:` comment conventions.
  - **Error translation:** client failures are translated to a domain-level exception in the `exception` package and mapped by `ApiExceptionHandler` to Problem Details (for example `502` or `504`), never leaking upstream bodies.
  - **Mapping:** an external response DTO is mapped to an internal or public DTO at the boundary, as the architecture conventions already state.
  - **Tests:** controlled responses with a stubbed client or a mock HTTP server in `@WebMvcTest`-style tests; no real network in automated tests.
  - **Resilience:** retries, idempotency, authentication, observability, and circuit breaking are decided per real integration and not added by default.
- Mark the guidance as unverified until a real integration exists, as the plan requires.
- Update and the AGENTS.md line about clients so they point to the new section.
- No code, dependency, or example client is added.

## Capabilities

### New Capabilities
<!-- None: documentation only; no spec delta (skip_specs). -->

### Modified Capabilities
<!-- None. -->

## Impact

- Modified docs only: README, AGENTS.md.
- No dependencies, code, endpoints, or schema changes.
