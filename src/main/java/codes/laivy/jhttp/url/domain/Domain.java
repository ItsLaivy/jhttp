package codes.laivy.jhttp.url.domain;

import codes.laivy.jhttp.url.csp.ContentSecurityPolicy;
import codes.laivy.jhttp.utilities.HttpProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Domain implements ContentSecurityPolicy.Source {

    // Static initializers

    public static final @NotNull Pattern DOMAIN_URL_PATTERN = Pattern.compile("^(?:(http|https)://)?" + Host.HOST_PATTERN.pattern() + "(?::(\\d+))?(/.*)?$");

    public static boolean validate(@NotNull String string) {
        return DOMAIN_URL_PATTERN.matcher(string).matches();
    }
    public static @NotNull Domain parse(@NotNull String string) throws ParseException {
        @NotNull Matcher matcher = DOMAIN_URL_PATTERN.matcher(string);

        if (matcher.matches()) {
            // Protocol
            @Nullable HttpProtocol protocol = matcher.group(1).equalsIgnoreCase("http") ? HttpProtocol.HTTP : matcher.group(1).equalsIgnoreCase("https") ? HttpProtocol.HTTPS : null;

            // Host
            @NotNull String[] parts = matcher.group(2).split("\\.");
            @NotNull String name = parts.length >= 2 ? parts[parts.length - 2] + "." + parts[parts.length - 1] : matcher.group(2);
            @Nullable Integer port = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : null;

            @NotNull Host host = new Host(matcher.group(2), port);

            if (port != null) {
                if (port == HttpProtocol.HTTP.getPort()) {
                    protocol = HttpProtocol.HTTP;
                } else if (port == HttpProtocol.HTTPS.getPort()) {
                    protocol = HttpProtocol.HTTPS;
                }
            }

            // Subdomains
            @NotNull Subdomain[] subdomains = Arrays.stream(Arrays.copyOf(parts, parts.length - 2)).map(subdomain -> {
                if (!subdomain.trim().equals("*")) return Subdomain.create(subdomain);
                else return Subdomain.wildcard();
            }).toArray(Subdomain[]::new);

            // Path
            @Nullable URI path = matcher.group(4) != null ? URI.create(matcher.group(4)) : null;

            // Finish
            return new Domain(protocol, subdomains, name, host, path);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid domain url", 0);
        }
    }

    public static @NotNull Domain create(@NotNull HttpProtocol protocol, @NotNull Subdomain @NotNull [] subdomains, @NotNull String name, @NotNull Host host, @NotNull URI path) {
        return new Domain(protocol, subdomains, name, host, path);
    }

    // Object

    private final @Nullable HttpProtocol protocol;
    private final @NotNull Subdomain @NotNull [] subdomains;
    private final @NotNull Host host;
    private final @Nullable URI path;

    private final @NotNull String name;

    private Domain(@Nullable HttpProtocol protocol, @NotNull Subdomain @NotNull [] subdomains, @NotNull String name, @NotNull Host host, @Nullable URI path) {
        this.protocol = protocol;
        this.subdomains = subdomains;
        this.name = name;
        this.host = host;
        this.path = path;
    }

    // Getters

    public @Nullable HttpProtocol getProtocol() {
        return protocol;
    }

    public @NotNull Subdomain @NotNull [] getSubdomains() {
        return subdomains;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Host getHost() {
        return host;
    }

    public @Nullable URI getPath() {
        return path;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Domain domain = (Domain) object;
        return protocol == domain.protocol && Objects.deepEquals(subdomains, domain.subdomains) && Objects.equals(host, domain.host) && Objects.equals(path, domain.path) && Objects.equals(name, domain.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(protocol, Arrays.hashCode(subdomains), host, path, name);
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder();
        @Nullable Integer port = getHost().getPort();

        if (getProtocol() != null) {
            builder.append(getProtocol().getName());

            if (Objects.equals(port, getProtocol().getPort())) {
                port = null;
            }
        } for (@NotNull Subdomain subdomain : getSubdomains()) {
            builder.append(subdomain.getValue()).append(".");
        }

        builder.append(getName());

        if (port != null) {
            builder.append(":").append(port);
        } if (getPath() != null) {
            if (!getPath().toString().startsWith("/")) {
                builder.append("/");
            }

            builder.append(getPath());
        }

        return builder.toString();
    }

    // Classes

    public static final class Host {

        // Static initializers

        public static final @NotNull Pattern HOST_NAME_PATTERN = Pattern.compile("^(?<host>localhost|(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|(?:\\d{1,3}\\.){3}\\d{1,3}|\\[(?<ipv6>(?:[a-fA-F0-9]{0,4}:){2,7}(?::[a-fA-F0-9]{0,4}){1,7})])");
        public static final @NotNull Pattern HOST_PATTERN = Pattern.compile("^" + HOST_NAME_PATTERN.pattern() + "(?::(?<port>\\d{1,5}))?$");

        public static boolean validate(@NotNull String string) {
            return HOST_PATTERN.matcher(string).matches();
        }
        public static @NotNull Host parse(@NotNull String string) throws ParseException {
            @NotNull Matcher matcher = HOST_PATTERN.matcher(string);

            if (matcher.matches()) {
                @NotNull String name = matcher.group("host");
                @Nullable Integer port = matcher.group("port") != null ? Integer.parseInt(matcher.group("port")) : null;

                return new Host(name, port);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid host", 0);
            }
        }

        public static @NotNull Host create(@NotNull String name) {
            return new Host(name, null);
        }
        public static @NotNull Host create(@NotNull String name, int port) {
            return new Host(name, port);
        }

        // Object

        private final @NotNull String name;
        private final @Nullable Integer port;

        private Host(@NotNull String name, @Nullable Integer port) {
            this.name = name;
            this.port = port;

            if (!name.matches(HOST_NAME_PATTERN.pattern())) {
                throw new IllegalArgumentException("this host name is invalid");
            }
        }

        public @NotNull String getName() {
            return name;
        }
        public @Nullable Integer getPort() {
            return port;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull Host host = (Host) object;
            return Objects.equals(port, host.port) && Objects.equals(name, host.name);
        }
        @Override
        public int hashCode() {
            return Objects.hash(name, port);
        }

        @Override
        public @NotNull String toString() {
            return getName() + (getPort() != null ? ":" + getPort() : "");
        }

    }

}
