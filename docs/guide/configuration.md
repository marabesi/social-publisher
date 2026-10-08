# Configuration

Before the CLI can publish anything, it needs to know **which social network to
use** and **how to authenticate**. Configuration is a single JSON document that
the `configuration` command validates and stores on disk.

## Where data lives

Posts, schedules and configuration are stored as files in one directory. By
default that directory is `data/` relative to where you run the tool, but you
can point it anywhere with the `SOCIAL_STORE_PATH` environment variable:

```sh
export SOCIAL_STORE_PATH=/my/path
social configuration -c '{"storage":"csv","timezone":"UTC"}'
social post -c "hello"
```

The directory is created on first use and holds `global.json` plus the data
files. The data files are named after the `fileName` set in the configuration:

- `posts-<fileName>.csv`
- `scheduler-<fileName>.csv`

So a configuration with `"fileName": "e2e-file"` produces
`posts-e2e-file.csv` and `scheduler-e2e-file.csv` in the store directory. When
no `fileName` is set (or no configuration is stored yet), the suffix `production`
is used. The same environment variable is honoured by the CLI, the REST API and
the desktop app.

## What gets stored

Everything is written to `global.json` in the store directory, relative to the
directory you run the CLI from. The document accepts the following keys:

| Key | Required | Default | Description |
| --- | --- | --- | --- |
| `twitter` | to publish | — | OAuth 1.0a credentials for X (Twitter). |
| `twitter.consumerKey` | yes | — | API key of your X app. |
| `twitter.consumerSecret` | yes | — | API key secret of your X app. |
| `twitter.accessToken` | yes | — | Access token for your X account. |
| `twitter.accessTokenSecret` | yes | — | Access token secret for your X account. |
| `storage` | no | `csv` | Storage adapter. Only `csv` is currently wired. |
| `timezone` | no | `UTC` | Timezone reported when a post is scheduled. |
| `fileName` | no | `production` | Suffix for the data files: `posts-<fileName>.csv` and `scheduler-<fileName>.csv`. |

Only these keys are accepted. Any other key makes the command fail with
`The give key <name> is not supported`.

::: warning Credentials are stored in plain text
`global.json` in the store directory contains your X credentials unencrypted.
Keep it out of version control (the repository already ignores `data/`) and
restrict access to the machine that runs the tool.
:::

## Get X (Twitter) credentials

Social Publisher authenticates with OAuth 1.0a using credentials created in the
[X developer portal](https://developer.x.com/):

1. Sign in and open the **Developer Portal**.
2. Create a **Project** and an **App** inside it.
3. In the app's **Keys and tokens** tab, generate:
   - **API Key** and **API Key Secret** → `consumerKey` and `consumerSecret`.
   - **Access Token** and **Access Token Secret** → `accessToken` and
     `accessTokenSecret`.
4. Make sure the app has **Read and Write** permissions, otherwise the publish
   step will fail.

## Create the configuration

Pass the JSON document to `configuration -c`:

```sh
social configuration -c '{
  "fileName": "prod-config",
  "storage": "csv",
  "timezone": "UTC",
  "twitter": {
    "consumerKey": "your-api-key",
    "consumerSecret": "your-api-key-secret",
    "accessToken": "your-access-token",
    "accessTokenSecret": "your-access-token-secret"
  }
}'
```

On success the CLI prints:

```text
Configuration has been stored
```

Running the command again overwrites `global.json`, so it is also the way
to rotate credentials or change the timezone or file suffix.

## Inspect the stored configuration

```sh
social configuration -l
```

This prints the stored JSON, or `There is no configuration stored` when nothing
has been saved yet.

## Troubleshooting

| Output | Cause |
| --- | --- |
| `Missing required fields` | No `-c` value and no `-l` flag were passed. |
| `The give key <name> is not supported` | The JSON contains an unknown key. |
| `There is no configuration stored` | `configuration -l` ran before a configuration was created. |
| `Missing required configuration: <name>` | A Twitter field was empty at publish time. |

With a valid configuration in place you can start
[managing posts](/guide/posts).
