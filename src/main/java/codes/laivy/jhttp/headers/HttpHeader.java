package codes.laivy.jhttp.headers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Arrays;

public interface HttpHeader<T> extends Cloneable {

    @NotNull HttpHeaderKey<T> getKey();
    @UnknownNullability T getValue();

    default @NotNull String getName() {
        return getKey().getName();
    }

    @Override
    boolean equals(@Nullable Object o);
    @Override
    int hashCode();

    @NotNull
    HttpHeader<T> clone();

    static <E> @NotNull HttpHeader<E> create(final @NotNull HttpHeaderKey<E> key, final @UnknownNullability E value) {
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
         * <p>
         * Client hints are HTTP headers used by servers to gather information about the client's device capabilities and preferences.
         * These headers enable servers to optimize resource delivery based on the specific requirements and limitations of the client's device,
         * resulting in improved performance and user experience.
         * <p>
         * By using client hints, servers can dynamically adapt the content they send to users, providing more relevant and efficient responses.
         * This can help in reducing bandwidth usage, improving load times, and ensuring that the content is appropriately tailored to the user's context.
         * <p>
         * The following headers are classified as client hints:
         * <ul>
         *     <li><b>Accept-CH</b>: Instructs the client to send specific client hint headers in subsequent requests.</li>
         *     <li><b>Accept-CH-Lifetime</b>: Indicates the duration (in seconds) that the client should remember and send the specified client hints.</li>
         *     <li><b>Content-DPR</b>: Specifies the device pixel ratio of the client device that should be used when fetching resources.</li>
         *     <li><b>DPR</b>: Device Pixel Ratio, indicates the pixel density of the client device.</li>
         *     <li><b>Device-Memory</b>: Indicates the amount of device memory the client has, in gigabytes.</li>
         *     <li><b>Save-Data</b>: A boolean value that indicates whether the user has enabled a reduced data usage mode in their browser.</li>
         *     <li><b>Viewport-Width</b>: Indicates the width of the client's viewport in CSS pixels.</li>
         *     <li><b>Width</b>: Specifies the desired width of the resource being requested, in physical pixels.</li>
         * </ul>
         * <p>
         * Example usage of client hints:
         * <pre>
         * {@code
         * HTTP/1.1 200 OK
         * Accept-CH: DPR, Width, Viewport-Width
         * Accept-CH-Lifetime: 86400
         * Content-DPR: 2.0
         * }
         * </pre>
         * In this example, the server instructs the client to send the DPR, Width, and Viewport-Width headers in subsequent requests.
         * The client should remember these hints for 86400 seconds (24 hours). The Content-DPR header specifies that the resource
         * being returned is optimized for a device pixel ratio of 2.0.
         */
        CLIENT_HINT("Accept-CH", "Accept-CH-Lifetime", "Content-DPR", "DPR", "Device-Memory", "Save-Data", "Viewport-Width", "Width", "Downlink", "ECT", "RTT", "Sec-CH-UA", "Sec-CH-UA-Arch", "Sec-CH-UA-Bitness", "Sec-CH-UA-Full-Version-List", "Sec-CH-UA-Full-Version", "Sec-CH-UA-Mobile", "Sec-CH-UA-Model", "Sec-CH-UA-Platform", "Sec-CH-UA-Platform-Version", "Sec-CH-Prefers-Color-Scheme", "Sec-CH-Prefers-Reduced-Motion"),

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
        ROOTING("Forwarded", "X-Forwarded-For", "X-Forwarded-Host", "X-Forwarded-Proto", "Via", "Host"),

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

        /**
         * Enum representing headers in HTTP that are forbidden to be set in certain contexts.
         * <p>
         * Forbidden header names are restricted from being set in HTTP requests made by JavaScript
         * using the XMLHttpRequest or fetch() API in the browser to prevent security vulnerabilities
         * such as Cross-Site Scripting (XSS).
         * <p>
         * The headers listed in this enum are commonly recognized as forbidden header names according to
         * browser security policies. Attempting to set these headers using JavaScript may result in the
         * browser ignoring or replacing the provided values to ensure user security and the integrity
         * of the HTTP protocol.
         * <p>
         * These headers are restricted to prevent manipulation of critical HTTP request attributes
         * that could otherwise be exploited to perform attacks or circumvent security measures.
         * <p>
         * The following headers are classified as forbidden names:
         * <ul>
         *     <li><b>Accept-Charset</b>: Specifies the character encodings that the client is willing to accept.</li>
         *     <li><b>Accept-Encoding</b>: Specifies the content encodings that the client is willing to accept.</li>
         *     <li><b>Access-Control-Request-Headers</b>: Indicates which HTTP headers can be used when making the actual request.</li>
         *     <li><b>Access-Control-Request-Method</b>: Indicates which HTTP method can be used when making the actual request.</li>
         *     <li><b>Connection</b>: Controls whether the network connection stays open after the current transaction finishes.</li>
         *     <li><b>Content-Length</b>: Specifies the size of the request or response body in bytes.</li>
         *     <li><b>Cookie</b>: Contains stored HTTP cookies previously sent by the server with Set-Cookie headers.</li>
         *     <li><b>Cookie2</b>: Similar to Cookie but used in an obsolete version of the HTTP protocol.</li>
         *     <li><b>Date</b>: Contains the date and time at which the message was sent.</li>
         *     <li><b>DNT</b>: Do Not Track, a request for the server to disable tracking of the user.</li>
         *     <li><b>Expect</b>: Indicates that particular server behaviors are required by the client.</li>
         *     <li><b>Host</b>: Specifies the domain name of the server and (optionally) the TCP port number on which the server is listening.</li>
         *     <li><b>Keep-Alive</b>: Controls the persistence of the network connection.</li>
         *     <li><b>Origin</b>: Indicates the origin of the cross-origin request.</li>
         *     <li><b>Referer</b>: Contains the address of the previous web page from which a link to the current request URL was followed.</li>
         *     <li><b>TE</b>: Indicates the transfer encodings the user agent is willing to accept.</li>
         *     <li><b>Trailer</b>: Indicates that the message body is terminated by a set of header fields.</li>
         *     <li><b>Transfer-Encoding</b>: Specifies the form of encoding used to safely transfer the payload body to the user.</li>
         *     <li><b>Upgrade</b>: Used to switch protocols.</li>
         *     <li><b>Via</b>: Added by proxies, both forward and reverse proxies, and can appear in the request headers and the response headers.</li>
         * </ul>
         * Additionally, headers that start with "Proxy-" or "Pec-" (case-insensitive) are considered forbidden:
         * <ul>
         *     <li><b>Proxy-</b>: Headers starting with "Proxy-" are reserved for proxy-related operations.</li>
         *     <li><b>Sec-</b>: Headers starting with "Sec-" are reserved for security-related operations.</li>
         * </ul>
         */
        FORBIDDEN_NAME("Accept-Charset", "Accept-Encoding", "Access-Control-Request-Headers", "Access-Control-Request-Method", "Connection", "Content-Length", "Cookie", "Cookie2", "Date", "DNT", "Expect", "Host", "Keep-Alive", "Origin", "Referer", "TE", "Trailer", "Transfer-Encoding", "Upgrade", "Via") {
            @Override
            public boolean matches(@NotNull HttpHeaderKey<?> key) {
                return super.matches(key) || key.getName().toLowerCase().startsWith("proxy-") || key.getName().toLowerCase().startsWith("sec-");
            }
        },

        /**
         * Enum representing CORS-satellited request headers, also known as "simple headers".
         * These headers can be sent in a CORS request without triggering a preflight OPTIONS request.
         * <p>
         * CORS (Cross-Origin Resource Sharing) is a mechanism that allows web applications to request resources
         * from a different domain than the one that served the web page.
         * This is commonly used for APIs.
         * <p>
         * Simple headers are considered safe because their values have limited impact on the request's behavior
         * and do not pose significant security risks.
         * They are restricted to a specific set of headers that are
         * commonly used and deemed secure for inclusion in CORS requests.
         * <p>
         * The following headers are classified as simple headers:
         *
         * <ul>
         *     <li><b>Accept</b>: Indicates the media types that the client can understand.</li>
         *     <li><b>Accept-Language</b>: Indicates the natural languages that the client prefers.</li>
         *     <li><b>Content-Language</b>: Describes the natural language(s) of the intended audience for the enclosed content.</li>
         *     <li><b>Content-Type</b>: Indicates the media type of the resource or the data being sent in the request.</li>
         *     <li><b>Range</b>: Requests only part of an entity. Used for partial downloads and presumable downloads.</li>
         * </ul>
         */
        SIMPLE_HEADER("Accept", "Accept-Language", "Content-Language", "Content-Type", "Range"),

        ;

        private final @NotNull String[] headers;

        Type(@NotNull String @NotNull ... headers) {
            this.headers = headers;
        }

        /**
         * Checks if the specified header key matches any header in the current type.
         *
         * @param key the HeaderKey to be checked
         * @return {@code true} if the key matches any header in the current type, {@code false} otherwise
         */
        public boolean matches(@NotNull HttpHeaderKey<?> key) {
            return Arrays.stream(headers).anyMatch(name -> key.getName().equalsIgnoreCase(name));
        }

    }

}
