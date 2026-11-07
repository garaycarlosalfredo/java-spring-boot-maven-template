# Design

## Context

The template is a single Maven module with package `com.example.template`, an `items` example feature with Flyway migrations, and `TEMPLATE:` comments in the configuration where derived projects adapt settings. Other stages are planned or drafted as changes (testing, CI, security, operations, integrations). See proposal.md.

## Goals / Non-Goals

**Goals:**
- A derived-project owner can follow one checklist from clone to first deployable service.
- Every claim is accurate for the repository at the time of writing and separates implemented from planned.

**Non-Goals:**
- A scaffolding script or generator (AGENTS.md forbids custom startup scripts and nothing here justifies one).
- Choosing a license other than MIT, which the owner decided.

## Decisions

- **One checklist, linked from the README,** kept short; deeper topics link to their own sections (testing, security, operations, integrations). If it exceeds a screen or two, move it to `docs/adapting-the-template.md` and link it.
- **Rename order matters,** so the checklist gives an order: coordinates in `pom.xml`, package rename (directories, `package` lines, imports, the main class), configuration, migrations, then the example feature.
- **Example feature options** are stated plainly: keep `items` as the reference, rename it, or delete it with its migration only if no database has applied it (applied migrations must not be rewritten; add a new version otherwise).
- **MIT license, applied last.** A task adds `LICENSE` with the MIT text and sets `<licenses>` in `pom.xml` (name `MIT License`, URL `https://opensource.org/license/mit`). The notice is `Copyright (c) 2026 Carlos Garay`, confirmed by the owner.
- **Verification by dry run.** The checklist is executed on a throwaway copy (for example in the scratchpad or `/tmp`-style temporary directory, outside the repo) and the findings are folded back.

## Risks / Trade-offs

- The checklist describes security, operations, and integration choices that may still be unimplemented → each item is labeled as implemented or planned and links to its plan.
- A package rename dry run can reveal missed references (for example `TEMPLATE:` comments or test names) → corrections are part of the change.
- Without the `LICENSE` file the repository is not reusable by default → the file is added in this change; nothing else blocks it.
