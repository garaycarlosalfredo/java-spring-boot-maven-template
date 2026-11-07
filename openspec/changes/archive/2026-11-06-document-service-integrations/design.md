# Design

## Context

The architecture conventions already define feature packages with layer subpackages, a shared `exception` package, DTO ownership per feature, and the rule that a feature uses another feature only through its service. There is no external integration, no HTTP client dependency choice, and no shared package. See proposal.md.

## Goals / Non-Goals

**Goals:**
- Give derived projects a convention for the first client, consistent with the existing structure.
- State plainly that the guidance is not verified by a running integration.

**Non-Goals:**
- Choosing an HTTP client library (`RestClient`, `WebClient`, declarative clients) for all projects.
- A sample or fictional client, resilience library, or dependency.

## Decisions

- **Client layer name `client`, inside the owning feature.** It sits beside `controller`, `service`, `repository`, `entity`, and `dto`, and the feature's service calls it. Never create the package until a client exists, following the no-empty-packages rule.
- **Shared integrations by external service name.** When two features need the same service, create a package named after that service (for example `payments/`) with its own client, DTOs, and a service-level entry point, treated as a feature by the existing cross-feature rule.
- **Library left open.** The guidance describes responsibilities (timeouts, error translation, mapping), not a library. The decision of the library is recorded as a `TEMPLATE:` choice for the first real integration.
- **Errors:** one domain-level exception per failure category (unavailable, timeout, unexpected response), placed in `exception`, mapped by `ApiExceptionHandler` to Problem Details with fixed generic details.
- **Verification statement:** the README section says that it was reviewed against no concrete use case and must be reviewed when the first integration is added.

## Risks / Trade-offs

- Guidance written without a real integration may prove wrong → labeled unverified; the plan's completion criterion is a review against a concrete case.
- Naming the `client` package now could constrain derived projects → it is a convention, not a created package.
