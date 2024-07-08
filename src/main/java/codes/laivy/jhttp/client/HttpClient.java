package codes.laivy.jhttp.client;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

/**
 * A client interface for making HTTP requests.
 *
 * <p>This interface provides methods to manage the connection status, and obtain the underlying socket.</p>
 *
 * @see Closeable
 * @see Socket
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface HttpClient extends Closeable {

    /**
     * Checks if this client is secure
     *
     * @return True if the client is secure; false otherwise
     * @since 1.0
     */
    default boolean isSecure() {
        return this instanceof SecureHttpClient;
    }

    /**
     * Retrieves the underlying socket of the HTTP client.
     *
     * @return The socket associated with this HTTP client.
     * @since 1.0
     */
    @NotNull Socket getSocket();

    /**
     * Checks whether the HTTP client is open or not.
     *
     * @return {@code true} if the client is open, {@code false} otherwise.
     * @since 1.0
     */
    boolean isOpen();

    /**
     * Closes the http client connection
     *
     * @since 1.0
     */
    void close() throws IOException;

}
