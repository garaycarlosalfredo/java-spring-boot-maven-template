## 1. README

- [x] 1.1 Add an "Architecture conventions" subsection under "Project structure and architecture" covering feature packages with layer subpackages, naming without the `Jpa` suffix and `public` classes, the `exception/` package, one controller per resource (base path on the class, `@Nested` test groups), the shared `ResourceNotFoundException`, DTO ownership, transaction boundaries, and the two validation roles; state that the example is aligned in `restructure-item-packages`
- [x] 1.2 Check every statement about existing behavior against the current code and label conventions the code does not follow yet as the target

## 2. AGENTS.md and plan

- [x] 2.1 Update the organization and layering bullets in AGENTS.md with the matching concise rules, replacing the root-package rule for error handling and pointing to the README section; keep README and AGENTS.md consistent
- [x] 2.2 Update `plans/01-architecture.md`: mark the documented conventions as done, record `restructure-item-packages` as the next change, and list moving `keepsRootUnmapped` as the only remaining pending item

## 3. Verification

- [x] 3.1 Run `./mvnw verify` to confirm nothing else changed (documentation-only change); report which checks executed
- [x] 3.2 Re-read the three edited files for English-only text and for no claims of unverified behavior
