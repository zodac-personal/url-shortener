package net.zodac.url;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

/**
 * Utility class used to validate URLs.
 */
class UrlValidator {

    private static final Set<String> VALID_URL_SCHEMES = Set.of("HTTP", "HTTPS");

    /**
     * For the {@code inputUrl}, checks if it's a valid {@link URI}, has a {@link URI#getScheme()} in {@link #VALID_URL_SCHEMES}, and has a non-null
     * {@link URI#getHost()}.
     *
     * @param inputUrl the URL to validate
     * @return {@code true} if the URL is valid
     */
    static boolean isValid(final String inputUrl) {
        try {
            final URI uri = URI.create(inputUrl);
            final String scheme = uri.getScheme().toUpperCase(Locale.getDefault());
            return VALID_URL_SCHEMES.contains(scheme) && uri.getHost() != null;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }
}
