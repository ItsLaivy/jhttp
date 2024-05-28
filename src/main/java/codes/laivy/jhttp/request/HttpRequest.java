package codes.laivy.jhttp.request;

import codes.laivy.jhttp.headers.Headers.MutableHeaders;
import codes.laivy.jhttp.message.Message;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.utilities.Method;
import codes.laivy.jhttp.utilities.URIAuthority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

/**
 * This interface represents an HTTP request.
 *
 * @author Daniel Richard (Laivy)
 * @version 1.0-SNAPSHOT
 */
public interface HttpRequest {

    // Object

    /**
     * Retrieves the raw bytes of this request, which is the purest form of the request data.
     * @return The raw bytes of the request
     */
    byte[] getBytes();

    @NotNull Method getMethod();

    /**
     * Retrieves the version of this HTTP request
     * @return the version of this request
     */
    @NotNull HttpVersion getVersion();

    /**
     * Retrieves the authority of the request, which can be null.
     * @return The authority of the request
     */
    @Nullable URIAuthority getAuthority();

    /**
     * Retrieves the URI path of this request.
     * @return The URI path of the request
     */
    @NotNull URI getUri();

    /**
     * Retrieves the headers of this request.
     * @return The headers of the request
     */
    @NotNull MutableHeaders getHeaders();

    /**
     * Retrieves the message, which is the body of the request. It can be null if there is no message.
     * @return The message body of the request
     */
    @Nullable Message getMessage();

}