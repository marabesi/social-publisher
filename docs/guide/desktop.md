# Desktop app

The `desktop` module is a Compose Multiplatform desktop UI over the same `core`.
It lets you manage configuration, posts and schedules without the terminal.

## Run it

```sh
./gradlew :desktop:run
```

Or launch it from the same CLI jar as everything else:

```sh
./social/bin/social desktop run     # or: java -jar social.jar desktop run
```

A native window opens; data is stored in the [store directory](/guide/configuration#where-data-lives)
(`data/` by default, or wherever `SOCIAL_STORE_PATH` points), so the app shares
the same posts, schedules and configuration as the CLI and the REST API.

To package a distributable app for the current OS:

```sh
./gradlew :desktop:packageDistributionForCurrentOS
```

The native image lands in `desktop/build/compose/binaries/`.

## What it can do

- Store a JSON configuration.
- Create posts and refresh the post list.
- Schedule a post for a publish date and delete schedules.