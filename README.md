# Social publisher - schedule posts on social network for developers

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/c7b738b1eb434894a1927f5a6aba588c)](https://www.codacy.com/gh/marabesi/social-publisher/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=marabesi/social-publisher&amp;utm_campaign=Badge_Grade) [![Coverage Status](https://coveralls.io/repos/github/marabesi/social-publisher/badge.svg?branch=main)](https://coveralls.io/github/marabesi/social-publisher?branch=main)

Social publisher allows you to schedule and publish posts into social
media. At the moment, the current medias are supported:

- Twitter

# Planned to be supported

- Linkedin

# Refs

- https://kotlinlang.org/docs/command-line.html#compile-a-library
- https://picocli.info
- https://cucumber.io/docs/gherkin/reference

## Running

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
kotlin -classpath "/home/marabesi/Downloads/picocli-4.6.2.jar:build/libs/social-1.0-SNAPSHOT.jar" MainKt post -l
````

# Documentation

## Architectural decisions

- [Hex architecture - definition](https://marabesi.com/architecture/2022/04/13/hexagonal-architecture)
- [Hex architecture - stackoverflow thread](https://stackoverflow.com/a/14659492/2258921)

## Local setup
Running cucumber without interacting with twitter:

```
CUCUMBER_FILTER_TAGS="not @interactsWithTwitter" ./gradlew cucumber
```

## Integrations

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

## Junit + kotlin

- https://www.baeldung.com/kotlin/assertfailswith

## Docker

- https://www.baeldung.com/java-dockerize-app
