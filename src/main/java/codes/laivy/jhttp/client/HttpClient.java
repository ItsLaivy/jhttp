package codes.laivy.jhttp.client;

import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.exception.parser.IllegalHttpVersionException;
import codes.laivy.jhttp.exception.parser.request.HttpRequestParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;

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
     * @return The HTTP request read from the client or null if there's nothing to read
     * @throws ClosedChannelException If the channel has been closed
     * @throws IOException If an I/O error occurs while reading the request.
     * @throws IllegalHttpVersionException If the received message cannot be parsed as a valid http version
     * @throws HttpRequestParseException If the received message cannot be parsed into a valid http request
     * @since 1.0
     */
    @Nullable HttpRequest.Future read() throws IOException, ClosedChannelException, IllegalHttpVersionException, HttpRequestParseException;

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
