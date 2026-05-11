package net.zodac;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * A {@link BeforeAllCallback} used to check if the application is running. Can be used to stop execution of test classes if the condition is not met.
 */
public class ApplicationRunningCondition implements BeforeAllCallback {

    private static final String BASE_URL = System.getProperty("base.url", "http://localhost:8080");
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @Override
    public void beforeAll(final ExtensionContext context) {
        try {
            final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/status"))
                .GET()
                .build();
            final HttpResponse<Void> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new IllegalStateException(String.format("Application is not running, '/status' returned HTTP code: %d", response.statusCode()));
            }
        } catch (final IOException e) {
            throw new IllegalStateException(String.format("Application is not running or unreachable at: '%s'", BASE_URL), e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while checking application status", e);
        }
    }
}
