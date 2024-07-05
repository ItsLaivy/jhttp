package codes.laivy.jhttp.media.text;

import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

public class TextMediaType extends MediaType<String> {

    // Static initializers

    public static final @NotNull Type TYPE = new Type("text", "plain");

    public static @NotNull MediaType<String> getInstance() {
        //noinspection unchecked
        return (MediaType<String>) MediaType.retrieve(TYPE).orElseThrow(() -> new NullPointerException("there's no media type '" + TYPE + "' registered on media type collections"));
    }

    // Object

    public TextMediaType() {
        super(TYPE, new Parser(), new Parameter[0]);
    }

    // Classes

    private static final class Parser implements MediaParser<String> {

        @Override
        public @NotNull String deserialize(@NotNull InputStream stream, @NotNull Parameter @NotNull ... parameters) throws IOException {
            @Nullable Parameter parameter = Arrays.stream(parameters).filter(p -> p.getKey().equalsIgnoreCase("charset")).findFirst().orElse(null);
            @Nullable Charset charset = parameter != null ? Deferred.charset(parameter.getValue()).orElse(null) : null;

            @NotNull StringBuilder builder = new StringBuilder();

            try (@NotNull InputStreamReader reader = (charset != null ? new InputStreamReader(stream, charset) : new InputStreamReader(stream))) {
                while (reader.ready()) builder.append((char) reader.read());
            }

            return builder.toString();
        }
        @Override
        public @NotNull InputStream serialize(@NotNull String content, @NotNull Parameter @NotNull ... parameters) {
            @Nullable Parameter parameter = Arrays.stream(parameters).filter(p -> p.getKey().equalsIgnoreCase("charset")).findFirst().orElse(null);
            @Nullable Charset charset = parameter != null ? Deferred.charset(parameter.getValue()).orElse(null) : null;

            if (charset != null) {
                return new ByteArrayInputStream(content.getBytes(charset));
            } else {
                return new ByteArrayInputStream(content.getBytes());
            }
        }

    }

}
