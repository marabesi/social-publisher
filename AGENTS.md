# AGENTS.md

Shared instructions for AI coding agents (opencode, GitHub Copilot, etc.) working
on **social-publisher**, a Kotlin/JVM monorepo that schedules and publishes posts
to social networks (Twitter today, LinkedIn planned). Work from the repository
root and keep changes inside it.

## Modules

- `core/` — domain and use cases (the `application` package) plus the shared
  outbound adapters (`csv`, `inmemory`, `social`). The `application` package
  must stay framework- and I/O-free.
- `cli/` — picocli commands (`adapters/inbound/cli`), the Guice composition root,
  `Main.kt`, the `CliOutput` adapter, and the Cucumber acceptance suite.
- `rest-api/` — Spring Boot REST API (`adapters/inbound/rest`), controllers and
  DTOs; wires the same core ports through Spring beans.
- `desktop/` — Compose Multiplatform desktop UI (`SocialPublisherStore`,
  `SocialPublisherApp`) over the same core.

`cli`, `rest-api` and `desktop` depend on `core`. `core` must never depend on them.

## Stack

- Kotlin 2.4 on JDK 25, built with Gradle (Kotlin DSL), multi-project.
- CLI: picocli. DI: Guice. REST: Spring Boot 4 + Spring Web. Desktop: Compose
  Multiplatform. JSON: kotlinx-serialization (core/CLI) and Jackson (REST).
  CSV: commons-csv.
- Tests: JUnit 5, MockK, Cucumber (Gherkin), jacoco.
- Quality: detekt (`detek.yml`), ktlint.

## Architecture (hexagonal) — respect it

- `core/src/main/kotlin/application/...` — domain/core: entities, use cases,
  ports (`application/persistence/*Repository.kt`, `application/Output.kt`,
  `application/socialnetwork/*`). No framework or I/O imports here.
- `core/src/main/kotlin/adapters/outbound/...` — implementations of the ports:
  `inmemory/`, `csv/` (filesystem persistence), `social/` (Twitter API).
- `cli/src/main/kotlin/adapters/inbound/cli/...` — picocli commands that drive
  the application layer; `adapters/outbound/cli/CliOutput.kt` prints output.
- `rest-api/src/main/kotlin/adapters/inbound/rest/...` — REST controllers that
  drive the same application layer.
- `desktop/src/main/kotlin/desktop/...` — Compose UI (`SocialPublisherApp`) and a
  thin store wrapper (`SocialPublisherStore`) over the same application layer.
- New behavior goes in `application/` behind a port, with adapters wired at the
  edges. Do not leak adapters into `application/`. Outbound adapters use
  `jakarta.inject.Inject` so both Guice and Spring can construct them.

## Conventions

- Match the surrounding style: small classes, constructor injection, one public
  type per file named after the file.
- Tests mirror the package layout: `application/*` for use cases, `integration/*`
  for filesystem adapters, `thirdpartyintegration/*` for third-party APIs
  (all in `core`); `adapters/cli/*` and `acceptance/*` in `cli`;
  `adapters/inbound/rest/*` in `rest-api`; `desktop/*` in `desktop`.
- Shared test helpers live in `core/src/testFixtures` (`MockedOutput`,
  `thirdpartyintegration/WireMockTwitter.kt`) and are consumed via
  `testImplementation(testFixtures(project(":core")))`.
- Twitter HTTP calls in tests are stubbed with **WireMock**
  (`thirdpartyintegration/WireMockTwitter.kt`); tests must never hit the live
  API. The base URL is overridden via the `twitter.api.baseUrl` system property.
- End-to-end CLI tests live in `cli/e2e` and run on **bashunit**.
- No comments unless asked. Keep changes minimal and focused.

## Commands

```sh
./gradlew build                       # full build (all modules)
./gradlew test                        # unit tests in every module
./gradlew :core:integrationTest       # filesystem/third-party integration
./gradlew :cli:cucumber               # Cucumber acceptance suite
./gradlew :rest-api:bootRun           # run the REST API on :8080
./gradlew :desktop:run                # run the Compose desktop app
./gradlew detekt ktlintCheck          # static analysis
./gradlew :core:test :cli:test :rest-api:test :desktop:test   # unit tests per module

./distribute.sh                       # build the ./social CLI distribution + a single social.jar
java -jar social.jar --help           # run the CLI from a single jar (also: rest serve / desktop run)
./lib/bashunit cli/e2e                # end-to-end CLI tests (bashunit)
```

- Skip live Twitter tests with:
  `CUCUMBER_FILTER_TAGS="not @interactsWithTwitter" ./gradlew :cli:cucumber`
- After code changes, run the relevant module tests and quality tasks; add or
  adjust tests for anything you change.

## End-to-end tests (bashunit)

- Runner: bashunit, invoked as `./lib/bashunit cli/e2e`. It is **not committed**
  (gitignored); install with:
  `curl -sL https://bashunit.typeddevs.com/install.sh | bash` (CI runs it via
  `.github/workflows/ci.yml`).
- Specs: `cli/e2e`. Use bashunit conventions — `test_*` functions,
  `set_up_before_script` / `tear_down_after_script`, and assertions like
  `assert_contains`.
- They exercise the packaged CLI via `sh ./social/bin/social ...`, so run
  `cli/distribute.sh` first to (re)build the `social/` distribution.

## Workflow

1. Read the relevant `application/` use case and its port before editing.
2. Implement in `core/`, then the inbound/outbound adapter as needed.
3. Add tests next to the matching existing test package/module.
4. Run the module test task (and `detekt`/`ktlintCheck` when touching style).
5. Report files changed with `file_path:line` references.
