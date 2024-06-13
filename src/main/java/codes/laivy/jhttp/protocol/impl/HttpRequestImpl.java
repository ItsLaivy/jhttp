package codes.laivy.jhttp.protocol.impl;

import codes.laivy.jhttp.headers.Headers.MutableHeaders;
import codes.laivy.jhttp.message.Message;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.request.HttpRequest;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.utilities.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public final class HttpRequestImpl implements HttpRequest {

    private final @NotNull HttpVersion version;
    private final @NotNull Method method;
    private final @Nullable URIAuthority authority;
    private final @NotNull URI uri;
    private final @NotNull MutableHeaders headers;
    private final @Nullable Message message;

    public HttpRequestImpl(@NotNull HttpVersion version, @NotNull Method method, @Nullable URIAuthority authority, @NotNull URI uri, @NotNull MutableHeaders headers, @Nullable Message message) {
        this.version = version;
        this.method = method;
        this.authority = authority;
        this.uri = uri;
        this.headers = headers;
        this.message = message;
    }

    @Override
    public byte[] getBytes() {
        return getVersion().getFactory().getRequest().wrap(this);
    }

    @Override
    public @NotNull HttpVersion getVersion() {
        return version;
    }

    @Override
    public @NotNull Method getMethod() {
        return method;
    }

    @Override
    public @Nullable URIAuthority getAuthority() {
        return authority;
    }

    @Override
    public @NotNull URI getUri() {
        return uri;
    }

    @Override
    public @NotNull MutableHeaders getHeaders() {
        return headers;
    }

    @Override
    public @Nullable Message getMessage() {
        return message;
    }
}
