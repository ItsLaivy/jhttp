package codes.laivy.jhttp.headers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Arrays;

public interface Header<T> {

    @NotNull HeaderKey<T> getKey();
    @UnknownNullability T getValue();

    default @NotNull String getName() {
        return getKey().getName();
    }

    @Override
    boolean equals(@Nullable Object o);
    @Override
    int hashCode();

    static <E> @NotNull Header<E> create(final @NotNull HeaderKey<E> key, final @UnknownNullability E value) {
        return key.create(value);
    }

    /**
     * The Type enum categorizes various HTTP headers based on their functionality.
     * Each enum constant represents a category of headers used in HTTP requests and responses.
     * These categories help in organizing and understanding the purpose of each header type.
     *
     * @see <a href="https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers">MDN Web Docs - HTTP Headers</a>
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    enum Type {
        /**
         * Headers related to authentication and authorization.
         * These headers are used to identify the user or client making the request and to provide credentials for access control.
         */
        AUTHENTICATION("WWW-Authenticate", "Authorization", "Proxy-Authenticate", "Proxy-Authorization"),

        /**
         * Headers used for caching mechanisms.
         * These headers control the storage and retrieval of cached content, affecting performance and freshness of resources.
         */
        CACHING("Age", "Cache-Control", "Expires", "Pragma", "Warning"),

        /**
         * Headers for client hints.
         * These headers allow servers to provide hints to the client to optimize resource delivery based on client capabilities and preferences.
         */
        CLIENT_HINT("Accept-CH", "Accept-CH-Lifetime", "Content-DPR", "DPR", "Device-Memory", "Save-Data", "Viewport-Width", "Width"),

        /**
         * Headers for conditional requests.
         * These headers make the request conditional, usually based on the state of the resource or the client's cached version of the resource.
         */
        CONDITIONAL("Last-Modified", "ETag", "If-Match", "If-None-Match", "If-Modified-Since", "If-Unmodified-Since"),

        /**
         * Headers that manage connection control.
         * These headers control the connection's persistence and the management of multiple requests over a single connection.
         */
        CONNECTION("Connection", "Keep-Alive"),

        /**
         * Headers used for content negotiation.
         * These headers allow the client to specify its preferences for the type, charset, encoding, and language of the response content.
         */
        CONTENT_NEGOTIATION("Accept", "Accept-Charset", "Accept-Encoding", "Accept-Language"),

        /**
         * Headers that control the behavior of HTTP requests.
         * These headers define expectations and forwarding limits for requests.
         */
        CONTROL("Expect", "Max-Forwards"),

        /**
         * Headers for managing cookies.
         * These headers handle the storage and transmission of cookies, which are used for stateful interactions between the client and server.
         */
        COOKIE("Cookie", "Set-Cookie", "Cookie2", "Set-Cookie2"),

        /**
         * Headers for Cross-Origin Resource Sharing (CORS).
         * These headers manage resource sharing across different origins, controlling access and permissions for cross-origin requests.
         */
        CROSS_ORIGIN("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Access-Control-Allow-Headers", "Access-Control-Allow-Methods", "Access-Control-Expose-Headers", "Access-Control-Max-Age", "Access-Control-Request-Headers", "Access-Control-Request-Method", "Origin", "Timing-Allow-Origin"),

        /**
         * Headers for tracking user preferences.
         * These headers are used to indicate the user's tracking preferences, such as enabling or disabling tracking.
         */
        DO_NOT_TRACK("DNT", "Tk"),

        /**
         * Headers related to downloads.
         * These headers provide information about how content should be displayed or handled when downloaded.
         */
        DOWNLOADS("Content-Disposition"),

        /**
         * Headers related to content properties.
         * These headers describe the size, type, encoding, language, and location of the content in the HTTP message.
         */
        CONTENT("Content-Length", "Content-Type", "Content-Encoding", "Content-Language", "Content-Location"),

        /**
         * Headers for routing requests through proxies.
         * These headers contain information about proxies that have forwarded the request, including client and server addresses.
         */
        ROOTING("Forwarded", "X-Forwarded-For", "X-Forwarded-Host", "X-Forwarded-Proto", "Via"),

