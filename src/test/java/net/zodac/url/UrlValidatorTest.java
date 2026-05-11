package net.zodac.url;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Unit tests for {@link UrlValidator}.
 */
class UrlValidatorTest {

    @ParameterizedTest
    @CsvSource({
        "http://www.google.com,true",
        "https://www.google.com,true",
        "file://www.google.com,false",
        "https://,false",
        ",false"
    })
    void testIsValid(final String input, final boolean expected) {
        final boolean actual = UrlValidator.isValid(input);
        assertThat(actual)
            .isEqualTo(expected);
    }
}
