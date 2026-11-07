## 1. Documentation

- [x] 1.1 Write the adaptation checklist: Maven coordinates, application name and main class, Java package rename, configuration and `TEMPLATE:` comments, environment variables and `.env.example`, database and schema ownership, and the example `items` feature options
- [x] 1.2 Write the "Before deployment" list linking the security, operations, and integration choices, labeled implemented or planned
- [x] 1.3 Document how to run the application and tests and which checks need external services or are not automated
- [x] 1.4 Review the README: separate current behavior from historical verification notes and link the final conventions
- [x] 1.5 State in the README the license (MIT) and link the `LICENSE` file

## 2. Verification

- [x] 2.1 Execute the checklist on a disposable derived copy outside the repository (rename coordinates and package, build, run the tests) and correct missing or misleading steps
- [x] 2.2 Run `./mvnw clean verify` in this repository and report which checks executed

## 3. License (MIT, chosen by the owner)

- [x] 3.1 Add the `LICENSE` file with the MIT text and the notice `Copyright (c) 2026 Carlos Garay`, update `<licenses>` in `pom.xml` and the README, and confirm they match
