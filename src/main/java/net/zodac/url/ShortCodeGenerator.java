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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class used to convert URLs into shortened versions.
 */
final class ShortCodeGenerator {

    private static final Logger LOGGER = LogManager.getLogger(ShortCodeGenerator.class);
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SHORT_CODE_LENGTH = 10;

    private ShortCodeGenerator() {

    }

    /**
     * Takes the {@code inputUrl} and hashes it using {@value #HASH_ALGORITHM}. The output value is truncated to a length of
     * {@value #SHORT_CODE_LENGTH}.
     *
     * @param inputUrl the URL to shorten
     * @return the generated short code for the URL
     * @throws IllegalStateException thrown if an invalid algorithm is used for {@link MessageDigest}
     */
    static String generate(final String inputUrl) {
        try {
            LOGGER.trace("Generating shortcode for {}", inputUrl);
            final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            final byte[] digestHash = digest.digest(inputUrl.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(digestHash)
                .substring(0, SHORT_CODE_LENGTH);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
