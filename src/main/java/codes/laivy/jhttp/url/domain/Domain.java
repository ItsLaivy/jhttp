package codes.laivy.jhttp.url.domain;

import codes.laivy.jhttp.element.HttpProtocol;
import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import codes.laivy.jhttp.url.Host;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Domain<T extends Host> implements ContentSecurityPolicy.Source {

    // Static initializers
    
    public static boolean validate(@NotNull String string) {
        if (string.startsWith("http://")) {
            string = string.substring(7);
        } else if (string.startsWith("https://")) {
            string = string.substring(8);
        }

        if (string.endsWith("/")) {
            string = string.substring(0, string.length() - 1);
        }

        return Host.validate(string);
    }
    public static @NotNull Domain<?> parse(@NotNull String string) throws IllegalArgumentException {
        if (validate(string)) {
            // Protocol
            @Nullable HttpProtocol protocol = null;

            if (string.startsWith("http://")) {
                string = string.substring(7);
                protocol = HttpProtocol.HTTP;
            } else if (string.startsWith("https://")) {
                string = string.substring(8);
                protocol = HttpProtocol.HTTPS;
            }

            // Host
            @NotNull Host host = Host.parse(string);

            // Finish
            return new Domain<>(protocol, host);
        } else {
            throw new IllegalArgumentException("cannot parse '" + string + "' as a valid domain url");
        }
    }

    public static <E extends Host> @NotNull Domain<E> create(@NotNull HttpProtocol protocol, @NotNull E host) {
        return new Domain<>(protocol, host);
    }

    // Object

    private final @Nullable HttpProtocol protocol;
    private final @NotNull T host;

    private Domain(@Nullable HttpProtocol protocol, @NotNull T host) {
        this.protocol = protocol;
        this.host = host;
    }

    // Getters

    public @Nullable HttpProtocol getProtocol() {
        return protocol;
    }

    public @NotNull T getHost() {
        return host;
    }

    // Implementations
    
    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Domain)) return false;
        @NotNull Domain<?> domain = (Domain<?>) object;
        return getProtocol() == domain.getProtocol() && Objects.equals(getHost(), domain.getHost());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getProtocol(), getHost());
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder();

        if (getProtocol() != null) {
            builder.append(getProtocol().getName());
        }

        builder.append(getHost());

        return builder.toString();
    }

}
