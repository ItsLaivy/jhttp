package codes.laivy.jhttp.module;

import codes.laivy.jhttp.element.HttpProtocol;
import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.domain.Domain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Objects;

public class Location implements ContentSecurityPolicy.Source {

    // Static initializers

    public static boolean validate(@NotNull String string) {
        try {
            if (!string.startsWith("/")) {
                // Get the protocol
                @Nullable HttpProtocol protocol;

                if (string.toLowerCase().startsWith("https://") || string.toLowerCase().startsWith("http://")) {
                    if (string.toLowerCase().startsWith("https://")) {
                        protocol = HttpProtocol.HTTPS;
                    } else if (string.toLowerCase().startsWith("http://")) {
                        protocol = HttpProtocol.HTTP;
                    } else {
                        return false;
                    }

                    string = string.substring(protocol.getName().length());
                }

                // Get domain
                @NotNull String[] parts = string.split("/", 2);
                if (!Host.validate(parts[0])) return false;

                // Finish with uri
                string = "/" + (parts.length == 2 ? parts[1].replace("+", "%20") : "");
            }

            // Parse uri
            new URI(string);

            // Finish successfully
            return true;
        } catch (@NotNull URISyntaxException ignore) {
        }

        return false;
    }
    public static @NotNull Location parse(@NotNull String string) {
        if (validate(string)) try {
            @Nullable HttpProtocol protocol = null;
            @Nullable Domain<?> domain = null;
            @NotNull URI uri;

            if (string.startsWith("/")) { // It's a path
                uri = URI.create(URLDecoder.decode(string.replace("+", "%20"), "UTF-8"));
            } else { // Parse domain and path

                if (string.toLowerCase().startsWith("https://") || string.toLowerCase().startsWith("http://")) {
                    if (string.toLowerCase().startsWith("https://")) {
                        protocol = HttpProtocol.HTTPS;
                    } else if (string.toLowerCase().startsWith("http://")) {
                        protocol = HttpProtocol.HTTP;
                    } else {
                        throw new IllegalArgumentException("cannot retrieve protocol from location");
                    }

                    string = string.substring(protocol.getName().length());
                }

                // Get domain
                @NotNull String[] parts = string.split("/", 2);

                domain = Domain.parse(parts[0]);
                uri = URI.create("/" + (parts.length == 2 ? parts[1] : ""));
            }

            return create(protocol, domain, uri);
        } catch (@NotNull UnsupportedEncodingException e) {
            throw new RuntimeException("cannot find UTF-8 charset on system", e);
        } else {
            throw new IllegalArgumentException("cannot parse '" + string + "' as a valid location");
        }
    }

    public static @NotNull Location create(@Nullable HttpProtocol protocol, @Nullable Domain<?> domain, @NotNull URI uri) {
        return new Location(protocol, domain, uri);
    }

    // Object

    private final @Nullable HttpProtocol protocol;
    private final @Nullable Domain<?> domain;
    private final @NotNull URI uri;

    protected Location(@Nullable HttpProtocol protocol, @Nullable Domain<?> domain, @NotNull URI uri) {
        this.protocol = protocol;
        this.domain = domain;
        this.uri = uri;
    }

    // Getters

    public @Nullable HttpProtocol getProtocol() {
        return protocol;
    }
    public @Nullable Domain<?> getDomain() {
        return domain;
    }
    public @NotNull URI getURI() {
        return uri;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Location)) return false;
        @NotNull Location location = (Location) object;
        return getProtocol() == location.getProtocol() && Objects.equals(getDomain(), location.getDomain()) && Objects.equals(getURI(), location.getURI());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getProtocol(), getDomain(), getURI());
    }

    @Override
    public final @NotNull String toString() {
        if (getDomain() != null) {
            @NotNull StringBuilder builder = new StringBuilder();

            if (getProtocol() != null) {
                builder.append(getProtocol().getName());
            }

            builder.append(getDomain().toString());

            if (!getURI().toString().isEmpty() && !getURI().toString().startsWith("/")) {
                builder.append("/");
            }

            builder.append(getURI());
            return builder.toString();
        } else {
            return getURI().toString();
        }
    }

}
