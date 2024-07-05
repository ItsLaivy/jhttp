package codes.laivy.jhttp.media.text;

import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.Charset;

public class TextMediaType extends MediaType<String> {

    // Static initializers

    public static final @NotNull Type TYPE = new Type("text", "plain");

    public static @NotNull MediaType<String> getInstance() {
        //noinspection unchecked
        return (MediaType<String>) MediaType.retrieve(TYPE).orElseThrow(() -> new NullPointerException("there's no media type '" + TYPE + "' registered on media type collections"));
    }

    // Object

    private final @Nullable MediaParser<String> parser;

    // Constructors

    public TextMediaType() {
        super(TYPE, null, new Parameter[0]);
        this.parser = new Parser();
    }

    // Getters

    @Override
    public @Nullable MediaParser<String> getParser() {
        return parser;
    }

    // Classes

    private final class Parser implements MediaParser<String> {

        @Override
        public @NotNull MediaType<String> getMediaType() {
            return TextMediaType.this;
        }

        @Override
        public @NotNull String deserialize(@NotNull InputStream stream) throws IOException {
            @Nullable Charset charset = getCharset() != null ? getCharset().orElse(null) : null;
            @NotNull StringBuilder builder = new StringBuilder();

            try (@NotNull InputStreamReader reader = (charset != null ? new InputStreamReader(stream, charset) : new InputStreamReader(stream))) {
                while (reader.ready()) builder.append((char) reader.read());
            }

            return builder.toString();
        }
        @Override
        public @NotNull InputStream serialize(@NotNull String content) {
            @Nullable Charset charset = getCharset() != null ? getCharset().orElse(null) : null;

            if (charset != null) {
                return new ByteArrayInputStream(content.getBytes(charset));
            } else {
                return new ByteArrayInputStream(content.getBytes());
            }
        }

    }

}
