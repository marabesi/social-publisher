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

## A tour of the app

Every screen below is rendered from the real app, so what you see is what you
get.

### Home

You land on a home page with buttons that jump straight to any section, without
opening the drawer.

![Home page of the desktop app](/screenshots/home.png)

The drawer in the top-left corner also navigates to every section, and the app
highlights the section you are in.

![Navigation drawer of the desktop app](/screenshots/navigation.png)

### Posts

Browse every post in a table and **edit** or **remove** any of them. The search
fields filter the list by text, by id or by scheduled social network.

![Posts page of the desktop app](/screenshots/posts.png)

### Compose

Create new posts with a plain text editor and a live character count. The same
editor opens pre-filled when you edit an existing post.

![Compose page of the desktop app](/screenshots/compose.png)

### Schedules

Pick a publish date and time with a calendar and clock picker when scheduling,
choose the target social network from a dropdown, and delete schedules. The page
also lists existing schedules and lets you **search**, **sort** and **filter by
status** them. Only upcoming schedules are shown by default; pick **Published**
in the status dropdown to list the schedules that have already been sent.

![Schedules page of the desktop app](/screenshots/schedules.png)

### Poster

Run the poster automatically every minute (or on a cadence you choose) and see
when it will run next. You can also trigger it manually with **Run now**.

![Poster page of the desktop app](/screenshots/poster.png)

### Configuration

Store your JSON configuration, including X (Twitter) and LinkedIn credentials.
From here you can also connect LinkedIn: generate the authorization URL, open it
in your browser and paste the returned code back to store the token. The
**Storage** field accepts `csv`, `notion` or `google_drive` — with `notion` or
`google_drive` the data files are also uploaded to your Notion page or Google
Drive folder on every change (see
[Notion storage](/guide/notion-storage) and
[Google Drive storage](/guide/google-drive-storage)).

![Configuration page of the desktop app](/screenshots/configuration.png)

Everything you do in the desktop app is written to the same local files the other
surfaces use, so nothing gets lost when you switch between them.