# Social publisher - schedule posts on social network for developers

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/c7b738b1eb434894a1927f5a6aba588c)](https://www.codacy.com/gh/marabesi/social-publisher/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=marabesi/social-publisher&amp;utm_campaign=Badge_Grade) [![Coverage Status](https://coveralls.io/repos/github/marabesi/social-publisher/badge.svg?branch=main)](https://coveralls.io/github/marabesi/social-publisher?branch=main)

Social publisher allows you to schedule and publish posts into social
media. At the moment, the current medias are supported:

- Twitter

It ships as a monorepo with four Gradle modules — `core` (domain and shared
adapters), `cli` (picocli command line), `rest-api` (Spring Boot REST API) and
`desktop` (Compose desktop UI) — that share the same use cases and data.

# Planned to be supported

- Linkedin

# Documentation

The user guide is built with [VitePress](https://vitepress.dev/) and lives under
[`docs/`](docs). To run it locally:

```sh
cd docs
npm install
npm run docs:dev
```

Other available scripts are `npm run docs:build` and `npm run docs:preview`.

# REST API

Run the Spring Boot API with:

```sh
./gradlew :rest-api:bootRun
```

It listens on `http://localhost:8080`. See the
[REST API guide](docs/guide/rest-api.md) for the endpoints and examples.

# Desktop app

Run the Compose desktop UI with:

```sh
./gradlew :desktop:run
```

See the [Desktop guide](docs/guide/desktop.md) for what it can do and how to
package a distributable app.

# Refs

- https://kotlinlang.org/docs/command-line.html#compile-a-library
- https://picocli.info
- https://cucumber.io/docs/gherkin/reference

## Running

### Single jar

`./distribute.sh` produces a self-contained `social.jar` with everything on
board. Run the whole tool with just Java:

```sh
java -jar social.jar --help
java -jar social.jar post -c "hello"
java -jar social.jar rest serve          # start the REST API on :8080
java -jar social.jar desktop run         # launch the Compose desktop app
```

### Docker

```sh
./distribute.sh

docker build --platform=linux/amd64 . -t social

docker run --platform=linux/amd64 --rm social
```

For persistent configuration run:

```sh
docker run --platform=linux/amd64 -v $(pwd)/data:/data --rm social 
```

### Running standalone

```sh
./distribute.sh
./social/bin/social post -l
```

## Usage documentation

### Data location

Posts, schedules and configuration are stored as files in one directory. By
default the directory is `data/` (relative to where you run the tool), but you
can point it anywhere with the `SOCIAL_STORE_PATH` environment variable:

```sh
export SOCIAL_STORE_PATH=/my/path
social post -c "hello"
```

The directory is created on first use and holds `global.json` plus the data
files. The data files are named after the `fileName` stored in the
configuration (e.g. `posts-e2e-file.csv`, `scheduler-e2e-file.csv` when
`"fileName":"e2e-file"`); without a `fileName` the `production` suffix is used.
It applies to the CLI, the REST API and the desktop app.

### Twitter credentials

- it is a must to have x.com credentials

### Creating configuration

```sh
social configuration -l
social configuration -c '{"fileName":"prod-config","storage":"csv","timezone":"UTC","twitter": { "consumerKey": "", "consumerSecret": "", "accessToken": "", "accessTokenSecret": "" }}'
```

### Creating posts

```sh
social post -l
social post -c "this is my text"
```

### Deleting posts

Not yet supported

### Updating posts

Not yet supported

### Scheduling a post

```sh
social scheduler create -p "1" -d "2026-10-02T09:00:00Z" -s "TWITTER"

social scheduler list 
social scheduler list --start-date "2026-10-02T09:00:00Z"
social scheduler list --end-date "2026-10-02T09:00:00Z"
social scheduler list --start-date "2026-10-02T09:00:00Z" --end-date "2026-10-02T09:00:00Z"

social scheduler delete -id "1"
```

### Send the posts

```sh
social poster -r
```

## Contributing to the development

### Architectural decisions

- [Hex architecture - definition](https://marabesi.com/architecture/2022/04/13/hexagonal-architecture)
- [Hex architecture - stackoverflow thread](https://stackoverflow.com/a/14659492/2258921)

### Local setup
Running cucumber without interacting with twitter:

```
CUCUMBER_FILTER_TAGS="not @interactsWithTwitter" ./gradlew cucumber
```

### Integrations

### Twitter

- https://developer.twitter.com/en/docs/authentication/oauth-1-0a
  - https://developer.twitter.com/en/docs/authentication/oauth-1-0a/authorizing-a-request
  - https://developer.twitter.com/en/docs/authentication/api-reference/request_token
  - https://developer.twitter.com/en/docs/authentication/api-reference/authorize
  - https://github.com/twitterdev/Twitter-API-v2-sample-code/blob/main/Manage-Tweets/create_tweet.js
- https://gist.github.com/robotdan/33f5834399b6b30fea2ae59e87823e1d
- https://developer.twitter.com/en/docs/twitter-api/v1/tweets/post-and-engage/api-reference/post-statuses-update
- https://docs.spring.io/spring-social-twitter/docs/1.1.0.RELEASE/reference/htmlsingle
- https://github.com/spring-attic/spring-social-twitter

### Publishing to central

- [Publish to Maven Central using Gradle](https://h4pehl.medium.com/publish-your-gradle-artifacts-to-maven-central-f74a0af085b1)

### Junit + kotlin

- https://www.baeldung.com/kotlin/assertfailswith

### Docker

- https://www.baeldung.com/java-dockerize-app
