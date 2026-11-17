## type/NNN-branch-name

<!-- PR title = branch name, same format: type/NNN-short-slug (for example feat/002-watch-reset-command). -->

### Summary

<!-- One paragraph: what the PR delivers, what it builds on, and the main verification result. -->

### Changes

- **Logical block** (for example the `:apps:mobile` module, the `:apps:wear` module, or a document)
  - Concrete change: `path/or/file`, what it does.
- **Deliverables**: files delivered, with their format.

### Technical Details

- Technical decisions and why (shared protocol paths/keys, module structure, UI choices).
- Bugs found and how they were fixed; documented fallbacks or limitations.
- New dependencies (or "No new dependencies") and what stays out of Git.
- Assignment step or deliverable covered (project setup, feature implementation, source code, demo, report).

### Verification

- `./gradlew projects`: result (lists `:apps:mobile` and `:apps:wear`).
- `./gradlew :apps:mobile:assembleDebug :apps:wear:assembleDebug`: result.
- Manual tests: run on the phone and watch emulators, demo of the implemented behavior.
- Not executed: anything not run, listed separately.
