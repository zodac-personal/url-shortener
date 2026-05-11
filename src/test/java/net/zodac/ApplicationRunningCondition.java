/*
 * BSD Zero Clause License
 *
 * Copyright (c) 2026 zodac.net
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

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
