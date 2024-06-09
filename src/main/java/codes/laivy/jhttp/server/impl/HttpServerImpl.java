package codes.laivy.jhttp.server.impl;

import codes.laivy.jhttp.connection.HttpClient;
import codes.laivy.jhttp.exception.ServerNotBindingException;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.server.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public final class HttpServerImpl implements HttpServer {

    private final @NotNull InsecureImpl insecure;
    private final @Nullable Secure secure = null;

    private final @NotNull Versions versions;

    public HttpServerImpl(@NotNull HttpVersion @NotNull ... versions) {
        this.versions = new VersionsImpl(versions);
        this.insecure = new InsecureImpl(8000);
    }

    @Override
    public @NotNull Versions getVersions() {
        return versions;
    }

    @Override
    public @NotNull Insecure getInsecure() {
        return insecure;
    }
    @Override
    public @Nullable Secure getSecure() {
        return secure;
    }

    @Override
    public void close() throws IOException {
        insecure.close();
    }

    @Override
    public void bind() throws IOException {
        insecure.initialize();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    // Classes

    private final class VersionsImpl implements Versions {

        private final @NotNull HttpVersion @NotNull [] versions;

        private VersionsImpl(@NotNull HttpVersion @NotNull ... versions) {
            this.versions = versions;
        }

        @Override
        public @NotNull Stream<HttpVersion> stream() {
            return Arrays.stream(versions);
        }
        @Override
        public @NotNull Iterator<HttpVersion> iterator() {
            return stream().iterator();
        }
    }
    final class InsecureImpl implements Insecure {

        private final @NotNull ClientsImpl clients = new ClientsImpl();

        private @UnknownNullability ServerSocketChannel server;
        private @UnknownNullability Selector selector;

        @Range(from = 1, to = Integer.MAX_VALUE)
        private final int bufferSize;

        private InsecureImpl(
                @Range(from = 1, to = Integer.MAX_VALUE)
                int bufferSize
        ) {
            this.bufferSize = bufferSize;
        }

        @Override
        public int getBufferSize() {
            return bufferSize;
        }

        @Override
        public @NotNull ServerSocket getSocket() throws ServerNotBindingException {
            if (!isActive() || server == null) {
                throw new ServerNotBindingException("cannot obtain the socket of an inactive server");
            }
            return server.socket();
        }
        public @UnknownNullability Selector getSelector() {
            return selector;
        }

        @Override
        public @NotNull Clients getClients() {
            return clients;
        }

        private void initialize() throws IOException {
            selector = Selector.open();

            server = ServerSocketChannel.open();
            server.socket().setReceiveBufferSize(getBufferSize());
            server.configureBlocking(false);

            server.register(selector, SelectionKey.OP_READ);
            server.register(selector, SelectionKey.OP_ACCEPT);
        }
        private void close() throws IOException {
            selector.close();
            server.close();

            server = null;
            selector = null;
        }

    }

    private static final class ClientsImpl implements Insecure.Clients {

        private final @NotNull Set<HttpClient> clients = new LinkedHashSet<>();

        @Override
        public @NotNull Stream<HttpClient> stream() {
            return clients.stream();
        }

        @NotNull
        @Override
        public Iterator<HttpClient> iterator() {
            return clients.iterator();
        }
    }

    private final class ReadThread extends Thread {

        private final @NotNull Executor PROCESSOR_EXECUTOR = new ThreadPoolExecutor(4, 16, 16, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

        private ReadThread() {
        }

        @Override
        public void run() {
            @NotNull Selector selector = insecure.getSelector();

            while (isActive() && selector.isOpen()) {
                // Select keys
                @NotNull Iterator<SelectionKey> keys;

                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) continue;

                    keys = selector.selectedKeys().iterator();
                } catch (@NotNull ClosedSelectorException e) {
                    break;
                } catch (@NotNull IOException e) {
                    getUncaughtExceptionHandler().uncaughtException(this, e);
                    continue;
                }

                // Iterate over keys
                while (keys.hasNext()) {
                    @NotNull SelectionKey key = keys.next();
                    keys.remove();

                    try {
                        if (key.isReadable()) {
                            @NotNull SocketChannel socket = (SocketChannel) key.channel();
                            PROCESSOR_EXECUTOR.execute(new ReadRunnable(socket));
                        } else if (key.isAcceptable()) {
                            @NotNull SocketChannel socket = getInsecure().getSocket().getChannel().accept();
                            PROCESSOR_EXECUTOR.execute(new AcceptanceRunnable(socket));
                        } else {
                            throw new UnsupportedOperationException("unsupported selection key operation: " + key.readyOps());
                        }
                    } catch (@NotNull CancelledKeyException ignore) {
                    } catch (@NotNull Throwable throwable) {
                        getUncaughtExceptionHandler().uncaughtException(this, throwable);
                    }
                }
            }
        }

        // Classes

        private final class AcceptanceRunnable implements Runnable {

            private final @NotNull SocketChannel socket;

            private AcceptanceRunnable(@NotNull SocketChannel socket) {
                this.socket = socket;
            }

            @Override
            public void run() {
                @NotNull HttpClientImpl client = new HttpClientImpl(socket);
                insecure.clients.clients.add(client);
            }
        }
        private final class ReadRunnable implements Runnable {

            private final @NotNull SocketChannel socket;

            private ReadRunnable(@NotNull SocketChannel socket) {
                this.socket = socket;
            }

            @Override
            public void run() {
                @NotNull
            }
        }

    }
}
