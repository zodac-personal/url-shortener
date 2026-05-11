package net.zodac.url;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import net.zodac.util.EnvironmentVariableUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.RedisClient;

/**
 * Implementation of {@link HttpServlet} which exposed endpoints for URL shortening and resolving.
 */
public class UrlShortenerServlet extends HttpServlet {

    private static final long serialVersionUID = -6921522406540539222L;
    private static final Logger LOGGER = LogManager.getLogger(UrlShortenerServlet.class);

    private static final String HTML_CONTENT_TYPE = "text/html;charset=UTF-8";
    private static final String SHORT_TO_URL_PREFIX = "short:";
    private static final String URL_TO_SHORT_PREFIX = "url:";
    private static final RedisClient JEDIS = RedisClient.builder().hostAndPort(
            EnvironmentVariableUtils.getOrDefault("CACHE_HOSTNAME", "cache"),
            EnvironmentVariableUtils.getIntOrDefault("CACHE_PORT", 6379)
        )
        .build();

    private static final String STATUS_PATH = "/status";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            response.setContentType(HTML_CONTENT_TYPE);
            final String pathInfo = request.getPathInfo();

            // Healthcheck endpoint
            if (STATUS_PATH.equals(pathInfo)) {
                LOGGER.trace("Received GET request at '{}'", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("<html><body><p>OK</p></body></html>");
                return;
            }

            LOGGER.debug("Received GET request at '{}' with parameters: {}", request.getRequestURI(), request.getQueryString());
            final String shortCode = request.getPathInfo().substring(1);
            final String originalUrl = JEDIS.get(SHORT_TO_URL_PREFIX + shortCode);
            if (originalUrl == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(String.format("Invalid short code: [%s]", shortCode));
                return;
            }

            response.sendRedirect(originalUrl);
        } catch (final Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Internal server error");
        }
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            LOGGER.debug("Received POST request at '{}' with parameters: {}", request.getRequestURI(), request.getParameterMap());
            response.setContentType(HTML_CONTENT_TYPE);
            final String inputUrl = request.getParameter("url");

            if (!UrlValidator.isValid(inputUrl)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(String.format("Invalid URL: [%s]", inputUrl));
                return;
            }

            final String shortUrl = getOrCreateShortUrl(request, inputUrl);
            LOGGER.debug("Input URL [{}] shortened to [{}]", inputUrl, shortUrl);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(
                """
                    <html>
                        <body>
                            <h1>Hello from URL Shortener</h1>
                            <div>
                                <b>Original:</b>
                                %s
                            </div>
                            <div>
                                <b>Shortened:</b>
                                %s
                            </div>
                        </body>
                    </html>""".formatted(inputUrl, shortUrl));
        } catch (final Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Internal server error");
        }
    }

    private static String getOrCreateShortUrl(final HttpServletRequest request, final String inputUrl) {
        final String existingShortUrl = JEDIS.get(URL_TO_SHORT_PREFIX + inputUrl);
        if (existingShortUrl != null) {
            LOGGER.debug("Found value for URL '{}' in cache", inputUrl);
            return existingShortUrl;
        }

        LOGGER.debug("Nothing in cache, generating new shortened URL");
        final String shortCode = ShortCodeGenerator.generate(inputUrl);
        final String shortUrl = generateShortUrl(request, shortCode);
        JEDIS.set(SHORT_TO_URL_PREFIX + shortCode, inputUrl);
        JEDIS.set(URL_TO_SHORT_PREFIX + inputUrl, shortUrl);
        return shortUrl;
    }

    private static String generateShortUrl(final HttpServletRequest request, final String shortCode) {
        LOGGER.trace("Generating short URL for {}", shortCode);
        return String.format("%s://%s:%s/%s", request.getScheme(), request.getServerName(), request.getServerPort(), shortCode);
    }

    @Override
    public void destroy() {
        JEDIS.close();
    }
}
