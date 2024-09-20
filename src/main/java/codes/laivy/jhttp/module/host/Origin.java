package codes.laivy.jhttp.module.host;

import codes.laivy.address.host.HttpHost;
import codes.laivy.address.http.HttpAddress;
import codes.laivy.address.port.Port;
import codes.laivy.jhttp.element.HttpProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Origin<T extends HttpAddress> {

    // Static initializers

    // Object

    private final @NotNull HttpProtocol protocol;
    private final @NotNull HttpHost<T> host;

    protected Origin(@NotNull HttpProtocol protocol, @NotNull HttpHost<T> host) {
        this.protocol = protocol;
        this.host = host;
    }

    // Getters

    public @NotNull HttpProtocol getProtocol() {
        return protocol;
    }

    public @NotNull T getAddress() {
        return host.getAddress();
    }
    public @Nullable Port getPort() {
        return host.getPort();
    }

    public @NotNull HttpHost<T> getHost() {
        return host;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Origin)) return false;
        @NotNull Origin<?> origin = (Origin<?>) object;
        return getProtocol() == origin.getProtocol() && Objects.equals(getHost(), origin.getHost());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getProtocol(), getHost());
    }

    @Override
    public @NotNull String toString() {
        return getProtocol().getName() + getHost();
    }

}
