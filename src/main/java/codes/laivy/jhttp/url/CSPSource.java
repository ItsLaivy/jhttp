package codes.laivy.jhttp.url;

import codes.laivy.jhttp.utilities.header.Wildcard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class CSPSource {

    // Static initializers

    public static @NotNull CSPSource parse(@NotNull String string) throws ParseException {
        if (Domain.validate(string)) {
            return Domain.parse(string);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid CSP source", 0);
        }
    }

    // Object

    private final @NotNull Type type;

    private CSPSource(@NotNull Type type) {
        this.type = type;
    }

    // Getters

    public @NotNull Type getType() {
        return type;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull CSPSource cspSource = (CSPSource) object;
        return type == cspSource.type;
    }
    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public abstract @NotNull String toString();

    // Classes

    public static final class Domain extends CSPSource {

        // Static initializers

        public static final @NotNull Pattern PARSE_PATTERN = Pattern.compile("^(?:(http|https|ws)://)?((?:\\*|[a-zA-Z0-9-]+)(?:\\.(?:\\*|[a-zA-Z0-9-]+))*)(?::(\\d+))?(/.*)?$");

        public static boolean validate(@NotNull String string) {
            return PARSE_PATTERN.matcher(string).matches();
        }
        public static @NotNull Domain parse(@NotNull String string) throws ParseException {
            if (validate(string)) {
                return new Domain(string);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid CSP domain", 0);
            }
        }

        public static @NotNull Domain create(@NotNull InetSocketAddress address, @NotNull Wildcard<Subdomain> @NotNull [] subdomains, @Nullable URI uri) {
            @NotNull String protocol = (address.getPort() == URIAuthority.DEFAULT_HTTP_PORT) ? "http://" : (address.getPort() == URIAuthority.DEFAULT_HTTPS_PORT) ? "https://" : "";
            @NotNull String port = !protocol.isEmpty() ? ":" + address.getPort() : "";
            @NotNull String path = "";
            @NotNull String sub = Arrays.stream(subdomains).map(s -> s + ".").collect(Collectors.joining());

            if (uri != null) {
                path = uri.getPath();

                if (!path.isEmpty() && path.charAt(0) != '/') {
                    path = "/" + uri.getPath();
                }
            }

            return new Domain(protocol + sub + address.getHostName() + port + path);
        }

        // Object

        private final @NotNull String content;

        private Domain(@NotNull String content) {
            super(Type.DOMAIN);
            this.content = content;
        }

        // Getters

        public boolean isSecure() {
            @NotNull Matcher matcher = PARSE_PATTERN.matcher(toString());
            @NotNull String protocol = matcher.group(1);

            if (protocol.equalsIgnoreCase("https")) {
                return true;
            }

            if (getPort() != null) {
                if (URIAuthority.DEFAULT_HTTPS_PORT == getPort()) {
                    return true;
                } else if (URIAuthority.DEFAULT_HTTP_PORT == getPort()) {
                    return false;
                }
            }

            return false;
        }

        public @NotNull String getHostname() {
            @NotNull Matcher matcher = PARSE_PATTERN.matcher(toString());
            @NotNull String subdomainStr = matcher.group(2);

            @NotNull String[] parts = subdomainStr.split("\\.");
            @NotNull String hostname = parts.length >= 2 ? parts[parts.length - 2] + "." + parts[parts.length - 1] : subdomainStr;

            return hostname;
        }
        public @Nullable Integer getPort() {
            @NotNull Matcher matcher = PARSE_PATTERN.matcher(toString());

            @NotNull String protocol = matcher.group(1);
            @NotNull String portStr = matcher.group(3);
            @Nullable Integer port = portStr != null ? Integer.parseInt(portStr) : null;

            if (port == null && protocol != null) {
                if (protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("ws")) {
                    port = URIAuthority.DEFAULT_HTTP_PORT;
                } else if (protocol.equalsIgnoreCase("https")) {
                    port = URIAuthority.DEFAULT_HTTPS_PORT;
                }
            }

            return port;
        }

        public @NotNull Wildcard<Subdomain> @NotNull [] getSubdomains() {
            @NotNull Matcher matcher = PARSE_PATTERN.matcher(toString());
            @NotNull String subdomainStr = matcher.group(2);

            @NotNull String[] parts = subdomainStr.split("\\.");
            @NotNull String[] subdomains = Arrays.copyOf(parts, parts.length - 2);

            //noinspection unchecked
            return Arrays.stream(subdomains).map(subdomain -> {
                if (!subdomain.trim().equals("*")) {
                    return Wildcard.create(Subdomain.create(subdomain));
                } else {
                    return Wildcard.create();
                }
            }).toArray(Wildcard[]::new);
        }

        public @Nullable URI getUri() {
            @NotNull Matcher matcher = PARSE_PATTERN.matcher(toString());
            @Nullable String path = matcher.group(4);

            return path != null ? URI.create(path) : null;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            if (!super.equals(object)) return false;
            @NotNull Domain domain = (Domain) object;
            return Objects.equals(content, domain.content);
        }
        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), content);
        }

        @Override
        public @NotNull String toString() {
            return content;
        }

    }

    /**
     * Enumeration representing various types of sources for Content Security Policy (CSP).
     * These sources define the allowed locations from which resources can be loaded and executed.
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    public enum Type {

        /**
         * Internet host by name or IP address. The URL scheme, port number, and path are optional.
         * Wildcards ('*') can be used for subdomains, host address, and port number, indicating that all
         * legal values of each are valid. When matching schemes, secure upgrades are allowed (e.g.,
         * specifying <a href="http://example.com">.<a href="..</a>">will match h</a>ttps://example.com).
         * That type matches with WebSocket URLs also. These URLs use the ws: or wss: schemes for WebSocket connections.
         * <p>
         * Example: example.com, *.example.com:80, 192.168.0.1
         * </p>
         *
         * @see <a href="https://developer.mozilla.org/en-US/docs/Learn/Common_questions/Web_mechanics/What_is_a_URL">URL Scheme</a>
         */
        DOMAIN,

        /**
         * Data URLs. These URLs embed small data directly in the URL and use the data: scheme.
         * <p>
         * Example: data:image/png;base64,iVBORw0KGgoNAANSUhEUgAAA...
         * </p>
         */
        DATA,

        /**
         * MediaStream URLs. These URLs represent a media stream and use the mediastream: scheme.
         * <p>
         * Example: mediastream:myStream
         * </p>
         */
        MEDIASTREAM,

        /**
         * Blob URLs. These URLs represent binary large objects and use the blob: scheme.
         * <p>
         * Example: blob:<a href="https://example.com/550e8400-e29b-41d4-a716-446655440000">...</a>
         * </p>
         */
        BLOB,

        /**
         * FileSystem URLs. These URLs represent files in a sandboxed file system and use the filesystem: scheme.
         * <p>
         * Example: filesystem:<a href="https://example.com/temporary/myFile">...</a>
         * </p>
         */
        FILESYSTEM,

    }

}
