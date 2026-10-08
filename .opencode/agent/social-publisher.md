---
description: Works on the social-publisher Kotlin CLI. Follows the repository root AGENTS.md.
mode: primary
temperature: 0.1
permission:
  edit: allow
  read: allow
  glob: allow
  grep: allow
  list: allow
  webfetch: allow
  websearch: allow
  todowrite: allow
  # Only ever touch files inside this repository worktree.
  external_directory: deny
  bash:
    # Allow development commands by default so routine work is never blocked.
    # The last matching rule wins, so the deny list stays at the bottom.
    "*": allow
    # Repository tooling.
    "git *": allow
    "./gradlew": allow
    "./gradlew *": allow
    "./distribute.sh": allow
    "./distribute.sh *": allow
    "./lib/bashunit": allow
    "./lib/bashunit *": allow
    "./acceptance.sh": allow
    "sh ./social/bin/social *": allow
    "docker *": allow
    # Read-only inspection and diagnostics.
    "ls *": allow
    "cat *": allow
    "find *": allow
    "rg *": allow
    "grep *": allow
    "sed *": allow
    "head *": allow
    "tail *": allow
    "wc *": allow
    "which *": allow
    "readlink *": allow
    "java -version": allow
    "javap *": allow
    "unzip *": allow
    "curl *": allow
    "python3 *": allow
    "ruby *": allow
    "node *": allow
    "npm *": allow
    "npx *": allow
    "bun *": allow
    "pnpm *": allow
    "yarn *": allow
    "mkdir *": allow
    "cp *": allow
    "mv *": allow
    "touch *": allow
    "echo *": allow
    "printf *": allow
    "sort *": allow
    "uniq *": allow
    "awk *": allow
    "tr *": allow
    "xargs *": allow
    # Never run these, even if a task seems to call for it.
    "sudo *": deny
    "rm -rf /*": deny
    "rm -rf ~*": deny
    "git push *": deny
    "git reset --hard *": deny
    "git clean *": deny
---

Follow the instructions in the repository root `AGENTS.md` for all work in this
project. Do not duplicate its content here; that file is the single source of
truth and is loaded automatically.

Never read, write, list, or otherwise access files outside this repository
worktree. All file tools and commands stay under the project root.

## Cross-surface features

Social Publisher is one product with three surfaces over the same `core`: the CLI
(`cli`), the REST API (`rest-api`) and the desktop app (`desktop`). A user should
be able to move between them and have the exact same features, messages and data.

- Design user-facing capabilities in `core/` first (a use case behind a port),
  then wire them into **all three** surfaces.
- Never ship a feature in a single adapter. If a surface truly cannot support it,
  call that out explicitly instead of leaving it out silently.
- All surfaces share the same store directory (`data/` or `SOCIAL_STORE_PATH`),
  so a change made in one must be visible from the others.
- Prefer structured use-case entry points over serializing only to satisfy a
  single adapter.

See the "Cross-surface consistency" section in `AGENTS.md` for the full rule.
