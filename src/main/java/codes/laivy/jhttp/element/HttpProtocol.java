package codes.laivy.jhttp.element;

import org.jetbrains.annotations.NotNull;

/**
 * The {@code HttpProtocol} enum represents the different versions of the HTTP protocol.
 * Each enum constant encapsulates details about a specific HTTP protocol version,
 * including its scheme, default port, and security attributes.
 * This enum is crucial for
 * configuring and managing connections in web applications, ensuring that the appropriate
 * protocol settings are applied.
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public enum HttpProtocol {

    /**
     * The HTTP protocol version.
     * This version is the standard protocol for transferring
     * hypertext requests and information on the internet.
     * It uses the "http://" scheme and
     * typically operates over port 80. HTTP connections are not secure, meaning that data
     * transferred is not encrypted.
     */
    HTTP("http://", 80, false),

    /**
     * The HTTPS protocol version.
     * This version is the secure variant of HTTP, using the
     * "https://" scheme and typically operating over port 443. HTTPS connections are secure,
     * meaning that data transferred is encrypted, providing confidentiality and integrity.
     */
    HTTPS("https://", 443, true),
    ;

    private final @NotNull String name;
    private final int port;
    private final boolean secure;

    /**
     * Constructs an {@code HttpProtocol} enum constant with the specified attributes.
     *
     * @param name   the name (scheme) of the protocol, such as "http://" or "https://"
     * @param port   the default port number for the protocol, such as 80 for HTTP and 443 for HTTPS
     * @param secure a boolean indicating whether the protocol is secure (true for HTTPS, false for HTTP)
     */
    HttpProtocol(@NotNull String name, int port, boolean secure) {
        this.name = name;
        this.port = port;
        this.secure = secure;
    }

    /**
     * Gets the scheme of the protocol.
     *
     * @return the name (scheme) of the protocol
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the default port number for the protocol.
     *
     * @return the default port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Checks if the protocol is secure.
     *
     * @return {@code true} if the protocol is secure, {@code false} otherwise
     */
    public boolean isSecure() {
        return secure;
    }

}

