package codes.laivy.jhttp.element.request;

import codes.laivy.jhttp.authorization.Credentials;
import codes.laivy.jhttp.connection.HttpClient;
import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.element.HttpElement;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.headers.*;
import codes.laivy.jhttp.module.Cookie;
import codes.laivy.jhttp.module.Forwarded;
import codes.laivy.jhttp.module.Origin;
import codes.laivy.jhttp.module.UserAgent;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.*;

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
            final @NotNull Headers headers,
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
                @NotNull HttpRequest that = (HttpRequest) object;
                return Objects.equals(getVersion(), that.getVersion()) && getMethod() == that.getMethod() && Objects.equals(getAuthority(), that.getAuthority()) && Objects.equals(getUri(), that.getUri()) && Objects.equals(getHeaders(), that.getHeaders()) && Objects.equals(getBody(), that.getBody());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getVersion(), getMethod(), getAuthority(), getUri(), getHeaders(), getMethod(), getBody());
            }

            @Override
            public @NotNull String toString() {
                return getVersion().getRequestFactory().serialize(this);
            }

        };
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

    // Getters

    /**
     * Retrieves the authorization credentials specified in the headers, if present.
     *
     * @return the authorization credentials, or {@code null} if not found
     */
    default @Nullable Credentials getAuthorization() {
        return getHeaders().first(HeaderKey.AUTHORIZATION).map(Header::getValue).orElse(null);
    }
    default void setAuthorization(@Nullable Credentials credentials) {
        if (credentials == null) getHeaders().remove(HeaderKey.AUTHORIZATION);
        else getHeaders().put(HeaderKey.AUTHORIZATION.create(credentials));
    }

    /**
     * Retrieves the host specified in the headers.
     *
     * @return the host specified in the headers
     * @throws IllegalStateException if the 'Host' header is not found in the HTTP request
     */
    default @NotNull Host getHost() {
        if (getHeaders().contains(HeaderKey.HOST)) return getHeaders().get(HeaderKey.HOST)[0].getValue();
        else throw new IllegalStateException("Cannot find 'Host' header from HTTP request");
    }
    default void setHost(@NotNull Host host) {
        getHeaders().put(HeaderKey.HOST.create(host));
    }

    /**
     * Retrieves the user agent specified in the headers, if present.
     *
     * @return the user agent specified in the headers, or {@code null} if not found
     */
    default @Nullable UserAgent getUserAgent() {
        return getHeaders().first(HeaderKey.USER_AGENT).map(Header::getValue).orElse(null);
    }
    default void setUserAgent(@Nullable UserAgent agent) {
        if (agent == null) getHeaders().remove(HeaderKey.USER_AGENT);
        else getHeaders().put(HeaderKey.USER_AGENT.create(agent));
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
        return getHeaders().first(HeaderKey.ACCEPT_LANGUAGE).map(Header::getValue).orElse(Wildcard.create(new Weight[0]));
    }
    default void setLocales(@NotNull Wildcard<Weight<Locale>[]> wildcard) {
        if (!wildcard.isWildcard() && wildcard.getValue().length == 0) {
            getHeaders().remove(HeaderKey.ACCEPT_LANGUAGE);
        } else {
            getHeaders().put(HeaderKey.ACCEPT_LANGUAGE.create(wildcard));
        }
    }

    /**
     * Retrieves the accepted encodings from the request headers, if present.
     *
     * @return the accepted encodings, or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    default @NotNull Wildcard<Weight<Deferred<Encoding>>[]> getEncodings() {
        return getHeaders().first(HeaderKey.ACCEPT_ENCODING).map(Header::getValue).orElse(Wildcard.create(new Weight[0]));
    }
    default void setEncodings(@NotNull Wildcard<Weight<Deferred<Encoding>>[]> wildcard) {
        if (!wildcard.isWildcard() && wildcard.getValue().length == 0) {
            getHeaders().remove(HeaderKey.ACCEPT_ENCODING);
        } else {
            getHeaders().put(HeaderKey.ACCEPT_ENCODING.create(wildcard));
        }
    }

    /**
     * Retrieves the forwarded information from the request headers, if present.
     *
     * @return the forwarded information, or {@code null} if not found
     */
    default @Nullable Forwarded getForwarded() {
        return getHeaders().first(HeaderKey.FORWARDED).map(Header::getValue).orElse(null);
    }
    default void setForwarded(@Nullable Forwarded forwarded) {
        if (forwarded == null) getHeaders().remove(HeaderKey.FORWARDED);
        else getHeaders().put(HeaderKey.FORWARDED.create(forwarded));
    }

    /**
     * Retrieves the referrer from the request headers, if present.
     *
     * @return the referrer, or {@code null} if not found
     */
    default @Nullable Origin getReferrer() {
        return getHeaders().first(HeaderKey.REFERER).map(Header::getValue).orElse(null);
    }
    default void setReferrer(@Nullable Origin origin) {
        if (origin == null) getHeaders().remove(HeaderKey.REFERER);
        else getHeaders().put(HeaderKey.REFERER.create(origin));
    }

    // Cookies

    /**
     * Retrieves all cookies from the request headers.
     *
     * @return an array of cookies
     */
    default @NotNull Cookie @NotNull [] getCookies() {
        return getHeaders().first(HeaderKey.COOKIE).map(Header::getValue).orElse(new Cookie[0]);
    }

    /**
     * Retrieves a specific cookie by name from the request headers.
     *
     * @param name the name of the cookie
     * @return an optional containing the cookie, or empty if not found
     */
    default @NotNull Optional<Cookie> getCookie(@NotNull String name) {
        return Arrays.stream(getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

    /**
     * Adds or removes a cookie with the specified name and value to/from the HTTP headers.
     * If the value is {@code null}, the cookie with the specified name will be removed.
     *
     * @param name  the name of the cookie
     * @param value the value of the cookie, or {@code null} to remove the cookie
     */
    default void cookie(@NotNull String name, @Nullable String value) {
        @NotNull Cookie[] cookies;

        if (value != null) {
            // Check if the cookie already exists
            if (getCookie(name).isPresent()) return;

            // Get cookies and add the new one
            @NotNull Cookie cookie = Cookie.create(name, value);

            cookies = getCookies();
            cookies = Arrays.copyOfRange(cookies, 0, cookies.length + 1);
            cookies[cookies.length - 1] = cookie;
        } else {
            @NotNull List<Cookie> list = new ArrayList<>(Arrays.asList(getCookies()));
            list.removeIf(c -> c.getName().equals(name));

            cookies = list.toArray(new Cookie[0]);
        }

        getHeaders().put(HeaderKey.COOKIE.create(cookies));
    }

    // Query and Path

    /**
     * Retrieves the path from the URI.
     *
     * @return the path from the URI, or {@code null} if not present
     */
    default @Nullable String getPath() {
        return StringUtils.isBlank(getUri().getPath()) ? null : getUri().getPath();
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
        @NotNull Headers getHeaders();

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

    }

}