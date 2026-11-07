# Proposal

## Why

Stage 4 of the template improvement plan asks that developers can tell what is tested automatically and what remains manual. Today that knowledge is spread across README, AGENTS.md, and past change notes:

- Automated tests are five classes: `ItemControllerTests` (`@WebMvcTest`, mocked service), `ItemServiceTests` (mocked repository), `ItemTests` (entity), and the two request record tests. None starts persistence.
- Flyway migrations, JPA mappings, queries, constraints, transactions, and durability are verified only by hand against the external PostgreSQL, and the steps and results are not collected in one place.
- The README "Planned work" list still shows persistence across container recreation as undone, although a restart check passed earlier.

## What Changes

- Record the decision (made with the user): keep the current policy of no PostgreSQL or Testcontainers in automated tests. Automated database integration tests are left to derived projects, and the template documents how they could add them.
- Add a "Testing strategy" section to the README with a table: what each test class covers, what is covered only manually, and the prerequisites for each manual check.
- Collect the manual persistence checklist (migration applied, create/read/update/delete round trip, restart durability, constraints) as reproducible steps that never touch the shared database destructively beyond sample rows, and say that the shared development database must not be used as a disposable test database.
- Review the existing tests against the endpoint behaviors and list any gap found; add only tests for gaps that are cheap and in scope, otherwise record them as known limitations.
- Update the README "Planned work" list, and the AGENTS.md testing lines so they agree.
- No production code, dependency, or build change.

## Capabilities

### New Capabilities
<!-- None: documentation and test-gap work only; no spec delta (skip_specs). -->

### Modified Capabilities
<!-- None. -->

## Impact

- Modified docs: README, AGENTS.md.
- Possibly new test cases in existing test classes if the review finds a cheap gap.
- No new dependencies or Docker requirement.
