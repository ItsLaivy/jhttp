package codes.laivy.jhttp.protocol.v1_0;

import codes.laivy.jhttp.client.HttpClient;
import codes.laivy.jhttp.content.Content;
import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.Target;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.element.response.HttpResponse.Future;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.exception.parser.request.HttpResponseParseException;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.protocol.factory.HttpResponseFactory;
import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

import static codes.laivy.jhttp.Main.CRLF;
import static codes.laivy.jhttp.headers.HttpHeaderKey.*;

final class HttpResponseFactory1_0 implements HttpResponseFactory {

    // Object

    private final @NotNull HttpVersion1_0 version;
    private final @NotNull Map<HttpClient, FutureImpl> futures;

    HttpResponseFactory1_0(@NotNull HttpVersion1_0 version) {
        this.version = version;
        this.futures = new HashMap<>();
    }

    // Getters

    @Override
    public @NotNull HttpVersion1_0 getVersion() {
        return version;
    }

    @Override
    public @NotNull HttpResponse create(@NotNull HttpStatus status, @NotNull HttpHeaders headers, @NotNull HttpBody body) {
        return new HttpResponseImpl(status, headers, body);
    }

    // Modules

    @Override
    public @NotNull String serialize(@NotNull HttpResponse response) {
        if (!response.getVersion().equals(getVersion())) {
            throw new IllegalArgumentException("cannot serialize a '" + response.getVersion() + "' http response using a '" + getVersion() + "' http response factory");
        }

        @NotNull StringBuilder builder = new StringBuilder(getVersion() + " " + response.getStatus().getCode() + " " + response.getStatus().getMessage() + CRLF);

        // Write headers
        for (@NotNull HttpHeader<?> header : response.getHeaders()) {
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
        @NotNull HttpHeaders headers = getVersion().getHeaderFactory().createMutable(Target.RESPONSE);

        if (content[0].split(CRLF, 2).length > 1) {
            for (@NotNull String headerString : content[0].split(CRLF, 2)[1].split("\\s*" + CRLF)) {
                try {
                    @NotNull HttpHeader<?> header = getVersion().getHeaderFactory().parse(headerString);
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

            // Check version
            if (!requestLine[0].equalsIgnoreCase(getVersion().toString())) {
                return false;
            }

            // Check status code
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

    private final class HttpResponseImpl implements HttpResponse {

        private final @NotNull HttpStatus status;
        private final @NotNull HttpHeaders headers;
        private final @Nullable HttpBody body;

        private HttpResponseImpl(
                @NotNull HttpStatus status,
                @NotNull HttpHeaders headers,
                @Nullable HttpBody body
        ) {
            this.status = status;
            this.headers = headers;
            this.body = body;

            if (status.equals(HttpStatus.CONTINUE)) {
                throw new UnsupportedOperationException("the http 1.0 responses doesn't supports the CONTINUE status code");
            }
        }

        // Getters

        @Override
        public @NotNull HttpStatus getStatus() {
            return status;
        }
        @Override
        public @NotNull HttpVersion getVersion() {
            return version;
        }
        @Override
        public @NotNull HttpHeaders getHeaders() {
            return headers;
        }
        @Override
        public @Nullable HttpBody getBody() {
            return body;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof HttpResponse)) return false;
            @NotNull HttpResponse that = (HttpResponse) object;
            return Objects.equals(getStatus(), that.getStatus()) && Objects.equals(getHeaders(), that.getHeaders()) && Objects.equals(getBody(), that.getBody());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getStatus(), getHeaders(), getBody());
        }
        @Override
        public @NotNull String toString() {
            return serialize(this);
        }

    }
    private final class FutureImpl implements Future {

        private final @NotNull CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        private @UnknownNullability ScheduledFuture<?> timeout;

        private final @NotNull HttpClient client;

        private final @NotNull HttpVersion1_0 version;
        private final @NotNull HttpStatus status;
        private final @NotNull HttpHeaders headers;

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

            this.version = HttpResponseFactory1_0.this.getVersion();
            this.status = request.getStatus();
            this.headers = getVersion().getHeaderFactory().createImmutable(request.getHeaders());

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
        public @NotNull HttpHeaders getHeaders() {
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

        // Future

        @Override
        @Contract("_->this")
        public @NotNull Future whenComplete(@NotNull BiConsumer<? super HttpResponse, ? super Throwable> action) {
            future.whenComplete(action);
            return this;
        }
        @Override
        @Contract("_->this")
        public @NotNull Future orTimeout(@NotNull Duration duration) {
            // Schedule a timeout task
            if (timeout != null) timeout.cancel(true);
            timeout = HttpVersion1_0.FUTURE_TIMEOUT_SCHEDULED.schedule(() -> {
                cancel(true);
            }, duration.toMillis(), TimeUnit.MILLISECONDS);

            whenComplete((value, exception) -> {
                timeout.cancel(false);
            });

            // Finish
            return this;
        }

    }

}
