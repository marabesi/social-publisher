# Social publisher - schedule posts on social network for developers

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/c7b738b1eb434894a1927f5a6aba588c)](https://www.codacy.com/gh/marabesi/social-publisher/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=marabesi/social-publisher&amp;utm_campaign=Badge_Grade) [![Coverage Status](https://coveralls.io/repos/github/marabesi/social-publisher/badge.svg?branch=main)](https://coveralls.io/github/marabesi/social-publisher?branch=main)

Social publisher allows you to schedule and publish posts into social media. It
comes as a desktop app, a command line tool and a REST API that all share the
same local data, so you can use whichever fits your workflow.

Currently supported:

- Twitter (X)
- LinkedIn

## Download and run

Grab the latest release from the
[releases page](https://github.com/marabesi/social-publisher/releases/latest):

- **Desktop app** — download the installer for your OS (`.dmg`, `.msi` or
  `.deb`), install it and open it. No terminal needed.
- **Single jar** — download `social.jar` and run:

  ```sh
  java -jar social.jar desktop run     # desktop app
  java -jar social.jar --help          # command line
  java -jar social.jar rest serve      # REST API on :8080 (Swagger UI at /swagger-ui/index.html)
  ```

The only requirement is **Java 25 or newer** when using `social.jar`; the native
desktop installer bundles it.

The full step-by-step is in the
[installation guide](https://marabesi.github.io/social-publisher/guide/installation).

## Documentation

The user guide lives at
<https://marabesi.github.io/social-publisher/> and covers configuration,
managing posts, scheduling, publishing, the REST API and the desktop app.

## Project layout

This repository is a monorepo with four Gradle modules sharing the same use
cases and data:

- `core` — domain and shared adapters.
- `cli` — picocli command line.
- `rest-api` — Spring Boot REST API.
- `desktop` — Compose Multiplatform desktop UI.

## Contributing

Want to build Social Publisher from source or work on it? Everything about the
local setup, tests, documentation site and releases lives in
[`CONTRIBUTING.md`](CONTRIBUTING.md).

## License

Released under the Apache-2.0 License.
