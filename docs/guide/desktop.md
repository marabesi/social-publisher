# Desktop app

Social Publisher ships with a desktop app so you can manage your configuration,
posts and schedules without the terminal.

## Install and start it

1. Download the installer for your operating system (`.dmg`, `.msi` or `.deb`)
   from the [latest release](https://github.com/marabesi/social-publisher/releases/latest).
2. Install and open it — no terminal required.

If you downloaded `social.jar` instead, launch the desktop app with:

```sh
java -jar social.jar desktop run
```

A native window opens. Your data is stored in the
[store directory](/guide/configuration#where-data-lives) (`data/` by default, or
wherever `SOCIAL_STORE_PATH` points), so the desktop app shares the same posts,
schedules and configuration as the terminal and the REST API.

## What you can do

- Store your JSON configuration and credentials.
- Create new posts with a rich text editor (bold, italic and underline).
- Browse every post in a table and **edit** or **remove** any of them.
- Schedule a post for a publish date and delete schedules.

Everything you do in the desktop app is written to the same local files the other
surfaces use, so nothing gets lost when you switch between them.
