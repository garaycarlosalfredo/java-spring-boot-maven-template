---
name: sync-docs
description: Check that README.md, AGENTS.md, and plans/ match the current code, and update them where they drifted. Use once a significant, user-visible change is finished - a new or changed endpoint, new behavior or validation rule, a configuration or migration change, or a feature moving from planned to implemented - or when asked to sync, update, or review the documentation. Do not use for internal refactors, renames, formatting, or test-only changes.
---

# Sync documentation with code

Documentation here is part of the deliverable: `AGENTS.md` requires README and code to stay synchronized, and a feature must not be marked complete while its verification is pending.

## Steps

1. **Find what changed.** Run `git status --short` and `git diff`. If the user named a feature, focus on it.
2. **Read the docs first.** Read `README.md` and `AGENTS.md`. Skim `plans/README.md` if the change touches planned work.
3. **Compare against the code, section by section.** Check the README sections that the change can affect:
   - `Current API`, `Input validation and errors`, `Item service`: routes, status codes, headers, request and response fields, validation rules, error format.
   - `Database configuration` and `Schema migrations`: properties, environment variables, migration files and their verification notes.
   - `Project structure and architecture`: packages and layering.
   - `Development and verification`: commands, and which checks were actually executed.
   - `Planned work` and `plans/`: items that are now implemented, or still pending.
   - The status summary in `AGENTS.md`: implemented versus pending features.
4. **Update only what drifted.** Make minimal edits that keep the existing structure and tone. Do not rewrite sections that are still correct.
5. **Report.** List each file and section you changed and why. List anything you found but did not change, and anything you could not confirm.

## Rules

- Write in English. Conversation may be Spanish; repository documentation is not.
- Never mark a feature complete or verified unless the check was actually run. Distinguish executed checks from expected results. If verification is pending, say it is pending.
- Do not invent behavior. Every statement about the API or configuration must be traceable to code you read.
- Move an item out of `Planned work` only when it is implemented and verified. Otherwise leave it, or mark it as partially done.
- Keep `AGENTS.md` and `README.md` consistent with each other. If they disagree, tell the user instead of choosing silently.
- Do not store secrets or real data in examples. Use only generic placeholders. Never open, print, or copy the local `.env`.
- Do not change code in this skill. If the docs are right and the code is wrong, report it.
