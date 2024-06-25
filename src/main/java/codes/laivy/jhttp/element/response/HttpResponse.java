package codes.laivy.jhttp.element.response;

import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.element.HttpElement;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.headers.Headers;
import codes.laivy.jhttp.module.Cookie.Request;
import codes.laivy.jhttp.module.UserAgent.Product;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * This interface represents an HTTP response.
 *
 * @author Daniel Richard (Laivy)
 * @version 1.0-SNAPSHOT
 */
public interface HttpResponse extends HttpElement {

    // Static initializers

    static @NotNull HttpResponse create(
            @NotNull HttpStatus status,
            @NotNull HttpVersion version,
            @NotNull Headers headers,
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
            public @NotNull Headers getHeaders() {
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

    // Getters

    default @Nullable Product getServer() {
        return getHeaders().first(HeaderKey.SERVER).map(Header::getValue).orElse(null);
    }
    default void setServer(@Nullable Product product) {
        if (product != null) getHeaders().put(HeaderKey.SERVER.create(product));
        else getHeaders().remove(HeaderKey.SERVER);
    }

    // Cookies

    default @NotNull Request @NotNull [] getCookies() {
        return Arrays.stream(getHeaders().get(HeaderKey.SET_COOKIE)).map(Header::getValue).toArray(Request[]::new);
    }
    default @NotNull Optional<Request> getCookie(@NotNull String name) {
        return Arrays.stream(getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

    default void cookie(@NotNull Request request) {
        // Remove the old cookie request if already exists
        for (@NotNull Header<Request> header : getHeaders().get(HeaderKey.SET_COOKIE)) {
            if (header.getValue().getName().equals(request.getName())) {
                getHeaders().remove(header);
            }
        }

        // Add header
        getHeaders().add(HeaderKey.SET_COOKIE.create(request));
    }

}