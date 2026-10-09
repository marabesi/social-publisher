# REST API

The `rest-api` module exposes the same application layer over HTTP with Spring
Boot. It reuses the `core` use cases and the shared `csv`/`social` outbound
adapters, so posts, schedules and configuration are the same data the CLI uses.

## Start it

The REST API is part of the same download. Start it with:

```sh
java -jar social.jar rest serve
```

It listens on `http://localhost:8080` and stores its data in the
[store directory](/guide/configuration#where-data-lives) (`data/` by default, or
wherever `SOCIAL_STORE_PATH` points).

Any extra arguments are passed on to Spring Boot, so you can pick another port:

```sh
java -jar social.jar rest serve --server.port=9090
```

## API docs (Swagger UI)

The API ships with automatically generated OpenAPI 3 documentation. Start the
server (as above) and open the Swagger UI in your browser:

- **Swagger UI:** <http://localhost:8080/swagger-ui/index.html>

  A browsable page listing every method with its parameters, request bodies and
  responses. You can call the API directly from it (`Try it out`) and it shows
  the different surfaces still share the same data.

- **OpenAPI JSON:** <http://localhost:8080/v3/api-docs>
- **OpenAPI YAML:** <http://localhost:8080/v3/api-docs.yaml>

Both OpenAPI documents are generated at runtime from the registered
controllers, so they always match the running version. If you changed the port
with `--server.port`, use that port in the URLs above.

## Endpoints

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/` | Index of the available resources (the API "home"). |
| `POST` | `/api/posts` | Create a post. Body: `{"text": "..."}`. |
| `GET` | `/api/posts` | List posts. Each entry includes `characterCount` and `characterLimit`. Query: `search`, `ids`, `socialMedia`. |
| `POST` | `/api/schedules` | Schedule a post. Body: `{"postId": "1", "publishDate": "2099-01-02T09:00:00Z", "socialMedia": "TWITTER"}`. `socialMedia` accepts `TWITTER` or `LINKEDIN`. |
| `POST` | `/api/schedules/random` | Pick a random post and schedule it for a day. Body: `{"publishDate": "2099-01-02", "socialMedia": "TWITTER"}`. |
| `GET` | `/api/schedules` | List schedules (each entry includes `socialMedia`). Query: `startDate`, `endDate`, `filter`, `orderBy`, `search`, `ids`, `socialMedia`. |
| `DELETE` | `/api/schedules/{id}` | Delete a schedule. |
| `POST` | `/api/poster/run` | Publish every due schedule. |
| `GET` | `/api/configuration` | Read the stored configuration. |
| `POST` | `/api/configuration` | Store a configuration. |
| `GET` | `/api/linkedin/authorization-url` | Get the LinkedIn authorization URL. Optional query: `state`. |
| `POST` | `/api/linkedin/token` | Exchange a LinkedIn authorization code. Body: `{"code": "..."}`. |

`GET /` returns the index of resources:

```json
{
  "name": "Social Publisher API",
  "resources": {
    "posts": "/api/posts",
    "schedules": "/api/schedules",
    "poster": "/api/poster/run",
    "configuration": "/api/configuration"
  }
}
```

`GET /api/schedules` accepts the same filter and order expressions as the CLI
(see [Scheduling posts](/guide/scheduling)):

```sh
curl --get localhost:8080/api/schedules --data-urlencode 'filter=post.text=release notes'
curl --get localhost:8080/api/schedules --data-urlencode 'orderBy=publish_date=desc'
```

`GET /api/posts` accepts the same search filters as `social post -l` (see
[Search posts](/guide/posts#search-posts)):

```sh
curl --get localhost:8080/api/posts --data-urlencode 'search=release notes'
curl --get localhost:8080/api/posts --data-urlencode 'ids=1,3'
curl --get localhost:8080/api/posts --data-urlencode 'socialMedia=LINKEDIN'
```

`GET /api/schedules` accepts the same search filters as `social scheduler list`
(see [Search schedules](/guide/scheduling#search-schedules)):

```sh
curl --get localhost:8080/api/schedules --data-urlencode 'search=release notes'
curl --get localhost:8080/api/schedules --data-urlencode 'ids=1,3'
curl --get localhost:8080/api/schedules --data-urlencode 'socialMedia=LINKEDIN'
```

## Examples

```sh
curl -X POST localhost:8080/api/posts \
  -H 'Content-Type: application/json' \
  -d '{"text":"hello from the REST API"}'

curl -X POST localhost:8080/api/schedules \
  -H 'Content-Type: application/json' \
  -d '{"postId":"1","publishDate":"2099-01-02T09:00:00Z","socialMedia":"TWITTER"}'

curl -X POST localhost:8080/api/schedules \
  -H 'Content-Type: application/json' \
  -d '{"postId":"1","publishDate":"2099-01-03T09:00:00Z","socialMedia":"LINKEDIN"}'

curl -X POST localhost:8080/api/schedules/random \
  -H 'Content-Type: application/json' \
  -d '{"publishDate":"2099-01-04","socialMedia":"TWITTER"}'

curl localhost:8080/api/schedules
```

## Errors

| Status | When |
| --- | --- |
| `400` | An invalid query value or an unusable request body. |
| `404` | Missing configuration, or deleting an unknown schedule. |
| `502` | The social network rejected the publish request. |