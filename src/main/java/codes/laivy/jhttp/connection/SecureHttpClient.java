package codes.laivy.jhttp.connection;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

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
@ApiStatus.Experimental
public interface SecureHttpClient extends HttpClient {

    /**
     * Retrieves the SSL session associated with the secure HTTP client.
     *
     * <p>This method returns {@code null} if no SSL session is established.</p>
     *
     * @return The SSL session associated with the secure HTTP client, or {@code null} if no session is established.
     * @since 1.0
     */
    @Nullable
    SSLSession getSession();
    
}