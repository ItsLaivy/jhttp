package codes.laivy.jhttp.request;

import codes.laivy.jhttp.headers.Headers.MutableHeaders;
import codes.laivy.jhttp.message.Message;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.utilities.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

/**
 * This interface represents an HTTP request.
 *
 * @author Daniel Richard (Laivy)
 * @version 1.0-SNAPSHOT
 */
public interface HttpRequest {

    // Static initializers

    static @NotNull HttpRequest create(
            final @NotNull HttpVersion version,
            final @NotNull Method method,
            final @Nullable URIAuthority authority,
            final @NotNull URI uri,
            final @NotNull MutableHeaders headers,
            final @Nullable Message message
    ) {
        return new HttpRequest() {

            // Object

            @Override
            public @NotNull HttpVersion getVersion() {
                return version;
            }
            @Override
            public @NotNull Method getMethod() {
                return method;
            }
            @Override
            public @Nullable URIAuthority getAuthority() {
                return authority;
            }
            @Override
            public @NotNull URI getUri() {
                return uri;
            }
            @Override
            public @NotNull MutableHeaders getHeaders() {
                return headers;
            }
            @Override
            public @Nullable Message getMessage() {
                return message;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull HttpRequest that = (HttpRequest) object;
                return Objects.equals(getVersion(), that.getVersion()) && getMethod() == that.getMethod() && Objects.equals(getAuthority(), that.getAuthority()) && Objects.equals(getUri(), that.getUri()) && Objects.equals(getHeaders(), that.getHeaders()) && Objects.equals(getMessage(), that.getMessage());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getVersion(), getMethod(), getAuthority(), getUri(), getHeaders(), getMethod());
            }

            @Override
            public @NotNull String toString() {
                return getVersion().getFactory().getRequest().wrap(this);
            }

        };
    }

    // Object

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