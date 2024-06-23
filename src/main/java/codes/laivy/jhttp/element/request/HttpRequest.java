package codes.laivy.jhttp.element.request;

import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.element.HttpElement;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.headers.Headers.MutableHeaders;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.url.URIAuthority;
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
public interface HttpRequest extends HttpElement {

    // Static initializers

    static @NotNull HttpRequest create(
            final @NotNull HttpVersion version,
            final @NotNull Method method,
            final @Nullable URIAuthority authority,
            final @NotNull URI uri,
            final @NotNull MutableHeaders headers,
            final @Nullable HttpBody body
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
            public @Nullable HttpBody getBody() {
                return body;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull HttpRequest that = (HttpRequest) object;
                return Objects.equals(getVersion(), that.getVersion()) && getMethod() == that.getMethod() && Objects.equals(getAuthority(), that.getAuthority()) && Objects.equals(getUri(), that.getUri()) && Objects.equals(getHeaders(), that.getHeaders()) && Objects.equals(getBody(), that.getBody());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getVersion(), getMethod(), getAuthority(), getUri(), getHeaders(), getMethod(), getBody());
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
     * Retrieves the authority of the request, which can be null.
     * @return The authority of the request
     */
    @Nullable URIAuthority getAuthority();

    /**
     * Retrieves the URI path of this request.
     * @return The URI path of the request
     */
    @NotNull URI getUri();

}