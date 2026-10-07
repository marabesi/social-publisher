# Scheduling posts

Scheduling links a post to a publish date. Schedules are stored in
`data/scheduler-production.csv`.

## Create a schedule

```sh
social scheduler create -p "1" -d "2026-10-02T09:00:00Z" -s "TWITTER"
```

| Option | Description |
| --- | --- |
| `-p` | Id of the post to schedule (from `social post -l`). |
| `-d` | Publish date as an ISO-8601 instant, e.g. `2026-10-02T09:00:00Z`. |
| `-s` | Social network. Defaults to `TWITTER`. |

On success:

```text
Post has been scheduled using UTC timezone
```

The date must be a valid ISO-8601 instant in UTC (ending in `Z`). The timezone
shown in the message comes from your [configuration](/guide/configuration).

Common failures:

| Output | Cause |
| --- | --- |
| `Missing required fields` | `-d` was omitted. |
| `Couldn't find post with id <id>` | No post exists with that id. |
| `Invalid date time to schedule post` | The date is not a valid ISO-8601 instant. |
| `Post is already scheduled for <date>` | The same post is already scheduled at that exact time. |

## List schedules

```sh
social scheduler list
```

```text
1. Post with id 1 will be published on 2026-10-02T09:00:00Z
```

### Filter by date range

```sh
social scheduler list --start-date "2026-10-01T00:00:00Z" --end-date "2026-12-31T23:59:59Z"
```

Both bounds accept ISO-8601 instants. Invalid values are reported as
`Invalid start date` or `Invalid end date`. When nothing matches you get
`No posts scheduled`.

### Group by post

```sh
social scheduler list --group-by post
```

```text
1. Post with id 1 posted 3 time(s)
```

`post` is the only accepted value for `--group-by`; anything else returns
`Value for group-by is not valid`.

## Delete a schedule

```sh
social scheduler delete -id "1"
```

```text
Schedule 1 has been removed from post 1
```

The id is the position shown by `social scheduler list`.

## Next step

Run the [poster](/guide/publishing) to send every schedule whose publish date
has passed.
