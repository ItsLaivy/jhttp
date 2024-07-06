package codes.laivy.jhttp.protocol.factory;

import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.client.HttpClient;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.element.response.HttpResponse.Future;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.parser.element.HttpBodyParseException;
import codes.laivy.jhttp.exception.parser.element.HttpResponseParseException;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface HttpResponseFactory {

    // Getters

    @NotNull HttpVersion getVersion();

    // Modules

    @NotNull HttpResponse create(@NotNull HttpStatus status, @NotNull HttpHeaders headers, @NotNull HttpBody body);

    @NotNull String serialize(@NotNull HttpResponse response) throws EncodingException, IOException;
    @NotNull HttpResponse parse(@NotNull String string) throws HttpResponseParseException, HttpBodyParseException;
    @NotNull Future parse(@NotNull HttpClient client, @NotNull String string) throws HttpResponseParseException;
    boolean validate(@NotNull String string);

}
