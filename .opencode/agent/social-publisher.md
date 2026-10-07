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
  # Only ever touch files inside this repository worktree.
  external_directory: deny
  bash:
    # Anything not listed below still requires confirmation.
    "*": ask
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
    # Never run these, even if a task seems to call for it.
    "sudo *": deny
    "rm -rf /*": deny
    "git push *": deny
---

Follow the instructions in the repository root `AGENTS.md` for all work in this
project. Do not duplicate its content here; that file is the single source of
truth and is loaded automatically.

Never read, write, list, or otherwise access files outside this repository
worktree. All file tools and commands stay under the project root.
