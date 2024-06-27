package codes.laivy.jhttp.protocol.v1_1;

import codes.laivy.jhttp.connection.HttpClient;
import codes.laivy.jhttp.content.Content;
import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.element.HttpProtocol;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.element.request.HttpRequest.Future;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.MissingHeaderException;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.exception.parser.request.HttpRequestParseException;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.headers.Headers;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.protocol.factory.HttpRequestFactory;
import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Stream;

import static codes.laivy.jhttp.Main.CRLF;
import static codes.laivy.jhttp.headers.HeaderKey.*;

final class HttpRequestFactory1_1 implements HttpRequestFactory {

    // Static initializers

    private static @NotNull URI uri(@NotNull String string) throws URISyntaxException {
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

    // Object

    private final @NotNull HttpVersion version;

    HttpRequestFactory1_1(@NotNull HttpVersion1_1 version) {
        this.version = version;
    }

    // Getters

    @Override
    public @NotNull HttpVersion getVersion() {
        return version;
    }

    // Modules

    @Override
    public @NotNull String serialize(@NotNull HttpRequest request) {
        if (!request.getVersion().equals(getVersion())) {
            throw new IllegalArgumentException("cannot serialize a '" + request.getVersion() + "' http request using a '" + getVersion() + "' http request factory");
        } else if (!request.getHeaders().contains(HeaderKey.HOST)) {
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
            builder.append(CRLF).append(getVersion().getHeaderFactory().serialize(header));
        }

        // End request configurations
        builder.append(CRLF).append(CRLF);

        // Write a message if exists
        if (request.getBody() != null) {
            builder.append(request.getBody());
        }

        // Finish
        return builder.toString();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public @NotNull HttpRequest parse(@NotNull String string) throws HttpRequestParseException {
        if (!string.contains(CRLF + CRLF)) {
            throw new HttpRequestParseException("http request missing conclusion (headers to body transition CRLFs)");
        }

        // Content
        @NotNull String[] content = string.split("\\s*" + CRLF + CRLF, 2);

        // Headers
        @NotNull RequestHeaders headers = new RequestHeaders();

        if (content[0].split(CRLF, 2).length > 1) {
            for (@NotNull String line : content[0].split(CRLF, 2)[1].split("\\s*" + CRLF)) {
                try {
                    @NotNull Header<?> header = getVersion().getHeaderFactory().parse(line);
                    if (!header.getKey().getTarget().isRequests()) continue;

                    headers.add(header);
                } catch (HeaderFormatException e) {
                    throw new HttpRequestParseException("cannot parse http request header line '" + line + "'", e);
                }
            }
        }

        // Validate host headers
        if (headers.get(HeaderKey.HOST).length == 0) {
            throw new HttpRequestParseException("the http 1.1 requests must have the 'Host' header", new MissingHeaderException(HeaderKey.HOST.getName()));
        } else if (headers.get(HeaderKey.HOST).length > 1) {
            throw new HttpRequestParseException("the http 1.1 requests cannot have multiples 'Host' headers", new HeaderFormatException(HeaderKey.HOST.getName()));
        }

        @NotNull Host host = headers.get(HeaderKey.HOST)[0].getValue();

        // Request line
        @NotNull String[] requestLine = content[0].split(CRLF, 2)[0].split(" ");
        @NotNull Method method = Method.valueOf(requestLine[0].toUpperCase());
        @Nullable URIAuthority authority = null;
        @NotNull URI uri;

        // Uri
        if (!requestLine[2].equals(getVersion().toString())) {
            throw new HttpRequestParseException("the http version '" + requestLine[2] + "' isn't compatible with the '" + getVersion() + "' http request parser");
        } else try {
            uri = uri(requestLine[1]);
        } catch (@NotNull URISyntaxException e) {
            throw new HttpRequestParseException("cannot parse uri '" + requestLine[1] + "' from http request", e);
        }

        // Authority
        if (URIAuthority.validate(requestLine[1])) try {
            authority = URIAuthority.parse(requestLine[1]);
        } catch (@NotNull URISyntaxException e) {
            throw new HttpRequestParseException("cannot parse uri authority '" + requestLine[1] + "' from http request", e);
        }

        // Message
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
            @NotNull Encoding[] encodings = new Encoding[0];
            boolean decoded = !headers.contains(CONTENT_ENCODING);

            if (!decoded) {
                @NotNull Deferred<Encoding>[] array = headers.get(CONTENT_ENCODING)[0].getValue();
                decoded = array.length == 0;

                // Only apply encoding if all the encodings are available (not pseudo)
                if (Arrays.stream(array).allMatch(Deferred::available)) {
                    encodings = Arrays.stream(array).map(Deferred::retrieve).toArray(Encoding[]::new);
                }
            }

            for (@NotNull Encoding encoding : encodings) {
                try {
                    pure = encoding.decompress(pure);
                } catch (@NotNull EncodingException e) {
                    throw new HttpRequestParseException("cannot decompress http request body using '" + encoding.getName() + "'", e);
                }

                decoded = true;
            }

            // Interpret Message
            if (StringUtils.isBlank(pure)) {
                body = null;
            } else try {
                @Nullable Content<?> contentBody = null;

                if (decoded && media != null) {
                    // noinspection unchecked
                    contentBody = ((MediaParser) media.getParser()).deserialize(media, pure);
                }

                body = HttpBody.create(contentBody, pure, content[1]);
            } catch (@NotNull MediaParserException e) {
                throw new HttpRequestParseException("cannot parse content body from http request", e);
            }
        }

        // Finish
        return HttpRequest.create(getVersion(), method, authority, uri, headers, body);
    }

    @Override
    public @NotNull Future parse(@NotNull HttpClient client, @NotNull String string) {
        return null;
    }

    @Override
    public boolean validate(@NotNull String string) {
        if (!string.contains(CRLF + CRLF)) {
            return false;
        }

        try {
            @NotNull String[] content = string.split("\\s*" + CRLF + CRLF, 2);

            // Check request line
            @NotNull String[] requestLine = content[0].split("\\s*" + CRLF, 2)[0].split("[\\s*]");
            @NotNull Method method = Method.valueOf(requestLine[0].toUpperCase());

            // Headers
            if (content[0].split("\\s*" + CRLF + "\\s*", 2).length > 1) {
                for (@NotNull String line : content[0].split("\\s*" + CRLF + "\\s*", 2)[1].split("\\s*" + CRLF + "\\s*")) {
                    if (!getVersion().getHeaderFactory().validate(line)) {
                        return false;
                    }
                }
            }

            // Finish
            return true;
        } catch (@NotNull Throwable throwable) {
            return false;
        }
    }

    // Classes

    private static final class RequestHeaders implements Headers {

        // Object

        private final @NotNull List<Header<?>> list = new LinkedList<>();

        RequestHeaders() {
        }

        // Natives

        @Override
        public @NotNull Header<?> @NotNull [] get(@NotNull String name) {
            return list.stream().filter(header -> header.getName().equalsIgnoreCase(name)).toArray(Header[]::new);
        }
        @Override
        public boolean contains(@NotNull String name) {
            return list.stream().anyMatch(header -> header.getName().equalsIgnoreCase(name));
        }
        @Override
        public @NotNull Stream<Header<?>> stream() {
            return list.stream();
        }
        @Override
        public int size() {
            return list.size();
        }
        @Override
        public boolean put(@NotNull Header<?> header) {
            remove(header.getKey());
            return add(header);
        }
        @Override
        public boolean add(@NotNull Header<?> header) {
            if (!header.getKey().getTarget().isRequests()) {
                throw new IllegalArgumentException("this header collection only accepts request headers!");
            }
            return list.add(header);
        }
        @Override
        public boolean remove(@NotNull Header<?> header) {
            return list.remove(header);
        }
        @Override
        public boolean remove(@NotNull HeaderKey<?> key) {
            return list.removeIf(header -> header.getName().equalsIgnoreCase(key.getName()));
        }
        @Override
        public boolean remove(@NotNull String name) {
            return list.removeIf(header -> header.getName().equalsIgnoreCase(name));
        }
        @Override
        public @NotNull Iterator<Header<?>> iterator() {
            return list.iterator();
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull RequestHeaders headers = (RequestHeaders) object;
            return Objects.equals(list, headers.list);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(list);
        }
        @Override
        public @NotNull String toString() {
            return list.toString();
        }

    }

}
