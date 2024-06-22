package codes.laivy.jhttp.element.response;

import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.headers.Headers.MutableHeaders;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.element.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * This interface represents an HTTP response.
 *
 * @author Daniel Richard (Laivy)
 * @version 1.0-SNAPSHOT
 */
public interface HttpResponse {

    // Static initializers

    static @NotNull HttpResponse create(
            @NotNull HttpStatus status,
            @NotNull HttpVersion version,
            @NotNull MutableHeaders headers,
            @Nullable HttpBody body
    ) {
        return new HttpResponse() {

            // Object

            @Override
            public @NotNull HttpStatus getStatus() {
                return status;
            }
            @Override
            public @NotNull HttpVersion getVersion() {
                return version;
            }
            @Override
            public @NotNull MutableHeaders getHeaders() {
                return headers;
            }
            @Override
            public @Nullable HttpBody getBody() {
                return body;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull HttpResponse that = (HttpResponse) object;
                return Objects.equals(getStatus(), that.getStatus()) && Objects.equals(getVersion(), that.getVersion()) && Objects.equals(getHeaders(), that.getHeaders()) && Objects.equals(getBody(), that.getBody());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getStatus(), getVersion(), getHeaders(), getBody());
            }

            @Override
            public @NotNull String toString() {
                return getVersion().getFactory().getResponse().wrap(this);
            }

        };
    }

    // Object

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
     * Retrieves the body of the response. It can be null if there is no' body, or it's empty.
     * @return The message body of the response
     */
    @Nullable
    HttpBody getBody();

}