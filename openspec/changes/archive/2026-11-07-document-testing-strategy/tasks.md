## 1. Review

- [x] 1.1 Map each behavior in `openspec/specs` to the automated test that covers it and list gaps
- [x] 1.2 Add tests for cheap in-scope gaps found in 1.1; record the rest as known limitations

## 2. Documentation

- [x] 2.1 Add a "Testing strategy" section to the README: table of automated coverage by test class, what stays manual, and prerequisites
- [x] 2.2 Add the manual persistence checklist (migration, create/read/update/delete round trip, restart durability, constraints) and the rule against using the shared database as a disposable test database
- [x] 2.3 Document how a derived project could add isolated integration tests separately from `verify`
- [x] 2.4 Align the README "Planned work" list, and the AGENTS.md testing lines

## 3. Verification

- [x] 3.1 Run `./mvnw clean verify` and report which checks executed
