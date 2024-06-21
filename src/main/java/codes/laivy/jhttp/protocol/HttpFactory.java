package codes.laivy.jhttp.protocol;

import codes.laivy.jhttp.exception.MissingHeaderException;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.message.Message;
import codes.laivy.jhttp.request.HttpRequest;
import codes.laivy.jhttp.response.HttpResponse;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.utilities.HttpStatus;
import codes.laivy.jhttp.utilities.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.text.ParseException;

import static codes.laivy.jhttp.headers.Headers.MutableHeaders;

public interface HttpFactory {

    @NotNull Request getRequest();
    @NotNull Response getResponse();
    @NotNull Headers getHeaders();

    interface Request {
        @NotNull HttpRequest parse(@NotNull String data) throws ParseException, MissingHeaderException, HeaderFormatException;
        @NotNull String wrap(@NotNull HttpRequest request);

        @NotNull HttpRequest build(@NotNull Method method, @Nullable URIAuthority authority, @NotNull URI uri, @NotNull MutableHeaders headers, @Nullable Message message);

        boolean isCompatible(@NotNull String data);
    }
    interface Response {
        @NotNull HttpResponse parse(@NotNull String data) throws ParseException, HeaderFormatException;
        @NotNull String wrap(@NotNull HttpResponse response);

        @NotNull HttpResponse build(@NotNull HttpStatus status, @NotNull MutableHeaders headers, @Nullable Message message);

        boolean isCompatible(@NotNull String data);
    }
    interface Headers {
        <T> @NotNull Header<T> parse(@NotNull String data) throws ParseException, HeaderFormatException;
        <T> @NotNull String wrap(@NotNull Header<T> header);

        boolean isCompatible(@NotNull String data);
    }

}
