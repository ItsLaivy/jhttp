package codes.laivy.jhttp.response;

import codes.laivy.jhttp.headers.Headers.MutableHeaders;
import codes.laivy.jhttp.message.Message;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.utilities.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface represents an HTTP response.
 *
 * @author Daniel Richard (Laivy)
 * @version 1.0-SNAPSHOT
 */
public interface HttpResponse {

    // Object

    /**
     * Retrieves the raw bytes of this response, which is the purest form of the response data.
     * @return The raw bytes of the response
     */
    byte[] getBytes();

    /**
     * Retrieves the status of this HTTP response
     * @return the version of this response
     */
    @NotNull HttpStatus getStatus();

    /**
     * Retrieves the version of this HTTP response
     * @return the version of this response
     */
    @NotNull HttpVersion getVersion();

    /**
     * Retrieves the headers of this response.
     * @return The headers of the response
     */
    @NotNull MutableHeaders getHeaders();

    /**
     * Retrieves the message, which is the body of the response. It can be null if there is no message.
     * @return The message body of the response
     */
    @Nullable Message getMessage();

}