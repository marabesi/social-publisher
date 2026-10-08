# Architecture

::: info For developers
This page describes how Social Publisher is built. If you only want to use the
tool, start with [Installation](/guide/installation).
:::

Social Publisher is a Kotlin/JVM monorepo built around a hexagonal (ports and
adapters) architecture. The core use cases are plain Kotlin and never import a
framework or touch I/O directly; everything external is reached through a port.

## Modules

| Module | Content |
| --- | --- |
| `core` | `application/*` (entities, use cases, ports) plus the shared outbound adapters: `csv/`, `inmemory/`, `social/`. |
| `cli` | picocli commands, Guice composition root, `CliOutput`, and the Cucumber acceptance suite. |
| `rest-api` | Spring Boot application, REST controllers (`adapters/inbound/rest`) and DTOs. |
| `desktop` | Compose Multiplatform desktop UI (`desktop/`) over the same application layer. |

`cli`, `rest-api` and `desktop` all depend on `core`; `core` never depends on the
others.

## High-level view

```text
             +---------------------------+      +-----------------------------+     +---------------------------+
             |        social CLI         |      |          REST API           |     |         Desktop            |
             |     picocli + Guice       |      |        Spring Boot          |     |       Compose Multiplatform |
             +-------------+-------------+      +--------------+--------------+     +-------------+-------------+
                           |                                  |                               |
              inbound CLI adapters            inbound REST adapters            inbound Compose UI
                           |                                  |                               |
                           +---------------+------------------+-------------------------------+
                                           |
                             +-------------v-------------+
                             |      application core     |
                             |    use cases + ports      |
                             +--+---------+-----------+--+
                                |         |           |
                   CSV persistence|      output      | social adapter
                                |         |           |
                                v         v           v
                         +-----------+  +-----+  +-----------------+
                         |  data/    |  |stdout| | X / Twitter API |
                         | CSV+JSON  |  +-----+ |   OAuth 1.0a    |
                         +-----------+          +-----------------+
```

## Layers

| Layer | Location | Responsibility |
| --- | --- | --- |
| Core | `core/src/main/kotlin/application` | Entities, use cases and ports. No framework or I/O imports. |
| Inbound adapters | `cli/.../adapters/inbound/cli`, `rest-api/.../adapters/inbound/rest`, `desktop/` | picocli commands, REST controllers, and a Compose UI that drive the use cases. |
| Outbound adapters | `core/src/main/kotlin/adapters/outbound` | Port implementations: CSV persistence, Twitter, in-memory (tests). |

Persistence ports live under `application/persistence`, output under
`application/Output.kt`, and social network integrations under
`application/socialnetwork`.

## The flow

1. **Credentials** — create an X developer app and collect OAuth 1.0a keys.
2. **Configuration** — store the credentials, storage and timezone.
3. **Posts** — create the text you want to publish.
4. **Schedules** — attach a publish date.
5. **Publishing** — run the poster routine to send everything that is due.

Each step is available either through the CLI or the [REST API](/guide/rest-api).

## Data layout

The tool is local-first. Data lives in a single store directory: `data/` by
default, or wherever the `SOCIAL_STORE_PATH` environment variable points. The
data files are named after the `fileName` stored in the configuration (falling
back to `production`):

| File | Contents |
| --- | --- |
| `global.json` | Configuration (credentials, storage, timezone). |
| `posts-<fileName>.csv` | Created posts (`text,id`). |
| `scheduler-<fileName>.csv` | Schedules (`postId,publishDate,scheduleId,published`). |

The same store is shared by the CLI, the REST API and the desktop app. See
[Configuration](/guide/configuration#where-data-lives) for how to change it.

## Extending it

New behavior belongs in `application/` behind a port, with concrete adapters
wired at the edges. For example, adding LinkedIn means a new
`SocialThirdParty` implementation rather than changes to the publishing use
case. See the repository's `AGENTS.md` for the full conventions.
