# AGENTS.md

Shared instructions for AI coding agents (opencode, GitHub Copilot, etc.) working
on **social-publisher**, a Kotlin/JVM CLI that schedules and publishes posts to
social networks (Twitter today, LinkedIn planned). Work from the repository root
and keep changes inside it.

## Stack

- Kotlin 2.4 on JDK 25, built with Gradle (Kotlin DSL).
- CLI: picocli. DI: Guice. JSON: kotlinx-serialization. CSV: commons-csv.
- Tests: JUnit 5, MockK, Cucumber (Gherkin), jacoco.
- Quality: detekt (`detek.yml`), ktlint.

## Architecture (hexagonal) — respect it

- `src/main/kotlin/application/...` — domain/core: entities, use cases,
  ports (`application/persistence/*Repository.kt`, `application/Output.kt`,
  `application/socialnetwork/*`). No framework or I/O imports here.
- `src/main/kotlin/adapters/inbound/cli/...` — picocli commands that drive
  the application layer.
- `src/main/kotlin/adapters/outbound/...` — implementations of the ports:
  `inmemory/`, `csv/` (filesystem persistence), `social/` (Twitter API),
  `cli/` (output).
- New behavior goes in `application/` behind a port, with adapters wired at
  the edges. Do not leak adapters into `application/`.

## Conventions

- Match the surrounding style: small classes, constructor injection via
  Guice, one public type per file named after the file.
- Tests mirror the package layout under `src/test/kotlin`: `application/*`
  for use cases, `adapters/cli/*` for commands, `integration/*` for
  filesystem adapters, `thirdpartyintegration/*` for third-party APIs, and
  `acceptance/*` for Cucumber steps (`src/test/resources/*.feature`).
- Twitter HTTP calls in tests are stubbed with **WireMock**
  (`thirdpartyintegration/WireMockTwitter.kt`); tests must never hit the live
  API. The base URL is overridden via the `twitter.api.baseUrl` system property.
- End-to-end CLI tests live in `e2e/*_test.sh` and run on **bashunit**.
- No comments unless asked. Keep changes minimal and focused.

## Commands

```sh
./gradlew test                 # unit tests (excludes acceptance + thirdparty)
./gradlew integrationTest      # filesystem/third-party integration
./gradlew cucumber             # Cucumber acceptance suite
./gradlew detekt ktlintCheck   # static analysis
./gradlew build                # full build

./distribute.sh                # build the runnable ./social distribution
./lib/bashunit e2e             # end-to-end CLI tests (bashunit)
```

- Skip live Twitter tests with:
  `CUCUMBER_FILTER_TAGS="not @interactsWithTwitter" ./gradlew cucumber`
- After code changes, run `./gradlew test` and the relevant quality task;
  add/adjust tests for anything you change.

## End-to-end tests (bashunit)

- Runner: bashunit, invoked as `./lib/bashunit e2e`. It is **not committed**
  (gitignored); install with:
  `curl -s https://bashunit.typeddevs.com/install.sh | bash` (CI runs it via
  `.github/workflows/ci.yml`).
- Specs: `e2e/*_test.sh`. Use bashunit conventions — `test_*` functions,
  `set_up_before_script` / `tear_down_after_script`, and assertions like
  `assert_contains`.
- They exercise the packaged CLI via `sh ./social/bin/social ...`, so run
  `./distribute.sh` first to (re)build the `social/` distribution.

## Workflow

1. Read the relevant `application/` use case and its port before editing.
2. Implement in the core, then the inbound/outbound adapter as needed.
3. Add unit tests next to the matching existing test package.
4. Run `./gradlew test` (and `detekt`/`ktlintCheck` when touching style).
5. Report files changed with `file_path:line` references.
