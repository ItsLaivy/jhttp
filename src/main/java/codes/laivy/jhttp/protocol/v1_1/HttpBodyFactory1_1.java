package codes.laivy.jhttp.protocol.v1_1;

import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.exception.parser.element.HttpBodyParseException;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.media.Content;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.network.BitMeasure;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.protocol.factory.HttpBodyFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static codes.laivy.jhttp.headers.HttpHeaderKey.*;

final class HttpBodyFactory1_1 implements HttpBodyFactory {

    private final @NotNull HttpVersion version;

    public HttpBodyFactory1_1(@NotNull HttpVersion1_1 version) {
        this.version = version;
    }

    // Getters

    @Override
    public @NotNull HttpVersion getVersion() {
        return version;
    }

    // Modules

    @Override
    public @NotNull HttpBody parse(@NotNull HttpHeaders headers, @NotNull String content) throws HttpBodyParseException {
        // Content Type
        @Nullable MediaType<?> media = null;
        if (headers.contains(CONTENT_TYPE)) {
            media = headers.get(CONTENT_TYPE)[0].getValue();
        }

        if (headers.contains(CONTENT_LENGTH)) {
            int contentLength = (int) headers.get(CONTENT_LENGTH)[0].getValue().getBytes();
            content = content.substring(0, contentLength);
        }

        // Message Encoding
        if (headers.contains(CONTENT_ENCODING)) {
            // Verify encodings
            for (@NotNull Deferred<Encoding> encoding : headers.get(CONTENT_ENCODING)[0].getValue()) {
                if (encoding.available()) continue;
                throw new HttpBodyParseException("content encoding not accepted '" + encoding + "'");
            }

            // Decode
            @NotNull Encoding[] encodings = Arrays.stream(headers.get(CONTENT_ENCODING)).flatMap(header -> Arrays.stream(header.getValue())).map(Deferred::retrieve).toArray(Encoding[]::new);

            // Decompress
            for (@NotNull Encoding encoding : encodings) {
                try {
                    content = encoding.decompress(content);
                } catch (@NotNull EncodingException e) {
                    throw new HttpBodyParseException("cannot decompress content using encoding '" + encoding.getName() + "'", e);
                }
            }
        }

        // Interpret Message
        try {
            return create(media, new ByteArrayInputStream(content.getBytes()));
        } catch (@NotNull MediaParserException | @NotNull IOException e) {
            throw new HttpBodyParseException("cannot create http body", e);
        }
    }

    @Override
    public @NotNull String serialize(@NotNull HttpHeaders headers, @NotNull HttpBody body) throws IOException, EncodingException {
        @NotNull StringBuilder builder = new StringBuilder();
        long length = headers.first(CONTENT_LENGTH).map(HttpHeader::getValue).orElse(BitMeasure.create(BitMeasure.Level.BYTES, -1)).getBytes();

        // Stream
        @NotNull InputStream stream = body.getInputStream();
        stream.reset();

        try (@NotNull InputStreamReader reader = new InputStreamReader(stream)) {
            while (reader.ready()) {
                if (length != -1 && builder.length() > length) break;
                builder.append((char) stream.read());
            }
        }

        @NotNull String string = builder.toString();

        // Message Encoding
        if (headers.contains(CONTENT_ENCODING)) {
            // Verify encodings
            for (@NotNull Deferred<Encoding> encoding : headers.get(CONTENT_ENCODING)[0].getValue()) {
                if (encoding.available()) continue;
                throw new EncodingException("content encoding not accepted '" + encoding + "'");
            }

            // Decode
            @NotNull Encoding[] encodings = Arrays.stream(headers.get(CONTENT_ENCODING)).flatMap(header -> Arrays.stream(header.getValue())).map(Deferred::retrieve).toArray(Encoding[]::new);

            // Decompress
            for (@NotNull Encoding encoding : encodings) {
                string = encoding.compress(string);
            }
        }

        // Finish
        return string;
    }

    // Modules

    @Override
    public @NotNull HttpBody create(@Nullable MediaType<?> type, @NotNull InputStream stream) throws MediaParserException, IOException {
        return new HttpBodyImpl(type, stream);
    }

    // Classes

    private static final class HttpBodyImpl implements HttpBody {

        private final @NotNull Map<MediaType<?>, Content<?>> contentMap = new HashMap<>();
        private final @NotNull Object lock = new Object();

        private volatile @UnknownNullability InputStream stream;

        private HttpBodyImpl(@Nullable MediaType<?> type, @NotNull InputStream stream) throws MediaParserException, IOException {
            this.stream = stream;

            if (type != null) {
                @NotNull Content<?> content = content(type, stream);
                contentMap.put(type, content);
            }
        }

        // Getters

        @Override
        public @NotNull <T> Content<T> getContent(@NotNull MediaType<T> mediaType) throws MediaParserException, IOException {
            if (stream == null) {
                throw new IllegalStateException("this http body is closed");
            } else if (contentMap.containsKey(mediaType)) {
                //noinspection unchecked
                return (Content<T>) contentMap.get(mediaType);
            } else {
                @NotNull InputStream stream = getInputStream();
                stream.reset();

                @NotNull Content<T> content = content(mediaType, stream);

                synchronized (lock) {
                    contentMap.put(mediaType, content);
                }

                return content;
            }
        }
        @Override
        public @NotNull InputStream getInputStream() {
            if (stream == null) {
                throw new IllegalStateException("this http body is closed");
            }

            synchronized (lock) {
                return stream;
            }
        }

        // Loaders

        @Override
        public void close() throws IOException {
            if (stream == null) {
                throw new IllegalStateException("this http body is already closed");
            }

            synchronized (lock) {
                contentMap.clear();
                stream.close();
                stream = null;
            }
        }

        // Modules

        private <T> @NotNull Content<T> content(@NotNull MediaType<T> type, @NotNull InputStream inputStream) throws MediaParserException, IOException {
            @NotNull HttpBody body = this;
            @NotNull AtomicReference<T> reference = new AtomicReference<>(type.getParser().deserialize(inputStream));

            return new Content<T>() {

                @Override
                public @NotNull MediaType<T> getMediaType() {
                    return type;
                }

                @Override
                public @NotNull HttpBody getBody() {
                    return body;
                }

                @Override
                public @NotNull T getData() {
                    return reference.get();
                }
                @Override
                public void setData(@NotNull T data, boolean autoFlush) throws IOException {
                    reference.set(data);
                    if (autoFlush) flush();
                }

                @Override
                public void flush() throws IOException {
                    synchronized (lock) {
                        stream.close();
                        stream = getMediaParser().serialize(getData());
                    }
                }

            };
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            // todo: this body can only match with HttpBodyImpl,
            //  it should be compatible with all bodies with the equals input stream
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull HttpBodyImpl httpBody = (HttpBodyImpl) object;
            return Objects.equals(toString(), httpBody.toString());
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(stream);
        }

        @Override
        public @NotNull String toString() {
            @NotNull StringBuilder builder = new StringBuilder();

            try {
                @NotNull InputStream stream = getInputStream();
                stream.reset();

                try (@NotNull InputStreamReader reader = new InputStreamReader(stream)) {
                    while (reader.ready()) builder.append((char) reader.read());
                }
            } catch (IOException e) {
                throw new RuntimeException("cannot convert http body into a string", e);
            }

            return builder.toString();
        }

    }

}
