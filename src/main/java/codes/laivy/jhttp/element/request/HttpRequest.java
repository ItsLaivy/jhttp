package codes.laivy.jhttp.element.request;

import codes.laivy.jhttp.authorization.Credentials;
import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.client.HttpClient;
import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.element.FormData;
import codes.laivy.jhttp.element.HttpElement;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.Target;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.headers.*;
import codes.laivy.jhttp.media.Content;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.media.form.FormUrlEncodedMediaType;
import codes.laivy.jhttp.media.form.MultipartFormDataMediaType;
import codes.laivy.jhttp.module.Forwarded;
import codes.laivy.jhttp.module.Location;
import codes.laivy.jhttp.module.UserAgent;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

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
            final @NotNull HttpBody body
    ) {
        return create(version, method, authority, uri, version.getHeaderFactory().createMutable(Target.REQUEST), body);
    }
    static @NotNull HttpRequest create(
            final @NotNull HttpVersion version,
            final @NotNull Method method,
            final @Nullable URIAuthority authority,
            final @NotNull URI uri,
            final @NotNull HttpHeaders headers,
            final @NotNull HttpBody body
    ) {
        return version.getRequestFactory().create(method, authority, uri, headers, body);
    }

    // Object

    /**
     * Retrieves the method of the request
     * @return The method of the request
     */
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

    // Modules

    default @Nullable Credentials getAuthorization() {
        return getHeaders().first(HttpHeaderKey.AUTHORIZATION).map(HttpHeader::getValue).orElse(null);
    }
    default void setAuthorization(@Nullable Credentials value) {
        if (value != null) getHeaders().put(HttpHeaderKey.AUTHORIZATION.create(value));
        else getHeaders().remove(HttpHeaderKey.AUTHORIZATION);
    }

    /**
     * Retrieves the host specified in the headers.
     *
     * @return the host specified in the headers
     * @throws IllegalStateException if the 'Host' header is not found in the HTTP request
     */
    default @NotNull Host getHost() {
        if (getHeaders().contains(HttpHeaderKey.HOST)) return getHeaders().get(HttpHeaderKey.HOST)[0].getValue();
        else throw new IllegalStateException("Cannot find 'Host' header from HTTP request");
    }

    /**
     * Retrieves the user agent specified in the headers, if present.
     *
     * @return the user agent specified in the headers, or {@code null} if not found
     */
    default @Nullable UserAgent getUserAgent() {
        return getHeaders().first(HttpHeaderKey.USER_AGENT).map(HttpHeader::getValue).orElse(null);
    }

    /**
     * Retrieves the preferred locale from the request headers.
     *
     * @return the preferred locale
     */
    default @NotNull Locale getLocale() {
        @NotNull Wildcard<Weight<Locale>[]> locales = getLocales();

        if (locales.getValue().length > 0) {
            return Arrays.stream(locales.getValue())
                    .sorted(Comparator.comparingDouble(o -> o.getWeight() != null ? o.getWeight() : Double.MIN_VALUE))
                    .map(Weight::getValue)
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
        }

        return Locale.getDefault();
    }

    /**
     * Retrieves the accepted locales from the request headers.
     *
     * @return the accepted locales
     */
    @SuppressWarnings("unchecked")
    default @NotNull Wildcard<Weight<Locale>[]> getLocales() {
        return getHeaders().first(HttpHeaderKey.ACCEPT_LANGUAGE).map(HttpHeader::getValue).orElse(Wildcard.create(new Weight[0]));
    }

    /**
     * Retrieves the accepted encodings from the request headers, if present.
     *
     * @return the accepted encodings, or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    default @NotNull Wildcard<Weight<Deferred<Encoding>>[]> getEncodings() {
        return getHeaders().first(HttpHeaderKey.ACCEPT_ENCODING).map(HttpHeader::getValue).orElse(Wildcard.create(new Weight[0]));
    }

    /**
     * Retrieves the forwarded information from the request headers, if present.
     *
     * @return the forwarded information, or {@code null} if not found
     */
    default @Nullable Forwarded getForwarded() {
        return getHeaders().first(HttpHeaderKey.FORWARDED).map(HttpHeader::getValue).orElse(null);
    }

    /**
     * Retrieves the referrer from the request headers, if present.
     *
     * @return the referrer, or {@code null} if not found
     */
    default @Nullable Location getReferrer() {
        return getHeaders().first(HttpHeaderKey.REFERER).map(HttpHeader::getValue).orElse(null);
    }

    // Post Data

    /**
     * Retrieves the form data associated with the request.
     * <p>
     * The form data is represented as an array of {@link FormData} objects.
     *
     * @return an array of {@link FormData} objects associated with the POST request, or null if there's no form data available
     */
    @SuppressWarnings("unchecked")
    default @NotNull FormData @Nullable [] getFormData() {
        @NotNull Optional<MediaType<?>> optional = getHeaders().first(HttpHeaderKey.CONTENT_TYPE).map(HttpHeader::getValue);

        if (!optional.isPresent()) {
            return null;
        } else try {
            @Nullable MediaType<?> media = optional.get();

            if (!media.getType().equals(FormUrlEncodedMediaType.TYPE) && !media.getType().equals(MultipartFormDataMediaType.TYPE)) {
                return null;
            }

            // Finish
            @NotNull Content<FormData[]> content = getBody().getContent(((MediaType<FormData[]>) media));
            return content.getData();
        } catch (@NotNull MediaParserException | @NotNull IOException e) {
            throw new IllegalArgumentException("cannot parse form data content from body", e);
        }
    }

    /**
     * Retrieves a specific form data item by its name from the POST request as example.
     * This method provides a convenient way to find a form data item with the specified name.
     * <p>
     * If the specified name is not found, an empty {@link Optional} is returned.
     *
     * @param name the name of the form data item to retrieve, must not be null.
     * @return an {@link Optional} containing the {@link FormData} item with the specified name, or an empty {@link Optional} if not found.
     * @throws IllegalStateException if the request is not a POST request.
     */
    default @NotNull Optional<FormData> getFormData(@NotNull String name) {
        @NotNull FormData @Nullable [] data = getFormData();

        if (data != null) {
            return Arrays.stream(data)
                    .filter(post -> post.getName().equalsIgnoreCase(name))
                    .findFirst();
        } else {
            throw new IllegalStateException("there's no form data into the request available to read");
        }
    }

    // Query and Path

    /**
     * Retrieves the path from the URI.
     *
     * @return the path from the URI
     */
    default @NotNull String getPath() {
        return getUri().getPath();
    }

    /**
     * Retrieves a query parameter from the URI.
     *
     * @param name the name of the query parameter
     * @return an optional containing the query parameter value, or empty if not found
     */
    default @NotNull Optional<String> getQuery(@NotNull String name) {
        return Optional.ofNullable(getQueries().getOrDefault(name, null));
    }

    /**
     * Retrieves all query parameters from the URI.
     *
     * @return a map containing all query parameters
     */
    default @NotNull Map<String, String> getQueries() {
        return StringUtils.readQueryParams(getUri());
    }

    // Classes

    /**
     * A class that constructs an HTTP request.
     * This class is part of the HttpRequestFactory.
     * It is essential for handling cases where the client sends requests with fragmented or chunked encrypted messages.
     * <p>
     * When you initiate a parsing process using the HttpRequestFactory, it returns this Future containing
     * the mandatory information at the start of the request, including the client, method, authority, and URI.
     * The body is not included as it can be fragmented for various reasons.
     * <p>
     * Whenever the client sends a new message, it should send this new message to the factory again.
     * The factory
     * will get this Future, completing it if the response is finally concluded.
     */
    // todo: #isChunked method
    interface Future extends java.util.concurrent.Future<@NotNull HttpRequest> {

        /**
         * Retrieves the HttpClient associated with this request future.
         *
         * @return The HttpClient associated with this request. Never null.
         */
        @NotNull HttpClient getClient();

        /**
         * Retrieves the version of this request future.
         *
         * @return the {@link HttpVersion} representing the version of this future.
         */
        @NotNull HttpVersion getVersion();

        /**
         * Retrieves the method of the request
         * @return The method of the request
         */
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

        /**
         * Gets the headers from this http request future
         * @return The header list of this http request future
         */
        @NotNull
        HttpHeaders getHeaders();

        /**
         * Returns the raw HTTP request as a string.
         * The difference between this method and {@link #toString()}
         * is that {@link #toString()} represents the string representation of the Future itself,
         * while this method
         * returns the actual HTTP request.
         * <p>
         * If the Future is not completed, this method will return a fragmented HTTP request.
         *
         * @return The raw HTTP request as a string. Never null.
         */
        @NotNull String getAsString();

        @Override
        @NotNull String toString();

        // Future

        /**
         * Attaches the given action to be invoked when this future completes.
         * The action is executed with the result of the HTTP request and any throwable
         * that was thrown during the request execution. The action will be run after
         * the HTTP request completes, either successfully or with an error.
         *
         * <p>The action is provided with two parameters:
         * <ul>
         *   <li>The {@link HttpRequest} object representing the completed HTTP request.</li>
         *   <li>A {@link Throwable} object representing any error that occurred, or {@code null} if the request completed successfully.</li>
         * </ul>
         *
         * <p>This method is useful for performing additional operations or cleanup
         * after the request is complete, regardless of the outcome.
         *
         * @param action the action to be executed when the future completes
         * @return a {@link Future} that represents the result of the action
         * @throws NullPointerException if the specified action is {@code null}
         */
        @NotNull Future whenComplete(@NotNull BiConsumer<? super HttpRequest, ? super Throwable> action);

        /**
         * Specifies a timeout for the HTTP request. If the request does not complete
         * within the given duration, the future will be completed exceptionally with
         * a {@link TimeoutException}.
         *
         * <p>The duration parameter defines the maximum time to wait for the request to complete.
         * If the duration elapses before the request completes, the future is automatically
         * canceled and a {@link TimeoutException} is thrown.
         *
         * <p>This method is useful for ensuring that the request does not the app froze indefinitely
         * and provides a way to handle long-running requests.
         *
         * @param duration the maximum time to wait for the request to complete
         * @return a {@link Future} that represents the result of the timeout operation
         * @throws NullPointerException if the specified duration is {@code null}
         */
        @NotNull Future orTimeout(@NotNull Duration duration);

    }

}