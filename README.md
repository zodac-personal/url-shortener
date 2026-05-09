# Implementation

## Deployment

The application can be launched with `docker`:

```shell
docker compose up --build -d
```

If `docker` is not available, you can run using `mvn`:

```shell
mvn package
java -jar target/url-shortener.jar
```

**NOTE:** I couldn't get the provided [mvnw](mvnw) wrapper to work on my Debian machine, but it might work on other systems:

```shell
./mvnw package
java -jar target/url-shortener.jar

# On Windows:
mvnw.cmd package
java -jar target/url-shortener.jar
```

This will launch the Java `url-shortener` application, and the external cache. You should see the following line in the console to confirm the
application started successfully:

```shell
Server started on http://localhost:8080
```

## Automated Testing

First start the application (either option defined in [Deployment](#deployment)), then run `mvn clean install -Pintegration-tests` to run all unit
tests and the endpoint tests.

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

## Ran Out Of Time

- I would have done a minimal docker image with distroless/non-root and use `jlink` to reduce JDK size
    - I would also have made the services in [docker-compose.yml](docker-compose.yml) more secure (drop capabilities, no privileges, resource limits,
      etc.)
    - Added a `/status` or `/health` endpoint so a `HEALTHCHECK` could be included
- Proper API docs (**OpenAPI**/**Swagger** or **RAML**)
- Added **HAProxy** (or a similar load balancer) so the number of `url-shortener` containers could be scaled and HAProxy could balance requests
- Added **nginx** (or a similar reverse proxy) to handle SSL termination
- Actual integration tests for the full E2E flow (if additional components were added)
- Added linting (**PMD**, **SpotBugs**, **CheckStyle**, etc.)
- Used a proper logging framework
- Added some instrumentation, maybe?
    - Future planning to expose some metrics for something like **Prometheus**
- Potentially introduced an in-memory cache (like [Caffeine](https://github.com/ben-manes/caffeine)) in addition to **valkey**
    - Probably overkill
- Updated to JDK 26

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
