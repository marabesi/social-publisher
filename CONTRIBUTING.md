# Contributing to Social Publisher

Thanks for taking the time to contribute! This document covers everything needed
to build Social Publisher from source, run the test suites and work on the
documentation.

If you just want to **use** Social Publisher, you do not need any of this — head
to the [user guide](https://marabesi.github.io/social-publisher/) and download a
release instead.

## Prerequisites

- **JDK 25** (or newer) on your `PATH`. Verify with:

  ```sh
  java -version
  ```

- **Git** to clone the repository.
- **Node.js 22** — only if you want to build the documentation site.
- **Docker** — only if you want to build the container image.

You do **not** need to install Gradle: the repository ships with the Gradle
wrapper (`./gradlew`).

## Architectural decisions

Social Publisher follows a hexagonal (ports and adapters) architecture. New
behavior belongs in `application/` behind a port, with concrete adapters wired at
the edges.

- [Hexagonal architecture — definition](https://marabesi.com/architecture/2022/04/13/hexagonal-architecture)
- [Hexagonal architecture — Stack Overflow](https://stackoverflow.com/a/14659492/2258921)
- [Architecture overview](docs/architecture.md)
- Repository conventions: [`AGENTS.md`](AGENTS.md)

## Build from source

Clone the repository and build the distribution:

```sh
git clone https://github.com/marabesi/social-publisher.git
cd social-publisher
./distribute.sh
```

`./distribute.sh` produces two things:

- **`social.jar`** in the project root — a single self-contained jar with the
  CLI, the REST API and the desktop app on board.
- **`social/`** — the unpacked CLI distribution with the `social/bin/social`
  launcher.

Run the freshly built tools:

```sh
./social/bin/social --help
# or, using the single jar:
java -jar social.jar --help
java -jar social.jar rest serve          # REST API on :8080
java -jar social.jar desktop run         # Compose desktop app
```

::: tip Alternative layouts
If you only need the unpacked launcher, `./gradlew installDist` puts it at
`build/install/social/bin/social`. On Windows, use `social\bin\social.bat`.
:::

### Module tasks

| Command | What it does |
| --- | --- |
| `./gradlew :rest-api:bootRun` | Run the REST API from source on `:8080`. |
| `./gradlew :desktop:run` | Run the Compose desktop app from source. |
| `./gradlew :rest-api:bootJar` | Build the standalone REST API jar. |
| `./gradlew :desktop:packageDistributionForCurrentOS` | Build a native desktop installer for the current OS (output in `desktop/build/compose/binaries/`). |

## Tests and quality

```sh
./gradlew build                                                     # full build (all modules)
./gradlew test                                                      # unit tests in every module
./gradlew :core:integrationTest                                     # filesystem / third-party integration
./gradlew :cli:cucumber                                             # Cucumber acceptance suite
./gradlew detekt ktlintCheck                                        # static analysis
./gradlew :core:test :cli:test :rest-api:test :desktop:test         # unit tests per module
```

Twitter HTTP calls in tests are stubbed with WireMock; tests never hit the live
API. To skip the suite that really talks to X (Twitter):

```sh
CUCUMBER_FILTER_TAGS="not @interactsWithTwitter" ./gradlew :cli:cucumber
```

### End-to-end tests (bashunit)

The end-to-end CLI tests live in `cli/e2e` and run on **bashunit**, which is not
committed. Install it and run the suite against the packaged CLI:

```sh
curl -sL https://bashunit.typeddevs.com/install.sh | bash
./distribute.sh
./lib/bashunit cli/e2e
```

## Documentation site

The user guide is built with [VitePress](https://vitepress.dev/) and lives under
[`docs/`](docs). To run it locally:

```sh
cd docs
npm install
npm run docs:dev
```

Other available scripts are `npm run docs:build` and `npm run docs:preview`.

## Docker

Build the distribution and the image, then run it:

```sh
./distribute.sh
docker build --platform=linux/amd64 . -t social
docker run --platform=linux/amd64 --rm social
```

For persistent configuration, mount a host folder as the data volume:

```sh
docker run --platform=linux/amd64 -v "$(pwd)/data:/data" --rm social
```

`run.sh` wraps the build and run commands above.

## Publishing to Maven Central

Publishing runs from the manual `Publish to central` workflow
(`.github/workflows/publish.yml`), which executes `./gradlew publish` with the
`OSSRH_*` secrets.

- [Publish to Maven Central using Gradle](https://h4pehl.medium.com/publish-your-gradle-artifacts-to-maven-central-f74a0af085b1)

## Integrations

### Twitter

- <https://developer.twitter.com/en/docs/authentication/oauth-1-0a>
  - <https://developer.twitter.com/en/docs/authentication/oauth-1-0a/authorizing-a-request>
  - <https://developer.twitter.com/en/docs/authentication/oauth-1-0a/api-reference/request_token>
  - <https://developer.twitter.com/en/docs/authentication/oauth-1-0a/api-reference/authorize>
  - <https://github.com/twitterdev/Twitter-API-v2-sample-code/blob/main/Manage-Tweets/create_tweet.js>
- <https://gist.github.com/robotdan/33f5834399b6b30fea2ae59e87823e1d>
- <https://developer.twitter.com/en/docs/twitter-api/v1/tweets/post-and-engage/api-reference/post-statuses-update>
- <https://docs.spring.io/spring-social-twitter/docs/1.1.0.RELEASE/reference/htmlsingle>
- <https://github.com/spring-attic/spring-social-twitter>

## References

- <https://kotlinlang.org/docs/command-line.html#compile-a-library>
- <https://picocli.info>
- <https://cucumber.io/docs/gherkin/reference>
- <https://www.baeldung.com/kotlin/assertfailswith>
- <https://www.baeldung.com/java-dockerize-app>
