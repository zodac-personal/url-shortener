package net.zodac.url;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class used to convert URLs into shortened versions.
 */
final class ShortCodeGenerator {

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
     */
    static String generate(final String inputUrl) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            final byte[] digestHash = digest.digest(inputUrl.getBytes(StandardCharsets.UTF_8));

            // TODO: Maybe check this and look for something to do alphanumeric only? No big deal
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(digestHash)
                .substring(0, SHORT_CODE_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
