package codes.laivy.jhttp.protocol.v1_1;

import codes.laivy.jhttp.content.Content;
import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.element.HttpProtocol;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.MissingHeaderException;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.exception.parser.IllegalHttpVersionException;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.headers.Headers.MutableHeaders;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.protocol.HttpFactory;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.pseudo.provided.PseudoEncoding;
import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Arrays;
import java.util.regex.Pattern;

import static codes.laivy.jhttp.Main.CRLF;
import static codes.laivy.jhttp.headers.HeaderKey.*;

@ApiStatus.Internal
final class HttpFactory1_1 implements HttpFactory {

    // Utilities

    private static @NotNull URI parseUri(@NotNull String string) throws URISyntaxException {
        // Remove protocol
        for (@NotNull HttpProtocol protocol : HttpProtocol.values()) {
            if (string.startsWith(protocol.getName())) {
                string = string.replaceFirst(protocol.getName(), "");
            }
        }

        // Parse
        @NotNull String[] split = string.split("/", 2);

        if (split.length == 1) {
            return new URI("/");
        } else {
            return new URI("/" + split[1]);
        }
    }

    // Parsers

    private final @NotNull Request request = new Request() {
        @SuppressWarnings("rawtypes")
        @Override
        public @NotNull HttpRequest parse(@NotNull String string) throws IllegalHttpVersionException, MissingHeaderException, HeaderFormatException, UnknownHostException, URISyntaxException, EncodingException, ParseException, MediaParserException {
            if (!isCompatible(string)) {
                throw new ParseException("not a valid " + getVersion() + " response", -1);
            }

            // Content
            @NotNull String[] content = string.split(CRLF + CRLF, 2);

            // Headers
            @NotNull MutableHeaders headers = codes.laivy.jhttp.headers.Headers.createMutable();
            if (content[0].split(CRLF, 2).length > 1) {
                for (@NotNull String headerString : content[0].split(CRLF, 2)[1].split(CRLF)) {
                    @NotNull Header<?> header = getHeaders().parse(headerString);
                    if (!header.getKey().getTarget().isRequests()) continue;

                    headers.add(header);
                }
            }

            // Validate host headers
            if (headers.get(HeaderKey.HOST).length == 0) {
                throw new MissingHeaderException("cannot find '" + HeaderKey.HOST + "' header. This header is required for " + getVersion() + " requests.");
            } else if (headers.get(HeaderKey.HOST).length > 1) {
                throw new HeaderFormatException("multiples '" + HeaderKey.HOST + "' headers.");
            }

            @NotNull Host host = headers.get(HeaderKey.HOST)[0].getValue();

            // Request line
            @NotNull String[] requestLine = content[0].split(CRLF, 2)[0].split(" ");
            @NotNull Method method = Method.valueOf(requestLine[0].toUpperCase());
            @Nullable URIAuthority authority = URIAuthority.validate(requestLine[1]) ? URIAuthority.parse(requestLine[1]) : null;
            @NotNull URI uri = parseUri(requestLine[1]);

            if (!requestLine[2].equals(getVersion().toString())) {
                throw new IllegalHttpVersionException("this request from version '" + requestLine[2] + "' cannot be parsed using the version '" + getVersion() + "'");
            }

            // Message
            @Nullable MediaType<?> media = null;
            @NotNull Charset charset = StandardCharsets.UTF_8;

            @Nullable HttpBody body;

            {
                // Content Type
                if (headers.contains(CONTENT_TYPE)) {
                    media = headers.get(CONTENT_TYPE)[0].getValue();
                }

                // Message Length
                @NotNull String pure = content[1];

                if (headers.contains(CONTENT_LENGTH)) {
                    int contentLength = (int) headers.get(CONTENT_LENGTH)[0].getValue().getBytes();
                    pure = pure.substring(0, contentLength);
                }

                // Message Encoding
                @NotNull Encoding @Nullable [] encodings = null;
                if (headers.contains(CONTENT_ENCODING)) {
                    @NotNull PseudoEncoding[] array = headers.get(CONTENT_ENCODING)[0].getValue();

                    // Only apply encoding if all the encodings are available (not pseudo)
                    if (Arrays.stream(array).allMatch(PseudoEncoding::available)) {
                        encodings = Arrays.stream(array).map(PseudoEncoding::retrieve).toArray(Encoding[]::new);
                    }
                }

                if (encodings != null) {
                    for (@NotNull Encoding encoding : encodings) {
                        pure = encoding.decompress(pure);
                    }
                }

                // Interpret Message
                if (StringUtils.isBlank(pure)) {
                    body = null;
                } else {
                    @Nullable Content<?> contentBody = null;

                    if (media != null) {
                        // noinspection unchecked
                        contentBody = ((MediaParser) media.getParser()).deserialize(media, pure);
                    }

                    body = HttpBody.create(contentBody, pure, content[1]);
                }
            }

            // Finish
            return build(method, authority, uri, headers, body);
        }

        @Override
        public @NotNull String wrap(@NotNull HttpRequest request) {
            if (!request.getHeaders().contains(HeaderKey.HOST)) {
                throw new IllegalStateException("the http requests from version " + getVersion() + " must have the '" + HeaderKey.HOST + "' header");
            } else if (request.getHeaders().count(HeaderKey.HOST) > 1) {
                throw new IllegalStateException("the http requests from version " + getVersion() + " cannot have multiples '" + HeaderKey.HOST + "' headers");
            }

            @NotNull StringBuilder builder = new StringBuilder();

            // Write request line
            @NotNull String authority = request.getAuthority() != null ? request.getAuthority().toString() : "";

            @NotNull String uri = request.getUri().toString();
            if (!StringUtils.isBlank(uri)) {
                if (!uri.startsWith("/")) authority += "/";
                authority += request.getUri().toString();
            }

            builder.append(request.getMethod().name()).append(" ").append(authority).append(" ").append(getVersion());
            // Write headers
            for (@NotNull Header<?> header : request.getHeaders()) {
                if (!header.getKey().getTarget().isRequests()) continue;
                builder.append(CRLF).append(getHeaders().wrap(header));
            }
            // End request configurations
            builder.append(CRLF).append(CRLF);
            // Write a message if exists
            if (request.getBody() != null) {
                builder.append(request.getBody());
            }

            return builder.toString();
        }

        @Override
        public @NotNull HttpRequest build(@NotNull Method method, @Nullable URIAuthority authority, @NotNull URI uri, @NotNull MutableHeaders headers, @Nullable HttpBody body) {
            return HttpRequest.create(getVersion(), method, authority, uri, headers, body);
        }

        @Override
        public boolean isCompatible(@NotNull String string) {
            if (!string.contains(CRLF) || !string.contains("\n\r\n")) {
                return false;
            }

            @NotNull String[] split = string.split("\r");
            return split[0].toUpperCase().endsWith(getVersion().toString()) && split[0].split(" ").length == 3;
        }
    };
    private final @NotNull Response response = new Response() {
        @SuppressWarnings("rawtypes")
        @Override
        public @NotNull HttpResponse parse(@NotNull String string) throws ParseException, HeaderFormatException, EncodingException, IllegalHttpVersionException, MediaParserException {
            if (!isCompatible(string)) {
                throw new ParseException("not a valid " + getVersion() + " response", -1);
            }

            // Content
            @NotNull String[] content = string.split(CRLF + CRLF, 2);

            // Response line
            @NotNull String[] line = content[0].split(CRLF, 2)[0].split(" ", 3);

            int code = Integer.parseInt(line[1]);
            @NotNull HttpStatus status = HttpStatus.getByCode(code);

            // Retrieve headers
            @NotNull MutableHeaders headers = codes.laivy.jhttp.headers.Headers.createMutable();

            if (content[0].split(CRLF, 2).length > 1) {
                for (@NotNull String headerString : content[0].split(CRLF, 2)[1].split(CRLF)) {
                    @NotNull Header<?> header = getHeaders().parse(headerString);
                    if (!header.getKey().getTarget().isResponses()) continue;

                    headers.add(header);
                }
            }

            // Message
            @NotNull Charset charset = StandardCharsets.UTF_8;
            @Nullable MediaType<?> media = null;

            @Nullable HttpBody body;

            {
                // Content Type
                if (headers.contains(CONTENT_TYPE)) {
                    media = headers.get(CONTENT_TYPE)[0].getValue();
                }

                // Message Length
                @NotNull String pure = content[1];

                if (headers.contains(CONTENT_LENGTH)) {
                    int contentLength = (int) headers.get(CONTENT_LENGTH)[0].getValue().getBytes();
                    pure = pure.substring(0, contentLength);
                }

                // Message Encoding
                @NotNull Encoding @Nullable [] encodings = null;
                if (headers.contains(CONTENT_ENCODING)) {
                    @NotNull PseudoEncoding[] array = headers.get(CONTENT_ENCODING)[0].getValue();

                    // Only apply encoding if all the encodings are available (not pseudo)
                    if (Arrays.stream(array).allMatch(PseudoEncoding::available)) {
                        encodings = Arrays.stream(array).map(PseudoEncoding::retrieve).toArray(Encoding[]::new);
                    }
                }

                if (encodings != null) {
                    for (@NotNull Encoding encoding : encodings) {
                        pure = encoding.decompress(pure);
                    }
                }

                // Interpret Message
                if (StringUtils.isBlank(pure)) {
                    body = null;
                } else {
                    @Nullable Content<?> contentBody = null;

                    if (media != null) {
                        // noinspection unchecked
                        contentBody = ((MediaParser) media.getParser()).deserialize(media, pure);
                    }

                    body = HttpBody.create(contentBody, pure, content[1]);
                }
            }

            // Finish
            return build(status, headers, body);
        }

        @Override
        public @NotNull String wrap(@NotNull HttpResponse response) {
            for (@NotNull HeaderKey<?> key : response.getStatus().getHeaders()) {
                if (!response.getHeaders().contains(key)) {
                    throw new NullPointerException("this response code '" + response.getStatus().getCode() + "' must have the header '" + key.getName() + "'");
                }
            }

            @NotNull StringBuilder builder = new StringBuilder();
            builder.append(getVersion()).append(" ").append(response.getStatus().getCode()).append(" ").append(response.getStatus().getMessage()).append(CRLF);
            // Write headers
            for (@NotNull Header<?> header : response.getHeaders()) {
                builder.append(getHeaders().wrap(header)).append(CRLF);
            }
            // End request configurations
            builder.append(CRLF);
            // Write a message if exists
            if (response.getBody() != null) {
                builder.append(response.getBody());
            }

            return builder.toString();
        }

        @Override
        public @NotNull HttpResponse build(@NotNull HttpStatus status, @NotNull MutableHeaders headers, @Nullable HttpBody body) {
            return HttpResponse.create(status, getVersion(), headers, body);
        }

        @Override
        public boolean isCompatible(@NotNull String string) {
            if (!string.contains(CRLF) || !string.contains("\n\r\n")) {
                return false;
            }

            return string.startsWith(getVersion().toString()) && string.split(" ", 3).length == 3;
        }
    };
    private final @NotNull Headers headers = new Headers() {
        @Override
        public <T> @NotNull Header<T> parse(@NotNull String string) throws ParseException, HeaderFormatException {
            if (!isCompatible(string)) {
                throw new ParseException("not a valid " + getVersion() + " header: " + string, -1);
            }

            @NotNull String[] parts = string.split(":\\s*", 2);
            @NotNull String name = parts[0];
            @NotNull String value = parts[1];

            //noinspection unchecked
            @NotNull HeaderKey<T> key = (HeaderKey<T>) HeaderKey.retrieve(name);
            return key.read(getVersion(), value);
        }

        @Override
        public <T> @NotNull String wrap(@NotNull Header<T> header) {
            return header.getName() + ": " + header.getKey().write(getVersion(), header);
        }

        @Override
        public boolean isCompatible(@NotNull String string) {
            return Pattern.compile(":\\s*").matcher(string).find();
        }
    };

    // Object

    private final @NotNull HttpVersion version;

    public HttpFactory1_1(@NotNull HttpVersion version) {
        this.version = version;
    }

    public @NotNull HttpVersion getVersion() {
        return version;
    }

    // Parsers

    @Override
    public @NotNull Request getRequest() {
        return request;
    }
    @Override
    public @NotNull Response getResponse() {
        return response;
    }
    @Override
    public @NotNull Headers getHeaders() {
        return headers;
    }

}
