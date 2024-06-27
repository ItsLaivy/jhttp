package codes.laivy.jhttp.protocol.factory;

import codes.laivy.jhttp.connection.HttpClient;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.element.response.HttpResponse.Future;
import codes.laivy.jhttp.exception.parser.request.HttpResponseParseException;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;

public interface HttpResponseFactory {

    // Getters

    @NotNull HttpVersion getVersion();

    // Modules

    @NotNull String serialize(@NotNull HttpResponse response);
    @NotNull HttpResponse parse(@NotNull String string) throws HttpResponseParseException;
    @NotNull Future parse(@NotNull HttpClient client, @NotNull String string) throws HttpResponseParseException;
    boolean validate(@NotNull String string);

}
