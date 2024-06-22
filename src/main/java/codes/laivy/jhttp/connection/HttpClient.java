package codes.laivy.jhttp.connection;

import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.element.response.HttpResponse;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.Flushable;
import java.net.Socket;

public interface HttpClient extends Closeable {

    @NotNull Socket getSocket();

    @Blocking
    @NotNull HttpRequest read();
    void write(@NotNull HttpResponse response);

    boolean isOpen();
    
    @NotNull Metrics getAnalytic();

    // Classes

    interface Metrics extends Flushable {
        long getRequestCount();
        long getResponseCount();

        long getSentBytesCount();
        long getReceivedBytesCount();
    }

}