        /**
         * Headers for redirection.
         * These headers provide information about the new location of a resource when a client needs to be redirected.
         */
        REDIRECT("Location"),

        /**
         * Headers that provide context about the request.
         * These headers include information about the client, such as the user agent, host, and referrer.
         */
        REQUEST_CONTEXT("From", "Host", "Referer", "Referrer-Policy", "User-Agent"),

        /**
         * Headers that provide context about the response.
         * These headers include information about the server and the capabilities it supports.
         */
        RESPONSE_CONTEXT("Allow", "Server"),

        /**
         * Headers used for specifying and handling ranges of data.
         * These headers are used for partial requests and responses, such as requesting or sending parts of a file.
         */
        RANGE("Accept-Ranges", "Range", "If-Range", "Content-Range"),

        /**
         * Headers related to security policies and enforcement.
         * These headers help protect resources from attacks and enforce security policies like content security and transport security.
         */
        SECURITY("Content-Security-Policy", "Content-Security-Policy-Report-Only", "Public-Key-Pins", "Public-Key-Pins-Report-Only", "Strict-Transport-Security", "Upgrade-Insecure-Requests", "X-Content-Type-Options", "X-Frame-Options", "X-XSS-Protection"),

        /**
         * Headers used for server-sent events. (Deprecated)
         * These headers were used for managing server-to-client notifications, now largely replaced by WebSockets and other technologies.
         */
        @Deprecated
        SERVER_EVENTS("Ping-From", "Ping-To", "Last-Event-ID"),

        /**
         * Headers related to transfer coding.
         * These headers are used to specify and manage different encoding methods for the message body, such as chunked transfer encoding.
         */
        TRANSFER_CODING("Transfer-Encoding", "TE", "Trailer"),

        /**
         * Headers used for WebSocket connections.
         * These headers manage the establishment and control of WebSocket connections, including keys and protocols.
         */
        WEBSOCKET("Sec-WebSocket-Key", "Sec-WebSocket-Extensions", "Sec-WebSocket-Accept", "Sec-WebSocket-Protocol", "Sec-WebSocket-Version"),

        /**
         * This enum value represents headers in HTTP that are hop-by-hop, meaning they are consumed and processed
         * by each intermediary node (e.g., proxies or gateways) in the communication path.
         * These headers are not passed
         * to the next node in the transmission chain
         * and have a specific behavior defined by the HTTP protocol.
         * <p>
         * Hop-by-hop headers are distinct from end-to-end headers,
         * which are transmitted unchanged from the origin to the final destination.
         * End-to-end headers contain information relevant to the entire request-response cycle and are
         * preserved by each intermediary node.
         * <p>
         * The headers listed in this enum are predefined hop-by-hop headers according to the HTTP RFC standards
         * (RFC 7230 and related RFCs).
         * These headers have specific semantics and are processed differently from other headers in the HTTP message.
         * <p>
         * Hop-by-hop headers are critical for managing the behavior of intermediary nodes, controlling aspects such as
         * connection persistence, authentication, content encoding, and protocol upgrades.
         * They play a crucial role in
         * ensuring the integrity and efficiency of HTTP communication across distributed networks.
         */
        HOP_BY_HOP("Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization", "TE", "Trailers", "Transfer-Encoding", "Upgrade"),
        ;

        private final @NotNull String[] headers;

        Type(@NotNull String @NotNull ... headers) {
            this.headers = headers;
        }

        /**
         * Retrieves the array of header keys for the current type.
         *
         * @return an array of HeaderKey objects representing the headers for the current type
         */
        public @NotNull HeaderKey<?> @NotNull [] getHeaders() {
            return Arrays.stream(headers).map(HeaderKey::retrieve).toArray(HeaderKey[]::new);
        }

        /**
         * Checks if the specified header key matches any header in the current type.
         *
         * @param key the HeaderKey to be checked
         * @return {@code true} if the key matches any header in the current type, {@code false} otherwise
         */
        public boolean matches(@NotNull HeaderKey<?> key) {
            return Arrays.stream(headers).anyMatch(name -> key.getName().equalsIgnoreCase(name));
        }

    }

}
