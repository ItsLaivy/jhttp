package codes.laivy.jhttp.server;

import codes.laivy.jhttp.connection.HttpClient;
import codes.laivy.jhttp.exception.ServerNotBindingException;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.stream.Stream;

public interface HttpServer extends Closeable {

    // Static initializers

    static @NotNull HttpServer create() {
        throw new UnsupportedOperationException();
    }

    // Object

    @NotNull Versions getVersions();

    @Nullable Insecure getInsecure();
    @Nullable Secure getSecure();

    // Loaders

    @Override
    void close() throws IOException;
    void bind() throws IOException;
    boolean isActive();

    // Classes

    interface Versions extends Iterable<HttpVersion> {

        default boolean isCompatible(int major, int minor) {
            return stream().anyMatch(v -> v.getMajor() == major && v.getMinor() == minor);
        }
        default boolean isCompatible(@NotNull HttpVersion version) {
            return stream().anyMatch(v -> v.equals(version));
        }

        @NotNull Stream<HttpVersion> stream();
    }
    interface Insecure {
        @NotNull ServerSocket getSocket() throws ServerNotBindingException;
        @NotNull Clients getClients();

        @Range(from = 1, to = Integer.MAX_VALUE)
        int getBufferSize();

        interface Clients extends Iterable<HttpClient> {
            default @NotNull Optional<HttpClient> get(@NotNull Socket socket) {
                return stream().filter(client -> client.getSocket().equals(socket)).findFirst();
            }
            @NotNull Stream<HttpClient> stream();
        }
    }
    interface Secure extends Insecure {
        @NotNull SSLContext getContext();
    }

}
