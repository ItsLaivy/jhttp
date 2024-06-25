package codes.laivy.jhttp.connection;

import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLSession;

/**
 * A secure extension of the {@link HttpClient} interface that provides support for SSL/TLS.
 *
 * <p>This interface extends the base {@link HttpClient} interface to add methods for obtaining
 * an SSL session.</p>
 *
 * @see HttpClient
 * @see SSLSession
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface SecureHttpClient extends HttpClient {

    /**
     * Retrieves the SSL session associated with the secure HTTP client.
     *
     * @return The SSL session associated with the secure HTTP client.
     * @since 1.0
     */
    @NotNull SSLSession getSession();

}