# Managing posts

Posts are the text you want to publish. Each post gets an incrementing numeric
id and is appended to `posts-<fileName>.csv` in the [store directory](/guide/configuration#where-data-lives).

## Create a post

```sh
social post -c "Shipping the docs for social-publisher today."
```

The CLI responds with:

```text
Post has been created
```

Empty or blank text is rejected with `Missing required fields`.

## List posts

```sh
social post -l
```

Output is one line per post, prefixed with its id, followed by the character
count and the limit:

```text
1. Shipping the docs for social-publisher today. (45/280)
```

Text longer than 50 characters is truncated in the listing with a trailing
`...`; the full text is still published. The number in parentheses is always the
full length of the post and the platform limit (280), so you can see at a glance
whether a post still fits. When there are no posts you get `No post found`.

## Search posts

`post -l` accepts three filters that also work together in the desktop app and
the REST API:

| Option | Description |
| --- | --- |
| `--search <text>` | Fuzzy, case-insensitive text search across the post text. Multiple words must all appear (as a subsequence) in the post. |
| `--ids <ids>` | Only posts whose id is in the comma separated list, e.g. `1,3`. |
| `--social-media <network>` | Only posts scheduled for the given network (`TWITTER` or `LINKEDIN`). |

```sh
social post -l --search "release notes"
social post -l --ids "1,3"
social post -l --social-media LINKEDIN
social post -l --search "notes" --ids "1,2,3" --social-media TWITTER
```

When nothing matches you get `No post found`.

## Next step

A post on its own is not sent anywhere. Attach a publish date with the
[scheduler](/guide/scheduling).
