package codes.laivy.jhttp.protocol.v1_1;

import codes.laivy.jhttp.connection.HttpClient;
import codes.laivy.jhttp.content.Content;
import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.element.response.HttpResponse.Future;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.exception.parser.request.HttpResponseParseException;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.headers.Headers;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.protocol.factory.HttpResponseFactory;
import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static codes.laivy.jhttp.Main.CRLF;
import static codes.laivy.jhttp.headers.HeaderKey.*;

final class HttpResponseFactory1_1 implements HttpResponseFactory {

    // Object

    private final @NotNull HttpVersion version;
    private final @NotNull Map<HttpClient, FutureImpl> futures;

    HttpResponseFactory1_1(@NotNull HttpVersion1_1 version) {
        this.version = version;
        this.futures = new HashMap<>();
    }

    // Getters

    @Override
    public @NotNull HttpVersion getVersion() {
        return version;
    }

    // Modules

    @Override
    public @NotNull String serialize(@NotNull HttpResponse response) {
        if (!response.getVersion().equals(getVersion())) {
            throw new IllegalArgumentException("cannot serialize a '" + response.getVersion() + "' http response using a '" + getVersion() + "' http response factory");
        }

        @NotNull StringBuilder builder = new StringBuilder(getVersion() + " " + response.getStatus().getCode() + " " + response.getStatus().getMessage() + CRLF);

        // Write headers
        for (@NotNull Header<?> header : response.getHeaders()) {
            if (!header.getKey().getTarget().isResponses()) continue;
            builder.append(getVersion().getHeaderFactory().serialize(header)).append(CRLF);
        }

        // End request configurations
        builder.append(CRLF);
        
        // Write a message if exists
        if (response.getBody() != null) {
            builder.append(response.getBody());
        }

        return builder.toString();
    }

    @SuppressWarnings("rawtypes")
    public @NotNull HttpResponse parse(@NotNull String string, boolean parseMedia) throws HttpResponseParseException {
        if (!string.contains(CRLF + CRLF)) {
            throw new HttpResponseParseException("http request missing conclusion (headers to body transition CRLFs)");
        }

        // Content
        @NotNull String[] content = string.split("\\s*" + CRLF + CRLF, 2);
        @NotNull String[] line = content[0].split(CRLF, 2)[0].split(" ", 3);

        int code = Integer.parseInt(line[1]);
        @NotNull HttpStatus status = HttpStatus.getByCode(code);

        // Retrieve headers
        @NotNull ResponseHeaders headers = new ResponseHeaders();

        if (content[0].split(CRLF, 2).length > 1) {
            for (@NotNull String headerString : content[0].split(CRLF, 2)[1].split("\\s*" + CRLF)) {
                try {
                    @NotNull Header<?> header = getVersion().getHeaderFactory().parse(headerString);
                    if (!header.getKey().getTarget().isResponses()) continue;

                    headers.add(header);
                } catch (HeaderFormatException e) {
                    throw new HttpResponseParseException("cannot parse http response header line '" + headerString + "'", e);
                }
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
                    throw new HttpResponseParseException("cannot decompress http response body using '" + encoding.getName() + "'", e);
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
                throw new HttpResponseParseException("cannot parse content body from http response", e);
            }
        }

        // Finish
        return HttpResponse.create(getVersion(), status, headers, body);
    }
    @Override
    public @NotNull HttpResponse parse(@NotNull String string) throws HttpResponseParseException {
        return parse(string, true);
    }

    @Override
    public @NotNull Future parse(@NotNull HttpClient client, @NotNull String string) throws HttpResponseParseException {
        @NotNull FutureImpl future;

        if (futures.containsKey(client)) {
            future = futures.get(client);
            future.feed(string);
        } else {
            future = new FutureImpl(client, string);
            futures.put(client, future);
        }

        return future;
    }

