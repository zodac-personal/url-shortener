# Implementation

## Deployment

The application can be launched with `docker`:

```shell
docker compose up --build -d --scale backend=10
```

This will launch the external cache, the load-balancer, and 10 replicas of the Java backend. You should see the following line in the console per
replica to confirm the application started successfully:

```shell
Server started on http://localhost:8080
```

Similarly, you can use `docker ps` to verify the status of the container:

<details>
    <summary>Docker container health</summary>

```shell
# Java backends
docker ps -a | grep url-shortener
# Output
a39b484cf236   url-shortener-backend  "java -jar app.jar"      22 seconds ago   Up 16 seconds (healthy)   8080/tcp          url-shortener-backend-6
8f2422a74b97   url-shortener-backend  "java -jar app.jar"      22 seconds ago   Up 16 seconds (healthy)   8080/tcp          url-shortener-backend-1
61da27d913dc   url-shortener-backend  "java -jar app.jar"      22 seconds ago   Up 15 seconds (healthy)   8080/tcp          url-shortener-backend-7
6c56ad08d034   url-shortener-backend  "java -jar app.jar"      22 seconds ago   Up 15 seconds (healthy)   8080/tcp          url-shortener-backend-3
ba235545679b   url-shortener-backend  "java -jar app.jar"      22 seconds ago   Up 15 seconds (healthy)   8080/tcp          url-shortener-backend-8
b23e72efedb0   url-shortener-backend  "java -jar app.jar"      22 seconds ago   Up 14 seconds (healthy)   8080/tcp          url-shortener-backend-2
44abcef2aad9   url-shortener-backend  "java -jar app.jar"      22 seconds ago   Up 14 seconds (healthy)   8080/tcp          url-shortener-backend-5
bbf06f1c4cb2   url-shortener-backend  "java -jar app.jar"      22 seconds ago   Up 14 seconds (healthy)   8080/tcp          url-shortener-backend-4
d7326714334c   url-shortener-backend  "java -jar app.jar"      22 seconds ago   Up 13 seconds (healthy)   8080/tcp          url-shortener-backend-10
94aa29d304d2   url-shortener-backend  "java -jar app.jar"      22 seconds ago   Up 13 seconds (healthy)   8080/tcp          url-shortener-backend-9

# Cache
docker ps -a | grep cache
# Output
2709800a98ba   valkey/valkey:9.0.3-alpine   "docker-entrypoint.s…"   About a minute ago   Up About a minute (healthy)   6379/tcp  cache

# Load-balancer
docker ps -a | grep load-balancer
# Output
74b30f76e741   haproxy:3.3.9-alpine         "docker-entrypoint.s…"   About a minute ago   Up About a minute (healthy)   0.0.0.0:8080->80/tcp, [::]:8080->80/tcp  load-balancer
```

</details>

## Automated Testing

First start the application then run `mvn clean install -Pintegration-tests` to run all unit tests and the endpoint tests.

```shell
docker compose up --build -d
mvn clean install -Pintegration-tests
```

## Manual Testing

### Shorten URL

```shell
curl -X POST -d 'url=https://www.youtube.com' http://localhost:8080

# Output
<html>
    <body>
        <h1>Hello from URL Shortener</h1>
        <div>
            <b>Original:</b>
            https://www.youtube.com
        </div>
        <div>
            <b>Shortened:</b>
            http://localhost:8080/2TMawShw8p
        </div>
    </body>
</html>
```

### Resolve URL

```shell
curl -X GET http://localhost:8080/2TMawShw8p -I

# Output
HTTP/1.1 302 
Location: https://www.youtube.com
Content-Type: text/html;charset=UTF-8
Content-Length: 0
Date: Sat, 09 May 2026 02:20:24 GMT
```

