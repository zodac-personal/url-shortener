package net.zodac.url;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UrlShortenerServlet extends HttpServlet {

    // TODO: Move to redis/valkey
    private static final Map<String, String> SHORT_TO_URL = new ConcurrentHashMap<>();
    private static final Map<String, String> URL_TO_SHORT = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String shortCode = request.getPathInfo().substring(1);
        final String originalUrl = SHORT_TO_URL.get(shortCode);

        // TODO: Handle miss
//        response.getWriter().write("<p>Hello from URL Shortener</p>");

        response.setContentType("text/html;charset=UTF-8");
        response.sendRedirect(originalUrl);
    }

    // TODO: Return 201_CREATED
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String inputUrl = request.getParameter("url");

        // TODO: Sanitize input (apache commons for url validation)

        final String shortCode = generateShortCode(inputUrl);
        SHORT_TO_URL.putIfAbsent(shortCode, inputUrl);
        final String shortUrl = URL_TO_SHORT.computeIfAbsent(inputUrl, unused -> generateShortUrl(request, shortCode));

        response.setContentType("text/html;charset=UTF-8");
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

    // TODO: Move to another class for testing
    private static String generateShortCode(final String inputUrl) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA256");
            final byte[] digestHash = digest.digest(inputUrl.getBytes(StandardCharsets.UTF_8));

            // TODO: Maybe check this and look for something to do alphanumeric only? No big deal
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(digestHash)
                .substring(0, 10);  // Extract length to const
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
