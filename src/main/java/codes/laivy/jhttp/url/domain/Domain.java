package codes.laivy.jhttp.url.domain;

import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.url.csp.ContentSecurityPolicy;
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

public final class Domain implements ContentSecurityPolicy.Source {

    // Static initializers

    public static final @NotNull Pattern DOMAIN_URL_PATTERN = Pattern.compile("^(?:(http|https|ws)://)?((?:\\*|[a-zA-Z0-9-]+)(?:\\.(?:\\*|[a-zA-Z0-9-]+))*)(?::(\\d+))?(/.*)?$");

    public static boolean validate(@NotNull String string) {
        return DOMAIN_URL_PATTERN.matcher(string).matches();
    }
    public static @NotNull Domain parse(@NotNull String string) throws ParseException {
        if (validate(string)) {
            return new Domain(string);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid CSP domain url", 0);
        }
    }

    public static @NotNull Domain create(@NotNull InetSocketAddress address, @NotNull Subdomain @NotNull [] subdomains, @Nullable URI uri) {
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
        this.content = content;
    }

    // Getters

    @Override
    public @NotNull Type getType() {
        return Type.DOMAIN;
    }

    public boolean isSecure() {
        @NotNull Matcher matcher = DOMAIN_URL_PATTERN.matcher(toString());
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
        @NotNull Matcher matcher = DOMAIN_URL_PATTERN.matcher(toString());
        @NotNull String subdomainStr = matcher.group(2);

        @NotNull String[] parts = subdomainStr.split("\\.");
        @NotNull String hostname = parts.length >= 2 ? parts[parts.length - 2] + "." + parts[parts.length - 1] : subdomainStr;

        return hostname;
    }

    public @Nullable Integer getPort() {
        @NotNull Matcher matcher = DOMAIN_URL_PATTERN.matcher(toString());

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

    public @NotNull Subdomain @NotNull [] getSubdomains() {
        @NotNull Matcher matcher = DOMAIN_URL_PATTERN.matcher(toString());
        @NotNull String subdomainStr = matcher.group(2);

        @NotNull String[] parts = subdomainStr.split("\\.");
        @NotNull String[] subdomains = Arrays.copyOf(parts, parts.length - 2);

        return Arrays.stream(subdomains).map(subdomain -> {
            if (!subdomain.trim().equals("*")) {
                return Subdomain.create(subdomain);
            } else {
                return Subdomain.wildcard();
            }
        }).toArray(Subdomain[]::new);
    }

    public @Nullable URI getUri() {
        @NotNull Matcher matcher = DOMAIN_URL_PATTERN.matcher(toString());
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
