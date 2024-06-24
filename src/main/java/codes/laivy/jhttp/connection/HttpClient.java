package codes.laivy.jhttp.connection;

import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.element.response.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

/**
 * A client interface for making HTTP requests.
 *
 * <p>This interface provides methods to read and write HTTP requests and responses,
 * manage the connection status, and obtain the underlying socket.</p>
 *
 * @see Closeable
 * @see Socket
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface HttpClient extends Closeable {

    /**
     * Retrieves the underlying socket of the HTTP client.
     *
     * @return The socket associated with this HTTP client.
     * @since 1.0
     */
    @NotNull Socket getSocket();

    /**
     * Reads an HTTP request from the client.
     *
     * @return The HTTP request read from the client.
     * @throws IOException If an I/O error occurs while reading the request.
     * @since 1.0
     */
    @NotNull HttpRequest read() throws IOException;

    /**
     * Writes an HTTP response to the client.
     *
     * @param response The HTTP response to be written.
     * @throws IOException If an I/O error occurs while writing the response.
     * @since 1.0
     */
    void write(@NotNull HttpResponse response) throws IOException;

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
