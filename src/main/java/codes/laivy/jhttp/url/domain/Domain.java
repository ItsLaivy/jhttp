package codes.laivy.jhttp.url.domain;

import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.csp.ContentSecurityPolicy;
import codes.laivy.jhttp.utilities.HttpProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Domain<T extends Host> implements ContentSecurityPolicy.Source {

    // Static initializers

    public static final @NotNull Pattern DOMAIN_URL_PATTERN = Pattern.compile("^(?:(http|https)://)?(?<host>[^]]{2,39})(?::(\\d+))?(/.*)?$");

    public static boolean validate(@NotNull String string) {
        return DOMAIN_URL_PATTERN.matcher(string).matches();
    }
    public static @NotNull Domain<?> parse(@NotNull String string) throws ParseException {
        @NotNull Matcher matcher = DOMAIN_URL_PATTERN.matcher(string);

        if (matcher.matches()) {
            // Protocol
            @Nullable HttpProtocol protocol = matcher.group(1).equalsIgnoreCase("http") ? HttpProtocol.HTTP : matcher.group(1).equalsIgnoreCase("https") ? HttpProtocol.HTTPS : null;

            // Host
            @NotNull String[] parts = matcher.group(2).split("\\.");
            @NotNull String name = parts.length >= 2 ? parts[parts.length - 2] + "." + parts[parts.length - 1] : matcher.group(2);
            @Nullable Integer port = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : null;

            @NotNull String hostname = matcher.group(2);
            @NotNull Host host;

            if (Host.IPv6.validate(hostname)) {
                host = Host.IPv6.parse(hostname);
            } else if (Host.IPv4.validate(hostname)) {
                host = Host.IPv4.parse(hostname);
            } else if (Host.Name.validate(hostname)) {
                host = Host.Name.parse(hostname);
            } else {
                throw new ParseException("unknown host '" + string + "'", matcher.start(2));
            }

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

            // Finish
            return new Domain<>(protocol, subdomains, name, host);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid domain url", 0);
        }
    }

    public static <E extends Host> @NotNull Domain<E> create(@NotNull HttpProtocol protocol, @NotNull Subdomain @NotNull [] subdomains, @NotNull String name, @NotNull E host) {
        return new Domain<>(protocol, subdomains, name, host);
    }

    // Object

    private final @Nullable HttpProtocol protocol;
    private final @NotNull Subdomain @NotNull [] subdomains;
    private final @NotNull Host host;

    private final @NotNull String name;

    private Domain(@Nullable HttpProtocol protocol, @NotNull Subdomain @NotNull [] subdomains, @NotNull String name, @NotNull Host host) {
        this.protocol = protocol;
        this.subdomains = subdomains;
        this.name = name;
        this.host = host;
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

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Domain<?> domain = (Domain<?>) object;
        return protocol == domain.protocol && Objects.deepEquals(subdomains, domain.subdomains) && Objects.equals(host, domain.host) && Objects.equals(name, domain.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(protocol, Arrays.hashCode(subdomains), host, name);
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
        }

        return builder.toString();
    }

}
