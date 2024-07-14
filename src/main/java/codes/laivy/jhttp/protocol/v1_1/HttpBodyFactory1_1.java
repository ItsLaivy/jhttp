package codes.laivy.jhttp.protocol.v1_1;

import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.exception.parser.element.HttpBodyParseException;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.network.BitMeasure;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.protocol.factory.HttpBodyFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
            int length = (int) headers.get(CONTENT_LENGTH)[0].getValue().getBytes();
            content = content.substring(0, Math.min(content.length(), length));
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
            @NotNull HttpBody body = HttpBody.create(getVersion(), content.getBytes(StandardCharsets.ISO_8859_1));
            if (media != null) body.getContent(media);

            return body;
        } catch (@NotNull MediaParserException | @NotNull IOException e) {
            throw new HttpBodyParseException("cannot create http body", e);
        }
    }

    @Override
    public @NotNull String serialize(@NotNull HttpHeaders headers, @NotNull HttpBody body) throws IOException, EncodingException {
        @NotNull StringBuilder builder = new StringBuilder();
        long length = headers.first(CONTENT_LENGTH).map(HttpHeader::getValue).orElse(BitMeasure.create(BitMeasure.Level.BYTES, -1)).getBytes();

        // Stream
        try (
                @NotNull InputStream stream = body.getInputStream();
                @NotNull InputStreamReader reader = new InputStreamReader(stream)
        ) {
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

}
