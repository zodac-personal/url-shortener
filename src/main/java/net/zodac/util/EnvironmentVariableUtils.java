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

package net.zodac.util;

/**
 * Utility class for retrieving environment variables.
 */
public final class EnvironmentVariableUtils {

    private EnvironmentVariableUtils() {

    }

    /**
     * Returns the {@code variableName} specified if it is set as a {@link System} property or as an environment variable. The order of
     * the search is to check for an environment variable first, then if none is found, check for a system property.
     *
     * <p>
     * If neither is set then the default value provided is returned.
     *
     * @param variableName the property name to search for
     * @param defaultValue the default value if no property exists
     * @return the value of the searched name, otherwise the {@code defaultValue}
     */
    public static String getOrDefault(final String variableName, final String defaultValue) {
        final String environmentVariable = System.getenv(variableName);
        if (environmentVariable != null) {
            return environmentVariable;
        }
        return System.getProperty(variableName, defaultValue);
    }

    /**
     * Returns the {@code variableName} specified if it is set as a {@link System} property or as an environment variable. The order of
     * the search is property first then environment variable.
     *
     * <p>
     * The returned value is then converted to an {@code int}.
     *
     * @param variableName the property to search for
     * @param defaultValue the default value if no property exists
     * @return the value of the searched property, otherwise the {@code defaultValue}
     */
    public static int getIntOrDefault(final String variableName, final int defaultValue) {
        final String value = System.getProperty(variableName, System.getenv(variableName));
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }
}
