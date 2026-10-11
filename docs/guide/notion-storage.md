# Notion storage

Social Publisher is local-first: posts, schedules and the configuration live in
plain files under the [store directory](/guide/configuration#where-data-lives)
(`data/` by default, or wherever `SOCIAL_STORE_PATH` points). With the `notion`
storage adapter you additionally keep copies of the data files in a Notion
workspace, so they show up next to your other notes there — useful as a remote
backup and to inspect the post queue from anywhere.

## How the integration works

The local files are always the source of truth. With `"storage": "notion"` the
tool writes changes to the store directory exactly as it does with `csv` and,
**on top of that**, uploads the data files to Notion every time the data
changes:

| You do this | Effect on the Notion copy |
| --- | --- |
| Create, edit or delete a post | `posts-<fileName>.csv` and `scheduler-<fileName>.csv` are re-uploaded |
| Create, delete or randomly schedule a post | the same two data files are re-uploaded |
| Publish due posts (`poster -r`) | schedules are marked as published and the data files are re-uploaded |

Reads never touch Notion: listing posts or schedules always comes from the local
files, so Notion is a remote copy for visibility and backup, not the source of
truth. You can keep using the CLI, the REST API or the desktop app exactly the
same way — they share the same store directory and the same Notion adapter, so
a change made on one surface is immediately visible on the others.

## The upload lifecycle

For each data file the tool runs the Notion
[file upload API](https://developers.notion.com/reference/file-upload) flow:

1. `POST /v1/file_uploads` creates a file upload object for
   `posts-<fileName>.csv` / `scheduler-<fileName>.csv`.
2. `POST /v1/file_uploads/{id}/send` sends the file contents to Notion.
3. The uploaded file is attached to the page configured in `notion.pageId` as a
   **file block**.

Every response received from the Notion API during this flow is also written to
the application log (`Notion API response (METHOD /path): ...`), so you can read
the full payload the server returned for each call — for example the file upload
object with its `id`:

```log
INFO: Notion API response (POST /v1/file_uploads): {"object":"file_upload","id":"...","status":"pending"}
```

## Replace, don't duplicate

Each data file is always attached under the same file name
(`posts-<fileName>.csv`, `scheduler-<fileName>.csv`). Before attaching, the tool
looks at the blocks already on the page:

- **No file block with that name** → a new file block is appended to the page.
- **A file block already exists** → it is **updated in place**, so the page
  shows the latest version of the file rather than a second copy.
- **Older duplicate blocks with the same name** (for example created by an
  earlier version of the tool) → they are removed, keeping exactly one block
  per data file.

Because the tool replaces instead of duplicating, the page never accumulates a
new copy of the data files on every change — a clean-up of duplicates left over
from before also happens automatically on the next upload.

## What you need

The Notion block lives in the configuration document (see
[Configuration](/guide/configuration#use-notion-as-storage)):

| Key | Purpose |
| --- | --- |
| `notion.apiKey` | The integration token (`ntn_...`). The integration must have access to the page. |
| `notion.pageId` | The page the file blocks are attached to. |

`pageId` matters because an uploaded file that is never attached to a page
expires after one hour. Attaching it to the page makes it a permanent part of
the workspace.

## What gets uploaded

Only the data files are uploaded:

- `posts-<fileName>.csv`
- `scheduler-<fileName>.csv`

The `fileName` comes from the configuration (default `production`), so the same
rule that names the local files also names the Notion attachments.

`global.json`, which holds the configuration and your credentials, is **never**
uploaded — it stays in the local store directory.

## Troubleshooting

| Symptom | Cause |
| --- | --- |
| `Could not upload to Notion` / upload errors | Notion's API returned an error, e.g. the integration token is invalid or the integration cannot access `notion.pageId`. |
| Files missing on the page after configuring | Uploads only run on a change (create/edit/delete post or schedule, or publishing). Store the configuration first, then make a change. |
| Multiple copies of a data file on the page | Old duplicates from a previous version. The next upload replaces one block and removes the rest, leaving a single copy. |
| Configuration visible nowhere in Notion | Expected: `global.json` is deliberately not uploaded. Only posts and schedules files are. |