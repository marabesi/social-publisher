# Publishing posts

The `poster` command walks every schedule that has not been published yet and,
for each one whose publish date is in the past, sends it to the configured
social network.

```sh
social poster -r
```

| Option | Description |
| --- | --- |
| `-r` | Run the routine that publishes all due posts. |

## What happens

For each unpublished schedule the CLI compares the publish date with the current
time:

- **Date in the future** → nothing is sent yet:

  ```text
  Waiting for the date to come to publish post 1 (scheduled for 02 Oct 2026 09:00:00)
  ```

- **Date reached** → the post is sent and marked as published:

  ```text
  Post 1 sent to twitter
  ```

When there is nothing left to send:

```text
There are no posts to be posted
```

## Run on a schedule

`poster -r` is a one-shot command, so it is meant to be triggered repeatedly by
your operating system. Two common options:

::: code-group

```sh [cron]
# Every 5 minutes
*/5 * * * * cd /path/to/social-publisher && ./social/bin/social poster -r
```

```sh [Docker]
docker run --rm -v "$(pwd)/data:/data" social poster -r
```

:::

::: tip
The routine reads its credentials from `global.json` at publish time, so the
store directory must be reachable from the working directory (default `data/`,
or override with `SOCIAL_STORE_PATH`). Set it explicitly in cron — `cd` does not
happen automatically.
:::

## Troubleshooting

| Output | Cause |
| --- | --- |
| `Missing required configuration: twitter` | No `twitter` object in the configuration. |
| `Missing required configuration: consumer key` | `twitter.consumerKey` is empty. |
| `Missing required configuration: consumer secret` | `twitter.consumerSecret` is empty. |
| `Missing required configuration: access token` | `twitter.accessToken` is empty. |
| `Missing required configuration: token secret` | `twitter.accessTokenSecret` is empty. |

If the network rejects the request, the underlying error body from the X API is
surfaced to help you debug permissions or revoked tokens.
