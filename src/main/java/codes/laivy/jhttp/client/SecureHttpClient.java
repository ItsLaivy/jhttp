package codes.laivy.jhttp.client;

import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * A secure extension of the {@link HttpClient} interface that provides support for SSL/TLS.
 *
 * <p>This interface extends the base {@link HttpClient} interface to add methods for obtaining
 * an SSL engine.</p>
 *
 * This secure http client is created after the handshake
 *
 * @see HttpClient
 * @see SSLEngine
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface SecureHttpClient extends HttpClient {

    /**
     * Retrieves the SSL context associated with the secure HTTP client.
     *
     * @return The SSL context associated with the secure HTTP client.
     * @since 1.0
     */
    @NotNull SSLContext getContext();

    /**
     * Retrieves the SSL engine associated with the secure HTTP client.
     *
     * @return The SSL engine associated with the secure HTTP client.
     * @since 1.0
     */
    @NotNull SSLEngine getEngine();

}