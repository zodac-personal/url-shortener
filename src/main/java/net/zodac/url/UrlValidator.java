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

package net.zodac.url;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

/**
 * Utility class used to validate URLs.
 */
final class UrlValidator {

    private static final Set<String> VALID_URL_SCHEMES = Set.of("HTTP", "HTTPS");

    private UrlValidator() {

    }

    /**
     * For the {@code inputUrl}, checks if it's a valid {@link URI}, has a {@link URI#getScheme()} in {@link #VALID_URL_SCHEMES}, and has a non-null
     * {@link URI#getHost()}.
     *
     * @param inputUrl the URL to validate
     * @return {@code true} if the URL is valid
     */
    static boolean isValid(final String inputUrl) {
        if (inputUrl == null) {
            return false;
        }

        try {
            final URI uri = URI.create(inputUrl);
            final String scheme = uri.getScheme().toUpperCase(Locale.getDefault());
            return VALID_URL_SCHEMES.contains(scheme) && uri.getHost() != null;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }
}
