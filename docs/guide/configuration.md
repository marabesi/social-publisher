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
| `linkedin` | to publish | — | OAuth 2.0 credentials for LinkedIn. |
| `linkedin.clientId` | to connect | — | Client ID of your LinkedIn app. |
| `linkedin.clientSecret` | to connect | — | Client secret of your LinkedIn app. |
| `linkedin.redirectUri` | to connect | — | Redirect URL registered in your LinkedIn app. |
| `linkedin.accessToken` | yes | — | Access token for your LinkedIn account. |
| `linkedin.authorUrn` | yes | — | Author of the post, e.g. `urn:li:person:123`. |
| `storage` | no | `csv` | Storage adapter. Only `csv` is currently wired. |
| `timezone` | no | `UTC` | Timezone used to interpret local publish dates and reported when a post is scheduled. |
| `fileName` | no | `production` | Suffix for the data files: `posts-<fileName>.csv` and `scheduler-<fileName>.csv`. |

Only these keys are accepted. Any other key makes the command fail with
`The give key <name> is not supported`.

::: warning Credentials are stored in plain text
`global.json` in the store directory contains your credentials unencrypted.
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

## Connect LinkedIn

Social Publisher authenticates with LinkedIn through OAuth 2.0. You need a
LinkedIn app, and then a short helper flow turns an authorization code into the
stored access token.

1. Create an app in the [LinkedIn developer portal](https://www.linkedin.com/developers/)
   and associate it with a LinkedIn Page.
2. Under **Products**, request **Share on LinkedIn** (grants `w_member_social`)
   and **Sign In with LinkedIn using OpenID Connect** (grants `openid profile`).
3. Under **Auth**, copy the **Client ID** and **Client Secret** and add a
   **redirect URL** you control (a page that returns 404 is fine).

Store those three values, then let the CLI generate the authorization URL:

```sh
social configuration -c '{
  "storage": "csv",
  "linkedin": {
    "clientId": "your-client-id",
    "clientSecret": "your-client-secret",
    "redirectUri": "https://example.com/linkedin/callback"
  }
}'

social linkedin connect
```

`linkedin connect` prints the URL to open in your browser. Approve the consent
screen; LinkedIn redirects to your redirect URL with a `?code=...` parameter.
Copy that code and exchange it for the token:

```sh
social linkedin token -c "the-code-from-the-redirect"
```

```text
LinkedIn account connected
```

The command stores `linkedin.accessToken` and `linkedin.authorUrn` (derived from
`GET /v2/userinfo`) for you, so there is no manual `curl`. The access token lasts
about 60 days; run `linkedin connect` + `linkedin token` again when it expires.

::: tip Token lifetime
LinkedIn's self-serve apps do not get a refresh token, so reconnect before the
~60-day expiry to avoid a failed publish.
:::

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
  },
  "linkedin": {
    "clientId": "your-client-id",
    "clientSecret": "your-client-secret",
    "redirectUri": "https://example.com/linkedin/callback",
    "accessToken": "your-linkedin-access-token",
    "authorUrn": "urn:li:person:your-id"
  }
}'
```

`linkedin.clientId`, `linkedin.clientSecret` and `linkedin.redirectUri` are only
needed for the connect flow; `accessToken` and `authorUrn` are what publishing
uses.

Both credential blocks are optional; provide the ones for the networks you
schedule to.

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
| `Missing required configuration: twitter` | No `twitter` block was stored but a Twitter schedule is due. |
| `Missing required configuration: linkedin` | No `linkedin` block was stored but a LinkedIn schedule is due. |
| `Missing required configuration: <name>` | A Twitter or LinkedIn field was empty at publish time. |

With a valid configuration in place you can start
[managing posts](/guide/posts).
