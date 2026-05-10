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
