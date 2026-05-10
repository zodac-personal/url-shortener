package net.zodac;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Annotation used to mark a test class to fail all tests if the {@link ApplicationRunningCondition} fails.
 *
 * <p>
 * Used as follows for a test class:
 *
 * <p>
 * {@snippet :
 *     @RequiresRunningApplication
 *     class EndpointIntegrationTests {
 *         // Test cases here
 *     }
 *}
 */
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ApplicationRunningCondition.class)
public @interface RequiresRunningApplication {

}
