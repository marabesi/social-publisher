# social-publisher

[![Coverage Status](https://coveralls.io/repos/github/marabesi/social-publisher/badge.svg?branch=main)](https://coveralls.io/github/marabesi/social-publisher?branch=main)

Social publisher allows you to schedule and publish posts into social
media. At the moment, the current medias are supported:

- Twitter
- Linkedin

# Refs

- https://kotlinlang.org/docs/command-line.html#compile-a-library
- https://picocli.info/

# Running standalone

kotlin -classpath "/home/marabesi/Downloads/picocli-4.6.2.jar:build/libs/social-1.0-SNAPSHOT.jar" MainKt post -l

# Documentation

## Architectural decisions

- [Hex architecture - definition](https://marabesi.com/architecture/2022/04/13/hexagonal-architecture)
- [Hex architecture - stackoverflow thread](https://stackoverflow.com/a/14659492/2258921)