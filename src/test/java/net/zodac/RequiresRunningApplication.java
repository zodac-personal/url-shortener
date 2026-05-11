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
