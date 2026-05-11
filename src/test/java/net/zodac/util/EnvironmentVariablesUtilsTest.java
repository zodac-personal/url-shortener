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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link EnvironmentVariableUtils}.
 */
class EnvironmentVariablesUtilsTest {

    private static int systemPropertyNameIncrementer = 1;

    @Test
    void whenGetOrDefault_givenVariableIsSet_thenVariableValueIsReturned() {
        final String propertyName = getSystemPropertyName();
        System.setProperty(propertyName, "user-set");

        final String result = EnvironmentVariableUtils.getOrDefault(propertyName, "default-value");
        assertThat(result)
            .isEqualTo("user-set");
    }

    @Test
    void whenGetOrDefault_givenVariableIsNotSet_thenDefaultIsReturned() {
        final String propertyName = getSystemPropertyName();

        final String result = EnvironmentVariableUtils.getOrDefault(propertyName, "default-value");
        assertThat(result)
            .isEqualTo("default-value");
    }

    @Test
    void whenGetIntOrDefault_givenVariableIsSet_thenVariableValueIsReturned() {
        final String propertyName = getSystemPropertyName();
        System.setProperty(propertyName, "19");

        final int result = EnvironmentVariableUtils.getIntOrDefault(propertyName, 5);
        assertThat(result)
            .isEqualTo(19);
    }

    @Test
    void whenGetIntOrDefault_givenVariableIsNotSet_thenDefaultIsReturned() {
        final String propertyName = getSystemPropertyName();

        final int result = EnvironmentVariableUtils.getIntOrDefault(propertyName, 5);
        assertThat(result)
            .isEqualTo(5);
    }

    @Test
    void whenGetIntOrDefault_givenVariableIsSet_andValueIsNotValidInt_thenDefaultIsReturned() {
        final String propertyName = getSystemPropertyName();

        System.setProperty(propertyName, "value");
        final int result = EnvironmentVariableUtils.getIntOrDefault(propertyName, -3);
        assertThat(result)
            .isEqualTo(-3);
    }

    private static String getSystemPropertyName() {
        return "property_" + systemPropertyNameIncrementer++;
    }
}
