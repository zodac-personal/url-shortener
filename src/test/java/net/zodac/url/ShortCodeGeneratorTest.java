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
        "http://www.youtube.com,lrOqK46O-e",
        "http://youtube.com,faZolJjBhq",
    })
    void testGenerate(final String input, final String expected) {
        final String actual = ShortCodeGenerator.generate(input);
        assertThat(actual)
            .isEqualTo(expected);
    }
}
