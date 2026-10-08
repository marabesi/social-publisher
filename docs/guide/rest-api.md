# REST API

The `rest-api` module exposes the same application layer over HTTP with Spring
Boot. It reuses the `core` use cases and the shared `csv`/`social` outbound
adapters, so posts, schedules and configuration are the same data the CLI uses.

## Run it

```sh
./gradlew :rest-api:bootRun
```

The API listens on `http://localhost:8080` and stores its data in the
[store directory](/guide/configuration#where-data-lives) (`data/` by default, or
wherever `SOCIAL_STORE_PATH` points).

To build and run the executable jar:

```sh
./gradlew :rest-api:bootJar
java -jar rest-api/build/libs/rest-api-1.0.0.jar
```

## Endpoints

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/posts` | Create a post. Body: `{"text": "..."}`. |
| `GET` | `/api/posts` | List posts. |
| `POST` | `/api/schedules` | Schedule a post. Body: `{"postId": "1", "publishDate": "2099-01-02T09:00:00Z"}`. |
| `GET` | `/api/schedules` | List schedules. Query: `startDate`, `endDate`, `filter`, `orderBy`. |
| `DELETE` | `/api/schedules/{id}` | Delete a schedule. |
| `POST` | `/api/poster/run` | Publish every due schedule. |
| `GET` | `/api/configuration` | Read the stored configuration. |
| `POST` | `/api/configuration` | Store a configuration. |

`GET /api/schedules` accepts the same filter and order expressions as the CLI
(see [Scheduling posts](/guide/scheduling)):

```sh
curl --get localhost:8080/api/schedules --data-urlencode 'filter=post.text=release notes'
curl --get localhost:8080/api/schedules --data-urlencode 'orderBy=publish_date=desc'
```

## Examples

```sh
curl -X POST localhost:8080/api/posts \
  -H 'Content-Type: application/json' \
  -d '{"text":"hello from the REST API"}'

curl -X POST localhost:8080/api/schedules \
  -H 'Content-Type: application/json' \
  -d '{"postId":"1","publishDate":"2099-01-02T09:00:00Z"}'

curl localhost:8080/api/schedules
```

## Errors

| Status | When |
| --- | --- |
| `400` | An invalid query value or an unusable request body. |
| `404` | Missing configuration, or deleting an unknown schedule. |
| `502` | The social network rejected the publish request. |