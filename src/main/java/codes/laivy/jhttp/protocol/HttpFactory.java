package codes.laivy.jhttp.protocol;

import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.exception.MissingHeaderException;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.exception.parser.IllegalHttpVersionException;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.headers.Headers;
import codes.laivy.jhttp.url.URIAuthority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;

public interface HttpFactory {

    @NotNull Request getRequest();
    @NotNull Response getResponse();
    @NotNull Headers getHeaders();

    interface Request {
        @NotNull HttpRequest parse(@NotNull String data) throws ParseException, MissingHeaderException, HeaderFormatException, IllegalHttpVersionException, UnknownHostException, URISyntaxException, EncodingException, MediaParserException;
        @NotNull String wrap(@NotNull HttpRequest request);

        @NotNull HttpRequest build(@NotNull Method method, @Nullable URIAuthority authority, @NotNull URI uri, @NotNull codes.laivy.jhttp.headers.Headers headers, @Nullable HttpBody body);

        boolean isCompatible(@NotNull String data);
    }
    interface Response {
        @NotNull HttpResponse parse(@NotNull String data) throws ParseException, HeaderFormatException, EncodingException, IllegalHttpVersionException, MediaParserException;
        @NotNull String wrap(@NotNull HttpResponse response);

        @NotNull HttpResponse build(@NotNull HttpStatus status, @NotNull codes.laivy.jhttp.headers.Headers headers, @Nullable HttpBody body);

        boolean isCompatible(@NotNull String data);
    }
    interface Headers {
        <T> @NotNull Header<T> parse(@NotNull String data) throws ParseException, HeaderFormatException;
        <T> @NotNull String wrap(@NotNull Header<T> header);

        boolean isCompatible(@NotNull String data);
    }

}
