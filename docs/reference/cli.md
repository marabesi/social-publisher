# CLI reference

All commands accept `-h` / `--help` and `-V` / `--version`.

::: tip Running the commands
The examples below use the `social` launcher. If you run the single jar instead,
replace `social` with `java -jar social.jar` — for example
`java -jar social.jar post -l`. See [Installation](/guide/installation) for how
to download Social Publisher and add the `social` alias.
:::

```text
Usage: social [-hV] [COMMAND]
post to any social media
Commands:
  post           Create and list posts
  scheduler      Create, list and delete schedules
  poster         Publish due schedules
  configuration  Store and inspect configuration
  linkedin       Connect a LinkedIn account
  desktop        Launch the Compose desktop app
  rest           Serve the REST API
```

## `social desktop run`

Launch the Compose desktop UI (see [Desktop app](/guide/desktop)).

```sh
social desktop run
```

## `social rest serve`

Start the Spring Boot REST API (see [REST API](/guide/rest-api)).

```sh
social rest serve
social rest serve --server.port=9090
```

Any additional arguments are forwarded to Spring Boot.

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
| `--search <text>` | Only posts matching the case-insensitive text search (matches word starts). |
| `--ids <ids>` | Only posts whose id is in the comma separated list, e.g. `1,3`. |
| `--social-media <network>` | Only posts scheduled for the given network (`TWITTER` or `LINKEDIN`). |

The `-l` filters can be combined. See [Managing posts](/guide/posts#search-posts).

## `social scheduler create`

Schedule a post for a publish date.

| Option | Description |
| --- | --- |
| `-p <id>` | Id of the post to schedule. |
| `-d <instant>` | Publish date as an ISO-8601 instant, e.g. `2026-10-02T09:00:00Z`. |
| `-s <network>` | Target network. Accepts `TWITTER` or `LINKEDIN`. Defaults to `TWITTER`. |

## `social scheduler list`

List schedules, optionally filtered.

| Option | Description |
| --- | --- |
| `--start-date <instant>` | Only schedules on or after this instant. |
| `--end-date <instant>` | Only schedules on or before this instant. |
| `-f`, `--filter <criteria>` | Filter by any property, e.g. `post.text=draft` or `day=10&month=07&year=2022`. |
| `--search <text>` | Only schedules whose post matches the case-insensitive text search (matches word starts). |
| `--ids <ids>` | Only schedules for posts whose id is in the comma separated list, e.g. `1,3`. |
| `--social-media <network>` | Only schedules targeting the given network (`TWITTER` or `LINKEDIN`). |
| `--group-by <criterion>` | Group the output. The only accepted value is `post`. |
| `-o`, `--order-by <criterion>` | Order the output. Accepts `publish_date=asc` or `publish_date=desc`. |

## `social scheduler delete`

Delete a schedule by its listed id.

| Option | Description |
| --- | --- |
| `-id <id>` | Id of the schedule to remove. |

## `social linkedin connect`

Print the LinkedIn authorization URL to obtain an access token. Requires
`linkedin.clientId` and `linkedin.redirectUri` to be stored. See
[Connect LinkedIn](/guide/configuration#connect-linkedin).

| Option | Description |
| --- | --- |
| `--state <value>` | Opaque value echoed back by LinkedIn (defaults to `social-publisher`). |

## `social linkedin token`

Exchange the authorization code for an access token and store it together with
the member URN.

| Option | Description |
| --- | --- |
| `-c`, `--code <code>` | Authorization code from the redirect URL (required). |

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
| `linkedin.api.baseUrl` (system property) | `https://api.linkedin.com` | Base URL for the LinkedIn API. |
| `LINKEDIN_API_BASE_URL` (environment variable) | — | Fallback base URL for the LinkedIn API. |
| `linkedin.oauth.baseUrl` (system property) | `https://www.linkedin.com` | Base URL for the LinkedIn OAuth token exchange. |
| `LINKEDIN_OAUTH_BASE_URL` (environment variable) | — | Fallback base URL for the LinkedIn OAuth token exchange. |

These are mainly useful for pointing the CLI at a stub during tests.
