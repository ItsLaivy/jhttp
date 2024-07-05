package codes.laivy.jhttp.media.json;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class JsonMediaType extends MediaType<JsonElement> {

    // Static initializers

    public static final @NotNull Type TYPE = new Type("application", "json");

    public static @NotNull MediaType<JsonElement> getInstance() {
        //noinspection unchecked
        return (MediaType<JsonElement>) MediaType.retrieve(TYPE).orElseThrow(() -> new NullPointerException("there's no media type '" + TYPE + "' registered on media type collections"));
    }

    // Object

    private final @Nullable MediaParser<JsonElement> parser;

    // Constructors

    public JsonMediaType() {
        super(TYPE, null, new Parameter[0]);
        this.parser = new Parser();
    }

    // Getters

    @Override
    public @Nullable MediaParser<JsonElement> getParser() {
        return parser;
    }

    // Classes

    private final class Parser implements MediaParser<JsonElement> {

        @Override
        public @NotNull MediaType<JsonElement> getMediaType() {
            return JsonMediaType.this;
        }

        @Override
        public @NotNull JsonElement deserialize(@NotNull InputStream stream) throws MediaParserException, IOException {
            return JsonParser.parseReader(new InputStreamReader(stream));
        }
        @Override
        public @NotNull InputStream serialize(@NotNull JsonElement content) {
            @Nullable Charset charset = getCharset() != null ? getCharset().orElse(null) : null;
            return new ByteArrayInputStream(charset != null ? content.toString().getBytes(charset) : content.toString().getBytes());
        }

    }

}
