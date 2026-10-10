# Scheduling posts

Scheduling links a post to a publish date. Schedules are stored in
`scheduler-<fileName>.csv` in the [store directory](/guide/configuration#where-data-lives).

## Create a schedule

```sh
social scheduler create -p "1" -d "2026-10-02T09:00:00Z" -s "TWITTER"
social scheduler create -p "1" -d "2026-10-02T09:00:00Z" -s "LINKEDIN"
```

| Option | Description |
| --- | --- |
| `-p` | Id of the post to schedule (from `social post -l`). |
| `-d` | Publish date as an ISO-8601 instant (e.g. `2026-10-02T09:00:00Z`) or as a local date-time interpreted in the [configured timezone](/guide/configuration), e.g. `2026-10-02T09:00:00`. |
| `-s` | Social network. Accepts `TWITTER` or `LINKEDIN`. Defaults to `TWITTER`. |

On success:

```text
Post has been scheduled using UTC timezone
```

The date is stored as a UTC instant. A value with an explicit offset (such as
`Z` or `+02:00`) is kept as-is; a local date-time without an offset is
interpreted using the [configured timezone](/guide/configuration) and converted
to UTC before being stored. The timezone shown in the message comes from your
[configuration](/guide/configuration).

For example, with `"timezone": "Europe/Madrid"` in the configuration, the local
date time `2026-10-02T09:00:00` is stored and listed as `2026-10-02T07:00:00Z`
because Madrid is two hours ahead of UTC on that date.

Common failures:

| Output | Cause |
| --- | --- |
| `Missing required fields` | `-d` was omitted. |
| `Couldn't find post with id <id>` | No post exists with that id. |
| `Invalid date time to schedule post` | The date is not a valid ISO-8601 date time. |
| `Post is already scheduled for <date>` | The same post is already scheduled at that exact time for the same social network. |

## Pick a random post for a day

Instead of choosing the post and the exact time yourself, you can let Social
Publisher do it:

```sh
social scheduler random -d "2026-10-07" -s "TWITTER"
```

```text
Post 3 has been randomly scheduled for 2026-10-07T14:23:11Z using UTC timezone
```

`-d` is the day to schedule within. It accepts a plain `yyyy-MM-dd` date or a
full ISO-8601 date time; the day is interpreted in the
[configured timezone](/guide/configuration). The publish time is picked at
random inside that day, but never earlier than 30 minutes from now, so a slot
requested for today always stays in the future. If the day is already in the
past, today is used instead, so a randomly created post is never scheduled in
the past.

The random pick uses the list of already scheduled posts: a post that is already
scheduled for the same social network in the same week is skipped, so the same
content does not repeat within a week.

| Output | Cause |
| --- | --- |
| `Missing required fields` | `-d` was omitted. |
| `Invalid date time to schedule post` | The day is not a valid date. |
| `No available time to schedule a post on <day>` | The day has no slot left (for example, today with less than 30 minutes remaining). |
| `No post available to schedule on the week of <day>` | Every post is already scheduled in that week. |

## List schedules

```sh
social scheduler list
```

```text
1. Post with id 1 will be published on 2026-10-02T09:00:00Z (Twitter)
```

The target social network (the `-s` value) is shown in parentheses. The desktop
app and the REST API report the same value for each schedule.

### Search schedules

The same search used for [posts](/guide/posts#search-posts) also works on
schedules, and can be combined with the filters below:

| Option | Description |
| --- | --- |
| `--search <text>` | Case-insensitive search over the scheduled post text. Every whitespace-separated term must match the beginning of a word in the post text, so `aws` matches `AWS` but not `laws`. |
| `--ids <ids>` | Only schedules for posts whose id is in the comma separated list, e.g. `1,3`. |
| `--social-media <network>` | Only schedules targeting the given network (`TWITTER` or `LINKEDIN`). |

```sh
social scheduler list --search "release notes"
social scheduler list --ids "1,3"
social scheduler list --social-media LINKEDIN
social scheduler list --search "notes" --ids "1,2,3" --social-media TWITTER
```

When nothing matches you get `No posts scheduled`.

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
| `socialMedia` | Target social network, e.g. `TWITTER` or `LINKEDIN`. |

### Filter by date range

```sh
social scheduler list --start-date "2026-10-01T00:00:00Z" --end-date "2026-12-31T23:59:59Z"
```

Both bounds accept ISO-8601 instants or a local date-time interpreted in the
[configured timezone](/guide/configuration). Invalid values are reported as
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
