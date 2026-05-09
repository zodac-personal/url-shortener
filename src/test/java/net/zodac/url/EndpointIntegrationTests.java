package net.zodac.url;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests for the exposed endpoints for the application.
 *
 * <p>
 * Application must already be running for these tests to pass.
 */
class EndpointIntegrationTests {

    private static final String BASE_URL = System.getProperty("base.url", "http://localhost:8080");
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    // URLs
    private static final String VALID_HTTPS_URL = "https://youtube.com";
    private static final String VALID_HTTPS_SHORT_CODE = "FGeTGg6Mcb";
    private static final String VALID_HTTP_URL = "http://youtube.com";
    private static final String VALID_HTTP_SHORT_CODE = "faZolJjBhq";

    @Test
    void validateStatusEndpoint() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/status"))
            .header("Content-Type", "text/html")
            .GET()
            .build();
        final HttpResponse<Void> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());

        assertThat(response.statusCode())
            .isEqualTo(HttpURLConnection.HTTP_OK);
    }

    @Test
    void createShortCodeForHttps() throws IOException, InterruptedException {
        final HttpRequest request = buildPostRequest(VALID_HTTPS_URL);
        final HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode())
            .isEqualTo(HttpURLConnection.HTTP_CREATED);

        assertThat(response.body())
            .contains("http://localhost:8080/" + VALID_HTTPS_SHORT_CODE);
    }

    @Test
    void resolveOriginalUrlForHttps() throws IOException, InterruptedException {
        final HttpRequest request = buildGetRequest(VALID_HTTPS_SHORT_CODE);
        final HttpResponse<Void> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());

        assertThat(response.statusCode())
            .isEqualTo(HttpURLConnection.HTTP_MOVED_TEMP);

        final Optional<String> location = response.headers().firstValue("location");
        assertThat(location)
            .isPresent()
            .hasValue(VALID_HTTPS_URL);
    }

    @Test
    void createShortCodeForHttp() throws IOException, InterruptedException {
        final HttpRequest request = buildPostRequest(VALID_HTTP_URL);
        final HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode())
            .isEqualTo(HttpURLConnection.HTTP_CREATED);

        assertThat(response.body())
            .contains("http://localhost:8080/" + VALID_HTTP_SHORT_CODE);
    }

    @Test
    void resolveOriginalUrlForHttp() throws IOException, InterruptedException {
        final HttpRequest request = buildGetRequest(VALID_HTTP_SHORT_CODE);
        final HttpResponse<Void> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());

        assertThat(response.statusCode())
            .isEqualTo(HttpURLConnection.HTTP_MOVED_TEMP);

        final Optional<String> location = response.headers().firstValue("location");
        assertThat(location)
            .isPresent()
            .hasValue(VALID_HTTP_URL);
    }

    @Test
    void createShortCodeForInvalidProtocol() throws IOException, InterruptedException {
        final HttpRequest request = buildPostRequest("file://youtube.com");
        final HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode())
            .isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    @Test
    void resolveOriginalUrlForInvalidShortCode() throws IOException, InterruptedException {
        final HttpRequest request = buildGetRequest("invalidCode");
        final HttpResponse<Void> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());

        assertThat(response.statusCode())
            .isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

    private static HttpRequest buildGetRequest(final String shortCode) {
        return HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/" + shortCode))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .GET()
            .build();
    }

    private static HttpRequest buildPostRequest(final String url) {
        final String urlBody = "url=" + URLEncoder.encode(url, StandardCharsets.UTF_8);
        return HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(urlBody))
            .build();
    }
}
