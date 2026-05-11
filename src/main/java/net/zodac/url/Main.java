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

import java.io.File;
import net.zodac.util.EnvironmentVariableUtils;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Main application class.
 */
public final class Main {

    private static final int TOMCAT_PORT = 8080;
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private Main() {

    }

    /**
     * Main method to start the Tomcat instance.
     */
    static void main() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        LOGGER.debug("Hostname: {}", EnvironmentVariableUtils.getOrDefault("HOSTNAME", "localhost"));

        final Tomcat tomcat = new Tomcat();
        tomcat.setPort(TOMCAT_PORT);
        tomcat.getConnector();

        final Context context = tomcat.addContext("", new File(".").getAbsolutePath());
        Tomcat.addServlet(context, "urlShortener", new UrlShortenerServlet());
        context.addServletMappingDecoded("/*", "urlShortener");

        try {
            tomcat.start();
            LOGGER.info("Server started on http://localhost:{}", TOMCAT_PORT);
            tomcat.getServer().await();
        } catch (final LifecycleException e) {
            LOGGER.error("Failed to start tomcat server", e);
        }
    }
}
