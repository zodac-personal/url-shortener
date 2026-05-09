package net.zodac.url;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

/**
 * Main application class.
 */
public class Main {

    private static final int TOMCAT_PORT = EnvironmentVariableUtils.getIntOrDefault("TOMCAT_PORT", 8080);

    /**
     * Main method to start the Tomcat instance.
     */
    static void main() {
        System.out.println("Hostname: " + EnvironmentVariableUtils.getOrDefault("HOSTNAME", "localhost"));

        final Tomcat tomcat = new Tomcat();
        tomcat.setPort(TOMCAT_PORT);
        tomcat.getConnector();

        final Context context = tomcat.addContext("", new File(".").getAbsolutePath());
        Tomcat.addServlet(context, "urlShortener", new UrlShortenerServlet());
        context.addServletMappingDecoded("/*", "urlShortener");

        try {
            tomcat.start();
            System.out.println("Server started on http://localhost:" + TOMCAT_PORT);  // TODO: Logger
            tomcat.getServer().await();
        } catch (final LifecycleException e) {
            throw new RuntimeException(e);
        }
    }
}
