package codes.laivy.jhttp.client;

import codes.laivy.jhttp.element.HttpMetrics;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;

/**
 * Interface representing an HTTP client.
 * This interface provides methods to retrieve socket information, address details,
 * HTTP metrics, and proxy usage.
 * <p>
 * Additionally, it includes methods for checking
 * the open status of the client and closing the connection.
 *
 * @author Daniel Richard (Laivy)
 * @version 1.0-SNAPSHOT
 */
public interface HttpClient extends Closeable {

    /**
     * Retrieves the underlying socket used by the HTTP client.
     *
     * @return the {@link Socket} used by the HTTP client; never null.
     */
    @NotNull Socket getSocket();

    /**
     * Retrieves the address of the HTTP client.
     *
     * @return the {@link SocketAddress} of the HTTP client; never null.
     */
    @NotNull SocketAddress getAddress();

    /**
     * Retrieves the metrics associated with the HTTP client's operations.
     *
     * @return the {@link HttpMetrics} related to the HTTP client's activities;
     *         never null.
     */
    @NotNull HttpMetrics getMetrics();

    /**
     * Checks if the HTTP client is using a proxy server.
     *
     * @return {@code true} if the client is using a proxy, {@code false} otherwise.
     * @throws NullPointerException caused if there are no previous headers to see if it is a proxy
     */
    boolean hasProxy() throws NullPointerException;

    /**
     * Checks if the HTTP client is currently open.
     *
     * @return {@code true} if the client is open, {@code false} otherwise.
     */
    boolean isOpen();

    /**
     * Closes the HTTP client immediately.
     *
     * @throws ClosedChannelException if the channel is already closed.
     * @throws IOException if an I/O error occurs while closing the client.
     */
    default void close() throws ClosedChannelException, IOException {
        close(Duration.ZERO);
    }

    /**
     * Closes the HTTP client with a specified timeout duration.
     * It will await for the current tasks to finish before shutdown the connection
     *
     * @param timeout the duration to wait before closing the client;
     *                must not be null.
     * @throws ClosedChannelException if the channel is already closed.
     * @throws IOException if an I/O error occurs while closing the client.
     */
    void close(@NotNull Duration timeout) throws ClosedChannelException, IOException;

}
