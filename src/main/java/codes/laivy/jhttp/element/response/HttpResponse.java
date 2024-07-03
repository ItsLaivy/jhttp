package codes.laivy.jhttp.element.response;

import codes.laivy.jhttp.client.HttpClient;
import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.element.HttpElement;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.Target;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.headers.Headers;
import codes.laivy.jhttp.module.UserAgent.Product;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

/**
 * This interface represents an HTTP response.
 *
 * @author Daniel Richard (Laivy)
 * @version 1.0-SNAPSHOT
 */
public interface HttpResponse extends HttpElement {

    // Static initializers

    static @NotNull HttpResponse create(
            @NotNull HttpVersion version,
            @NotNull HttpStatus status,
            @Nullable HttpBody body
    ) {
        return create(version, status, version.getHeaderFactory().createMutable(Target.RESPONSE), body);
    }
    static @NotNull HttpResponse create(
            @NotNull HttpVersion version,
            @NotNull HttpStatus status,
            @NotNull Headers headers,
            @Nullable HttpBody body
    ) {
        return new HttpResponse() {

            // Object

            @Override
            public @NotNull HttpVersion getVersion() {
                return version;
            }
            @Override
            public @NotNull HttpStatus getStatus() {
                return status;
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
                return getVersion().getResponseFactory().serialize(this);
            }

        };
    }

    // Object

    /**
     * Retrieves the status of this HTTP response
     * @return the version of this response
     */
    @NotNull HttpStatus getStatus();

    default @Nullable Product getServer() {
        return getHeaders().first(HeaderKey.SERVER).map(Header::getValue).orElse(null);
    }

    // Classes

    /**
     * A future representing the response of an HTTP response.
     * This interface provides methods
     * to access various details of the HTTP response.
     *
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    // todo: #isChunked method
    interface Future extends java.util.concurrent.Future<@NotNull HttpResponse> {

        /**
         * Retrieves the HttpClient associated with this response future.
         *
         * @return The HttpClient associated with this response. Never null.
         */
        @NotNull HttpClient getClient();

        /**
         * Retrieves the version of this response future.
         *
         * @return The {@link HttpVersion} representing the version of this future. Never null.
         */
        @NotNull HttpVersion getVersion();

        /**
         * Retrieves the status of this HTTP response future.
         *
         * @return The {@link HttpStatus} representing the status of this future. Never null.
         */
        @NotNull HttpStatus getStatus();

        /**
         * Gets the headers from this HTTP response future.
         *
         * @return The headers of this HTTP response future. Never null.
         */
        @NotNull Headers getHeaders();

        /**
         * Returns the raw HTTP response as a string. The difference between this method and {@link #toString()}
         * is that {@link #toString()} represents the string representation of the Future itself, while this method
         * returns the actual HTTP response.
         * <p>
         * If the Future is not completed, this method will return a fragmented HTTP response.
         *
         * @return The raw HTTP response as a string. Never null.
         */
        @NotNull String getAsString();

        @Override
        @NotNull String toString();

        // Future

        /**
         * Attaches the given action to be invoked when this future completes.
         * The action is executed with the result of the HTTP response and any throwable
         * that was thrown during the response execution. The action will be run after
         * the HTTP response completes, either successfully or with an error.
         *
         * <p>The action is provided with two parameters:
         * <ul>
         *   <li>The {@link HttpResponse} object representing the completed HTTP response.</li>
         *   <li>A {@link Throwable} object representing any error that occurred, or {@code null} if the response completed successfully.</li>
         * </ul>
         *
         * <p>This method is useful for performing additional operations or cleanup
         * after the response is complete, regardless of the outcome.
         *
         * @param action the action to be executed when the future completes
         * @return a {@link Future} that represents the result of the action
         * @throws NullPointerException if the specified action is {@code null}
         */
        @NotNull Future whenComplete(@NotNull BiConsumer<? super HttpResponse, ? super Throwable> action);

        /**
         * Specifies a timeout for the HTTP response. If the response does not complete
         * within the given duration, the future will be completed exceptionally with
         * a {@link TimeoutException}.
         *
         * <p>The duration parameter defines the maximum time to wait for the response to complete.
         * If the duration elapses before the response completes, the future is automatically
         * canceled and a {@link TimeoutException} is thrown.
         *
         * <p>This method is useful for ensuring that the response does not the app froze indefinitely
         * and provides a way to handle long-running requests.
         *
         * @param duration the maximum time to wait for the response to complete
         * @return a {@link Future} that represents the result of the timeout operation
         * @throws NullPointerException if the specified duration is {@code null}
         */
        @NotNull Future orTimeout(@NotNull Duration duration);

    }


}