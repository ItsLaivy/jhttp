package codes.laivy.jhttp.protocol.v1_1;

import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.protocol.factory.HttpHeaderFactory;
import codes.laivy.jhttp.protocol.factory.HttpRequestFactory;
import codes.laivy.jhttp.protocol.factory.HttpResponseFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@ApiStatus.Internal
final class HttpVersion1_1 extends HttpVersion {

    public static final @NotNull ScheduledExecutorService FUTURE_TIMEOUT_SCHEDULED = Executors.newScheduledThreadPool(1);

    private final @NotNull HttpRequestFactory requestFactory;
    private final @NotNull HttpResponseFactory responseFactory;
    private final @NotNull HttpHeaderFactory headerFactory;

    public HttpVersion1_1() {
        super(
                new byte[] {
                        0x68, 0x74, 0x74, 0x70, 0x2F, 0x31, 0x2E, 0x31
                },
                1,
                1
        );

        this.requestFactory = new HttpRequestFactory1_1(this);
        this.responseFactory = new HttpResponseFactory1_1(this);
        this.headerFactory = new HttpHeaderFactory1_1(this);
    }

    // Getters

    @Override
    public @NotNull HttpRequestFactory getRequestFactory() {
        return requestFactory;
    }
    @Override
    public @NotNull HttpResponseFactory getResponseFactory() {
        return responseFactory;
    }
    @Override
    public @NotNull HttpHeaderFactory getHeaderFactory() {
        return headerFactory;
    }

}
