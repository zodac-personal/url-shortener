package net.zodac.url;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link HttpServlet} which exposed endpoints for URL shortening and resolving.
 */
public class UrlShortenerServlet extends HttpServlet {

    // TODO: Move to redis/valkey
    private static final Map<String, String> SHORT_TO_URL = new ConcurrentHashMap<>();
    private static final Map<String, String> URL_TO_SHORT = new ConcurrentHashMap<>();
    private static final String HTML_CONTENT_TYPE = "text/html;charset=UTF-8";

    // TODO: Add some API docs
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String shortCode = request.getPathInfo().substring(1);
        if (!SHORT_TO_URL.containsKey(shortCode)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(String.format("Invalid short code: [%s]", shortCode));
            return;
        }

        response.setContentType(HTML_CONTENT_TYPE);
        final String originalUrl = SHORT_TO_URL.get(shortCode);
        response.sendRedirect(originalUrl);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String inputUrl = request.getParameter("url");

        if (!UrlValidator.isValid(inputUrl)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(String.format("Invalid URL: [%s]", inputUrl));
            return;
        }

        final String shortCode = ShortCodeGenerator.generate(inputUrl);
        SHORT_TO_URL.putIfAbsent(shortCode, inputUrl);
        final String shortUrl = URL_TO_SHORT.computeIfAbsent(inputUrl, unused -> generateShortUrl(request, shortCode));

        response.setContentType(HTML_CONTENT_TYPE);
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
                </html>
                """.formatted(inputUrl, shortUrl));
    }

    private static String generateShortUrl(HttpServletRequest request, final String shortCode) {
        return String.format("%s://%s:%s/%s", request.getScheme(), request.getServerName(), request.getServerPort(), shortCode);
    }
}
