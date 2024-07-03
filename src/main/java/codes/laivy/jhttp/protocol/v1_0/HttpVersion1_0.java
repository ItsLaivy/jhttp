package codes.laivy.jhttp.protocol.v1_0;

import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.module.connection.Connection;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.protocol.factory.HttpHeaderFactory;
import codes.laivy.jhttp.protocol.factory.HttpRequestFactory;
import codes.laivy.jhttp.protocol.factory.HttpResponseFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@ApiStatus.Internal
final class HttpVersion1_0 extends HttpVersion {

    public static final @NotNull ScheduledExecutorService FUTURE_TIMEOUT_SCHEDULED = Executors.newScheduledThreadPool(1);

    private final @NotNull HttpRequestFactory requestFactory;
    private final @NotNull HttpResponseFactory responseFactory;
    private final @NotNull HttpHeaderFactory headerFactory;

    public HttpVersion1_0() {
        super(
                new byte[0],
                1, 0
        );

        this.requestFactory = new HttpRequestFactory1_0(this);
        this.responseFactory = new HttpResponseFactory1_0(this);
        this.headerFactory = new HttpHeaderFactory1_0(this);
    }

    // Getters

    @Override
    public byte[] getId() {
        throw new UnsupportedOperationException("http 1.0 doesn't haves an ALPN identifier");
    }

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

    // Modules

    @Override
    public boolean shouldClose(@NotNull HttpResponse response) {
        @Nullable Connection connection = response.getHeaders().first(HeaderKey.CONNECTION).map(Header::getValue).orElse(null);
        return connection == null || connection.getType() == Connection.Type.CLOSE;
    }

}
