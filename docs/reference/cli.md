# CLI reference

All commands accept `-h` / `--help` and `-V` / `--version`.

```text
Usage: social [-hV] [COMMAND]
post to any social media
Commands:
  post           Create and list posts
  scheduler      Create, list and delete schedules
  poster         Publish due schedules
  configuration  Store and inspect configuration
```

## `social configuration`

Store or inspect the JSON configuration.

| Option | Description |
| --- | --- |
| `-c <json>` | Store the given JSON configuration. |
| `-l` | List the stored configuration. |

See [Configuration](/guide/configuration) for the document shape.

## `social post`

Create or list posts.

| Option | Description |
| --- | --- |
| `-c <text>` | Create a post with the given text. |
| `-l` | List created posts. |

## `social scheduler create`

Schedule a post for a publish date.

| Option | Description |
| --- | --- |
| `-p <id>` | Id of the post to schedule. |
| `-d <instant>` | Publish date as an ISO-8601 instant, e.g. `2026-10-02T09:00:00Z`. |
| `-s <network>` | Target network. Defaults to `TWITTER`. |

## `social scheduler list`

List schedules, optionally filtered.

| Option | Description |
| --- | --- |
| `--start-date <instant>` | Only schedules on or after this instant. |
| `--end-date <instant>` | Only schedules on or before this instant. |
| `--group-by <criterion>` | Group the output. The only accepted value is `post`. |

## `social scheduler delete`

Delete a schedule by its listed id.

| Option | Description |
| --- | --- |
| `-id <id>` | Id of the schedule to remove. |

## `social poster`

Publish due schedules.

| Option | Description |
| --- | --- |
| `-r` | Run the publish routine. |

## Global settings

| Setting | Default | Description |
| --- | --- | --- |
| `twitter.api.baseUrl` (system property) | `https://api.twitter.com` | Base URL for the X API. |
| `TWITTER_API_BASE_URL` (environment variable) | — | Fallback base URL for the X API. |

These are mainly useful for pointing the CLI at a stub during tests.