    @Override
    public boolean validate(@NotNull String string) {
        if (!string.contains(CRLF + CRLF)) {
            return false;
        }

        try {
            @NotNull String[] content = string.split("\\s*" + CRLF + CRLF, 2);

            // Check response line
            @NotNull String[] requestLine = content[0].split("\\s*" + CRLF, 2)[0].split("[\\s*]");
            Integer.parseInt(requestLine[1]);

            // Check headers
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

    private static class ResponseHeaders implements Headers {

        // Object

        protected final @NotNull List<Header<?>> list = new LinkedList<>();

        ResponseHeaders() {
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
            if (!header.getKey().getTarget().isResponses()) {
                throw new IllegalArgumentException("this header collection only accepts response headers!");
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
            @NotNull ResponseHeaders headers = (ResponseHeaders) object;
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
    private static final class ImmutableHeaders extends ResponseHeaders {

        private ImmutableHeaders(@NotNull Headers headers) {
            for (@NotNull Header<?> header : headers) {
                list.add(header);
            }
        }

        @Override
        public boolean add(@NotNull Header<?> header) {
            throw new UnsupportedOperationException("you cannot change the headers of a future request");
        }

        @Override
        public boolean remove(@NotNull Header<?> header) {
            throw new UnsupportedOperationException("you cannot change the headers of a future request");
        }

        @Override
        public boolean remove(@NotNull HeaderKey<?> key) {
            throw new UnsupportedOperationException("you cannot change the headers of a future request");
        }

        @Override
        public boolean remove(@NotNull String name) {
            throw new UnsupportedOperationException("you cannot change the headers of a future request");
        }
    }

    private final class FutureImpl implements Future {

        private final @NotNull CompletableFuture<HttpResponse> future = new CompletableFuture<>();

        private final @NotNull HttpClient client;

        private final @NotNull HttpVersion version;
        private final @NotNull HttpStatus status;
        private final @NotNull Headers headers;

        private @NotNull String body;

        public FutureImpl(
                @NotNull HttpClient client,
                @NotNull String body
        ) throws HttpResponseParseException {
            this.client = client;
            this.body = body;

            // Future checkers
            future.whenComplete((done, exception) -> {
                futures.remove(client);
            });

            // Request
            @NotNull HttpResponse request = parse(body, false);
            this.version = request.getVersion();
            this.status = request.getStatus();
            this.headers = new ImmutableHeaders(request.getHeaders());

            // Security
            check();
        }

        // Getters

        @Override
        public @NotNull HttpClient getClient() {
            return client;
        }
        @Override
        public @NotNull HttpVersion getVersion() {
            return version;
        }
        @Override
        public @NotNull HttpStatus getStatus() {
            return status;
        }
        @Override
        public @NotNull Headers getHeaders() {
            return headers;
        }

        // Modules

        private void check() {
            try {
                // Chunked Encoding
                if (getHeaders().contains(TRANSFER_ENCODING)) {
                    @NotNull Deferred<Encoding>[] encodings = getHeaders().get(TRANSFER_ENCODING)[0].getValue();
                    @NotNull Deferred<Encoding> deferred = encodings[encodings.length - 1];

                    if (deferred.available() && deferred.toString().equalsIgnoreCase("chunked")) {
                        @NotNull Encoding encoding = deferred.retrieve();

                        if (body.endsWith("0\r\n\r\n")) {
                            body = encoding.decompress(body);


                            // Completes the message with the new body
                            @NotNull HttpResponse response = parse(getAsString());
                            future.complete(response);

                            return;
                        }
                    }
                }

                // Content Length
                if (getHeaders().contains(CONTENT_LENGTH)) {
                    // Check if the content length matches with the current body length and finish it true
                    // Also complete exceptionally if the current body is higher than the required
                    long required = getHeaders().get(CONTENT_LENGTH)[0].getValue().getBytes();

                    if (required <= body.length()) {
                        body = this.body.substring(0, (int) required);

                        // Completes the message with the new body
                        @NotNull HttpResponse response = parse(getAsString());
                        future.complete(response);
                    }
                } else {
                    // Completes the message; without the chunked transfer encoding or content length,
                    // there's no reason to continue with the http response future

                    @NotNull HttpResponse response = parse(getAsString());
                    future.complete(response);
                }
            } catch (@NotNull Throwable throwable) {
                // Any error report to the future
                future.completeExceptionally(throwable);
            }
        }
        public void feed(@NotNull String body) {
            this.body += body;
            check();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return future.cancel(mayInterruptIfRunning);
        }
        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }
        @Override
        public boolean isDone() {
            return future.isDone();
        }

        @Override
        public @NotNull HttpResponse get() throws InterruptedException, ExecutionException {
            return future.get();
        }
        @Override
        public @NotNull HttpResponse get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return future.get(timeout, unit);
        }

        // Implementations

        @Override
        public @NotNull String getAsString() {
            return body;
        }

        @Override
        public @NotNull String toString() {
            return "FutureImpl{" +
                    "future=" + future +
                    '}';
        }

    }

}
