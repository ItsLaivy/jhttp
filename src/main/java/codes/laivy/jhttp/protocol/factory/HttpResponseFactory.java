package codes.laivy.jhttp.protocol.factory;

import codes.laivy.jhttp.client.HttpClient;
import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.element.response.HttpResponse.Future;
import codes.laivy.jhttp.exception.parser.request.HttpResponseParseException;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HttpResponseFactory {

    // Getters

    @NotNull HttpVersion getVersion();

    // Modules

    @NotNull HttpResponse create(@NotNull HttpStatus status, @NotNull HttpHeaders headers, @Nullable HttpBody body);

    @NotNull String serialize(@NotNull HttpResponse response);
    @NotNull HttpResponse parse(@NotNull String string) throws HttpResponseParseException;
    @NotNull Future parse(@NotNull HttpClient client, @NotNull String string) throws HttpResponseParseException;
    boolean validate(@NotNull String string);

}
