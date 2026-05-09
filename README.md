# URL Shortener — Coding Challenge

*Please spend around 1-2 hours on the coding part of this challenge. Scale down the requirements to fit the time allowed if needed. It's also A-okay to update the README outside of that time.*

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

You're welcome to add dependencies to `pom.xml` and introduce new classes, but please use the `Servlet` class provided. There are no other constraints.

**Please include a short note in this README** covering:
- How to use your API (endpoints, request format, examples)
- Any assumptions or shortcuts you made
- Anything you would do differently with more time
