package codes.laivy.jhttp.protocol;

import codes.laivy.jhttp.connection.HttpClient;
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
        @NotNull HttpRequest parse(byte[] data) throws ParseException, MissingHeaderException, HeaderFormatException;
        byte[] wrap(@NotNull HttpRequest request);

        @NotNull HttpRequest build(@NotNull Method method, @Nullable URIAuthority authority, @NotNull URI uri, @NotNull MutableHeaders headers, @Nullable Message message);

        boolean isCompatible(byte[] data);
    }
    interface Response {
        @NotNull HttpResponse parse(byte[] data) throws ParseException;
        byte[] wrap(@NotNull HttpResponse response);

        @NotNull HttpResponse build(@NotNull HttpStatus status, @NotNull MutableHeaders headers, @Nullable Message message);

        boolean isCompatible(byte[] data);
    }
    interface Headers {
        <T> @NotNull Header<T> parse(byte[] data) throws ParseException, HeaderFormatException;
        <T> byte[] wrap(@NotNull Header<T> header);

        boolean isCompatible(byte[] data);
    }

}
