package net.zodac.url;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Unit tests for {@link ShortCodeGenerator}.
 */
class ShortCodeGeneratorTest {

    @ParameterizedTest
    @CsvSource({
        "http://www.google.com,JT0UJwMEHd",
        "http://www.youtube.com,lrOqK46O-e"
    })
    void testGenerate(final String input, final String expected) {
        final String actual = ShortCodeGenerator.generate(input);
        assertThat(actual)
            .isEqualTo(expected);
    }
}
