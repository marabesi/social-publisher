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

### Filter by any property

Use `-f` / `--filter` to keep only schedules whose property matches a value.
Expressions are `property=value` pairs joined with `&`, and any property of the
scheduled item can be used, not just the date:

```sh
social scheduler list --filter "day=10&month=07&year=2022"
social scheduler list --filter "post.text=release notes"
social scheduler list --filter "published=false"
```

Numbers are compared numerically, so `month=07` and `month=7` are equivalent. An
unknown property or a malformed expression returns
`Value for filter is not valid`; when nothing matches you get
`No posts scheduled`.

| Property | Meaning |
| --- | --- |
| `id` | Schedule id. |
| `published` | Whether the schedule has already been sent (`true` / `false`). |
| `publishDate` | Full instant, e.g. `2022-07-10T09:00:00Z`. |
| `year`, `month`, `day` | Parts of the publish date. |
| `post.id` | Id of the scheduled post. |
| `post.text` | Text of the scheduled post. |
| `post.socialMediaId` | Social network id, set once published. |

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

### Order the output

Use `-o` / `--order-by` to control the order of the listed schedules. The only
orderable field is `publish_date`, with an `asc` or `desc` direction:

```sh
social scheduler list --order-by "publish_date=asc"
social scheduler list --order-by "publish_date=desc"
```

Ordering and filtering are independent and can be combined, including the `-f`
filter from the issue:

```sh
social scheduler list --filter "day=10&month=07&year=2022" --order-by "publish_date=asc"
social scheduler list --start-date "2026-10-01T00:00:00Z" --order-by "publish_date=desc"
```

An unknown field or direction returns `Value for order-by is not valid`.

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