You can also load `http://localhost:8080/2TMawShw8p` in a browser, which should then redirect to [YouTube](https://youtube.com).

### Error Cases

```shell
# Shorten an invalid URL
curl -X POST -d 'url=file://www.youtube.com' http://localhost:8080
# Output
Invalid URL: [file://www.youtube.com]

# Resolve a non-existent URL
curl -X GET http://localhost:8080/invalid
# Output
Invalid short code: [invalid]
```

### Caching

You can confirm the cache is working by stopping and restarting the application:

```shell
docker compose down && docker compose up --build
```

Then run a **GET** request to resolve a known short-code:

```shell
curl -X GET http://localhost:8080/2TMawShw8p -I
```

You should get a response despite no **POST** request shortening the URL, and can see the following log entry:

```shell
Found value in cache
```

### Load Balancing

You can confirm the load-balancer is working by stopping and restarting the application with multiple replicas

```shell
docker compose down && docker compose up --build --scale backend=10
```

Then run some **POST** requests to shorten a URL (can be the same request):

```shell
curl -X POST -d 'url=https://www.youtube.com'  http://localhost:8080
```

You should be able to see logs across the `backend` instances showing the load-balancer is spreading the requests across the replicas:

```shell
backend-8   | Found value in cache
backend-6   | Found value in cache
backend-2   | Found value in cache
backend-10  | Found value in cache
```

## Assumptions

- Assuming that a valid URL begins with either `https://` or `http://`, no support for other protocols
    - This can easily be extended in [UrlValidator](src/main/java/net/zodac/url/UrlValidator.java)
- No URL resolution needs to be performed
    - If two URLs resolve/redirect to the same target, they will have different short codes since they are two distinct URLs
- Short-code generation uses Base64 encoding, which may include some non-alphanumeric characters
- All endpoints live at `/`, and there are no sub-paths for this application
    - I would have put this under `url` if there was another resource exposed, but since we're considering scalability, I wanted this application to
      be stateless and easy to replicate
- The **POST** endpoint to shorten a URL:
    - Returns a full `<html>` page, rather than a `<div>` block (or some other partial element)
    - Returns a full URL in the format `http://server_address:server_port/<short_code>`, rather than just the short code itself
- The **GET** endpoint to resolve a short code:
    - Returns no HTML body, simply a redirect (HTTP 302, not 301) to the target URL
- HAProxy load-balancer allows for a maximum of 10 replicas
    - Using a simple `leastconn` strategy for load-balancing, instead of round-robin or hash-based assignment

## Ran Out Of Time

- Proper API docs (**OpenAPI**/**Swagger** or **RAML**)
- Added **nginx** (or a similar reverse proxy) to handle SSL termination
- Actual integration tests for the full E2E flow (if additional components were added)
- Added linting (**PMD**, **SpotBugs**, **CheckStyle**, etc.)
- Used a proper logging framework
- Added some instrumentation, maybe?
    - Future planning to expose some metrics for something like **Prometheus**
- Potentially introduced an in-memory cache (like [Caffeine](https://github.com/ben-manes/caffeine)) in addition to **valkey**
    - Probably overkill

----

# URL Shortener — Coding Challenge

*Please spend around 1-2 hours on the coding part of this challenge. Scale down the requirements to fit the time allowed if needed. It's also A-okay
to update the README outside of that time.*

*And it goes without saying, but it doesn't make sense to use agents!*

## Getting Started

You will need Java 21 or newer installed.

To build and run:

```shell
./mvnw package
java -jar target/url-shortener.jar
```

On Windows:

```shell
mvnw.cmd package
java -jar target/url-shortener.jar
```

The server starts on **http://localhost:8080**.

---

## The Task

Implement a URL shortener API inside `UrlShortenerServlet.java`.

**It should:**

- Accept a long URL and return an HTML response showing the original URL and its shortened equivalent
- It MUST always return the same short code for a given original URL
- Resolve a short code and redirect the browser to the original URL
- This MUST be robust when used at scale.

In-memory storage is fine.

You're welcome to add dependencies to `pom.xml` and introduce new classes, but please use the `Servlet` class provided. There are no other
constraints.

**Please include a short note in this README** covering:

- How to use your API (endpoints, request format, examples)
- Any assumptions or shortcuts you made
- Anything you would do differently with more time
