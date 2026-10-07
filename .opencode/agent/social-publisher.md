---
description: Works on the social-publisher Kotlin CLI. Follows the repository root AGENTS.md.
mode: primary
temperature: 0.1
permission:
  edit: allow
  bash:
    "*": ask
    "git *": allow
    "./gradlew": allow
    "./gradlew *": allow
    "./distribute.sh": allow
    "./lib/bashunit": allow
    "./lib/bashunit *": allow
    "./acceptance.sh": allow
    "sh ./social/bin/social *": allow
    "docker *": allow
---

Follow the instructions in the repository root `AGENTS.md` for all work in this
project. Do not duplicate its content here; that file is the single source of
truth and is loaded automatically.
