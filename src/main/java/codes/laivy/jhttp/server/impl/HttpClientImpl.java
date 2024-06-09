package codes.laivy.jhttp.server.impl;

import codes.laivy.jhttp.connection.HttpClient;
import codes.laivy.jhttp.request.HttpRequest;
import codes.laivy.jhttp.response.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

final class HttpClientImpl implements HttpClient {

    private final @NotNull SocketChannel socket;

    public HttpClientImpl(@NotNull SocketChannel socket) {
        this.socket = socket;
    }

    @Override
    public @NotNull Socket getSocket() {
        return socket.socket();
    }

    @Override
    public @NotNull HttpRequest read() {
        return null;
    }

    @Override
    public void write(@NotNull HttpResponse response) {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public @NotNull Metrics getAnalytic() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
