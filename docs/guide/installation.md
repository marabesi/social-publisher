# Installation

Social Publisher comes ready to run. You download it and start it — there is
nothing to build and no server to set up.

## What you need

- A computer with **Java 25 or newer** installed.

  Check it with:

  ```sh
  java -version
  ```

  ::: tip No Java yet?
  Grab a free OpenJDK build from [Adoptium](https://adoptium.net/) for Windows,
  macOS or Linux. If you use the native desktop installer below, you can skip
  Java entirely.
  :::

## Download

1. Open the [latest release](https://github.com/marabesi/social-publisher/releases/latest).
2. Download the file that suits you:

   | File | Best for |
   | --- | --- |
   | **`social.jar`** | One file with everything: the desktop app, the command line and the REST API. |
   | **Desktop installer** (`.dmg`, `.msi` or `.deb`) | Installing Social Publisher like any other app, no terminal needed. |

3. Keep `social.jar` somewhere easy to find, for example your home folder.

## Start the desktop app

The desktop app is the friendliest way to use Social Publisher.

- **If you downloaded the installer:** open Social Publisher from your
  applications menu (or by double-clicking the installed icon) and you are done.
- **If you downloaded `social.jar`:** double-clicking a `.jar` does not always
  work, so run:

  ```sh
  java -jar social.jar desktop run
  ```

A window opens where you can manage your configuration, posts and schedules.
See the [Desktop app guide](/guide/desktop) for a tour.

## Start from the terminal

The same `social.jar` also gives you a command line:

```sh
java -jar social.jar --help
```

You should see the available commands:

```text
Usage: social [-hV] [COMMAND]
post to any social media
Commands:
  post           Create and list posts
  scheduler      Create, list and delete schedules
  poster         Publish due schedules
  configuration  Store and inspect configuration
  desktop        Launch the Compose desktop app
  rest           Serve the REST API
```

A quick tour:

```sh
java -jar social.jar configuration -l
java -jar social.jar post -c "hello world"
java -jar social.jar post -l
```

::: tip Type less
Create an alias so you can run `social` instead of `java -jar social.jar`:

```sh
alias social="java -jar /path/to/social.jar"
social --help
```

Add the line to your shell profile (for example `~/.zshrc` or `~/.bashrc`) to
make it permanent.
:::

## Where your data lives

Posts, schedules and the configuration are stored as plain files in one folder.
By default that folder is `data/` next to where you run the tool. To keep your
data somewhere else, set the `SOCIAL_STORE_PATH` environment variable:

```sh
export SOCIAL_STORE_PATH=/my/path
```

The same folder is shared by the desktop app, the terminal and the REST API, so
you can switch between them freely. See
[Configuration](/guide/configuration#where-data-lives) for the details.

## Next step

With Social Publisher installed, [configure](/guide/configuration) your X
(Twitter) credentials so it can publish your posts.
