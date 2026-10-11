# Google Drive storage

Social Publisher is local-first: posts, schedules and the configuration live in
plain files under the [store directory](/guide/configuration#where-data-lives)
(`data/` by default, or wherever `SOCIAL_STORE_PATH` points). With the
`google_drive` storage adapter you additionally keep copies of the data files in
Google Drive, so they show up in your Drive — useful as a remote backup and to
inspect the post queue from anywhere.

## How the integration works

The local files are always the source of truth. With `"storage": "google_drive"`
the tool writes changes to the store directory exactly as it does with `csv`
and, **on top of that**, uploads the data files to Google Drive every time the
data changes:

| You do this | Effect on the Google Drive copy |
| --- | --- |
| Create, edit or delete a post | `posts-<fileName>.csv` and `scheduler-<fileName>.csv` are re-uploaded |
| Create, delete or randomly schedule a post | the same two data files are re-uploaded |
| Publish due posts (`poster -r`) | schedules are marked as published and the data files are re-uploaded |

Reads never touch Google Drive: listing posts or schedules always comes from the
local files, so Google Drive is a remote copy for visibility and backup, not the
source of truth. You can keep using the CLI, the REST API or the desktop app
exactly the same way — they share the same store directory and the same Google
Drive adapter, so a change made on one surface is immediately visible on the
others.

## The upload lifecycle

For each data file the tool talks to the Google Drive REST API:

1. `GET /drive/v3/files` looks for a file with the same name (inside the
   configured folder, if any).
2. If no file exists, `POST /upload/drive/v3/files?uploadType=multipart` creates
   it with the file contents.
3. If a file already exists, `PATCH /upload/drive/v3/files/{fileId}?uploadType=media`
   replaces its contents in place.

Every response received from the Google Drive API during this flow is also
written to the application log
(`Google Drive API response (METHOD /path): ...`), so you can read the full
payload the server returned for each call — for example the created file with
its `id`:

```log
INFO: Google Drive API response (POST /upload/drive/v3/files): {"kind":"drive#file","id":"...","name":"posts-prod.csv"}
```

## Replace, don't duplicate

Each data file always keeps the same name (`posts-<fileName>.csv`,
`scheduler-<fileName>.csv`). Before uploading, the tool searches for an existing
file with that name:

- **No file with that name** → a new file is created.
- **A file already exists** → its media is **updated in place**, so Drive shows
  the latest version of the file rather than a second copy.

Because the tool replaces instead of duplicating, the folder never accumulates a
new copy of the data files on every change.

## What you need

The Google Drive block lives in the configuration document (see
[Configuration](/guide/configuration#use-google-drive-as-storage)):

| Key | Purpose |
| --- | --- |
| `googleDrive.accessToken` | An OAuth 2.0 access token that grants access to Drive (the `drive.file` scope is enough). |
| `googleDrive.folderId` | The folder the data files are uploaded to. Leave it blank to use My Drive root. |

You can obtain an access token from the
[OAuth 2.0 Playground](https://developers.google.com/oauthplayground/) or any
OAuth client for your own Google account, selecting the Drive scope. Access
tokens expire, so refresh or regenerate the token and store the configuration
again when uploads start returning `401`.

## What gets uploaded

Only the data files are uploaded:

- `posts-<fileName>.csv`
- `scheduler-<fileName>.csv`

The `fileName` comes from the configuration (default `production`), so the same
rule that names the local files also names the Google Drive files.

`global.json`, which holds the configuration and your credentials, is **never**
uploaded — it stays in the local store directory.

## Troubleshooting

| Symptom | Cause |
| --- | --- |
| `Could not upload to Google Drive` / upload errors | Google Drive API returned an error, e.g. the access token is invalid or expired, or it lacks the Drive scope. |
| Files missing in Drive after configuring | Uploads only run on a change (create/edit/delete post or schedule, or publishing). Store the configuration first, then make a change. |
| Multiple copies of a data file | The folder should hold one file per data name; the next upload replaces it. Check that `googleDrive.folderId` points at the same folder every time. |
| Configuration visible nowhere in Drive | Expected: `global.json` is deliberately not uploaded. Only posts and schedules files are. |
