# Architecture

Social Publisher is a Kotlin/JVM CLI built around a hexagonal (ports and
adapters) architecture. The core use cases are plain Kotlin and never import a
framework or touch I/O directly; everything external is reached through a port.

## High-level view

```text
                 +---------------------------+
                 |        social CLI         |
                 |     picocli + Guice       |
                 +-------------+-------------+
                               |
                     inbound CLI adapters
                               |
                 +-------------v-------------+
                 |      application core     |
                 |    use cases + ports      |
                 +--+---------+-----------+--+
                    |         |           |
        CSV persistence|    CLI output   | social adapter
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
| Core | `src/main/kotlin/application` | Entities, use cases and ports. No framework or I/O imports. |
| Inbound adapters | `src/main/kotlin/adapters/inbound/cli` | picocli commands that drive the use cases. |
| Outbound adapters | `src/main/kotlin/adapters/outbound` | Port implementations: CSV persistence, Twitter, CLI output, in-memory (tests). |

Persistence ports live under `application/persistence`, output under
`application/Output.kt`, and social network integrations under
`application/socialnetwork`.

## The flow

1. **Credentials** — create an X developer app and collect OAuth 1.0a keys.
2. **Configuration** — store the credentials, storage and timezone with
   `social configuration -c`.
3. **Posts** — create the text you want to publish with `social post -c`.
4. **Schedules** — attach a publish date with `social scheduler create`.
5. **Publishing** — run `social poster -r` to send everything that is due.

## Data layout

The CLI is local-first. When run from a directory containing `data/`, it uses:

| File | Contents |
| --- | --- |
| `data/global.json` | Configuration (credentials, storage, timezone). |
| `data/posts-production.csv` | Created posts (`text,id`). |
| `data/scheduler-production.csv` | Schedules (`postId,publishDate,scheduleId,published`). |

Because paths are relative, the working directory you invoke the CLI from
determines which data set is used.

## Extending it

New behavior belongs in `application/` behind a port, with concrete adapters
wired at the edges in `adapters/`. For example, adding LinkedIn means a new
`SocialThirdParty` implementation rather than changes to the publishing use
case. See the repository's `AGENTS.md` for the full conventions.
