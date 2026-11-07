## 1. Workflow

- [x] 1.1 Add `.github/workflows/ci.yml` with triggers on push to `development` and pull requests, read-only permissions, JDK 25 (Temurin) with Maven cache, and `./mvnw --batch-mode verify`
- [x] 1.2 Confirm locally that `./mvnw --batch-mode verify` passes and that the workflow needs no secrets or `.env`

## 2. Documentation

- [x] 2.1 Add a CI section to the README: provider, triggers (push to `development` and all pull requests), Java version, command, and that there are no CI prerequisites
- [x] 2.2 Record the decision to defer formatting, static analysis, and dependency checks

## 3. Verification

- [x] 3.1 Run `./mvnw clean verify` locally and report which checks executed
- [ ] 3.2 User pushes a branch and confirms the GitHub Actions run succeeded; record the result in the README and plan (a local build does not prove CI)
