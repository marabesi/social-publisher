# Installation

Social Publisher ships as a runnable Java distribution. You can either build it
from source on your machine or run the prebuilt distribution inside Docker.

## Prerequisites

- **Java 25** (or newer) on your `PATH`. Verify with:

  ```sh
  java -version
  ```

- **Git** to clone the repository.
- **Docker** (optional) if you prefer to run the CLI in a container.

You do **not** need to install Gradle — the repository ships with the Gradle
wrapper.

## Build from source

Clone the repository and build the distribution:

```sh
git clone https://github.com/marabesi/social-publisher.git
cd social-publisher
./distribute.sh
```

`distribute.sh` runs `./gradlew distZip`, unpacks the archive and leaves a
ready-to-run `social/` directory in the project root.

Run the CLI through the generated launcher:

```sh
./social/bin/social --help
```

::: tip Make it global
Add the launcher to your `PATH` to run `social` from anywhere:

```sh
export PATH="$PATH:$(pwd)/social/bin"
social --help
```

On Windows, use `social\bin\social.bat` instead.
:::

::: details Alternative: Gradle installDist
If you only need the unpacked layout, `./gradlew installDist` produces the same
launcher at `build/install/social/bin/social`.
:::

## Run with Docker

Build the distribution first, then the image:

```sh
./distribute.sh
docker build --platform=linux/amd64 . -t social
docker run --rm social --help
```

The container working directory is `/`, so the CLI reads and writes its data in
`/data`. Mount a host folder there to keep your posts and configuration between
runs:

```sh
docker run --rm -v "$(pwd)/data:/data" social post -l
```

`run.sh` wraps the two commands above and starts the container with the data
volume mounted.

## Verify the installation

Regardless of how you installed it, `--help` should list the available
subcommands:

```text
Usage: social [-hV] [COMMAND]
post to any social media
  post
  scheduler
  poster
  configuration
```

You are ready to [configure the tool](/guide/configuration).
