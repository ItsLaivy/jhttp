package codes.laivy.jhttp.media.json;

import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.protocol.HttpVersion;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

public class JsonMediaType extends MediaType<JsonElement> {

    // Static initializers

    public static final @NotNull Type TYPE = new Type("application", "json");

    public static @NotNull MediaType<JsonElement> getInstance() {
        //noinspection unchecked
        @Nullable MediaType<JsonElement> media = (MediaType<JsonElement>) MediaType.retrieve(TYPE).orElse(null);
        if (media == null) media = new JsonMediaType();

        return media;
    }

    // Object

    public JsonMediaType() {
        super(TYPE, new Parser(), new Parameter[0]);
    }

    // Classes

    private static final class Parser implements MediaParser<JsonElement> {

        @Override
        public @NotNull JsonElement deserialize(@NotNull HttpVersion version, @NotNull InputStream stream, @NotNull Parameter @NotNull ... parameters) throws MediaParserException, IOException {
            @Nullable Parameter parameter = Arrays.stream(parameters).filter(p -> p.getKey().equalsIgnoreCase("charset")).findFirst().orElse(null);
            @Nullable Charset charset = parameter != null ? Deferred.charset(parameter.getValue()).orElse(null) : null;

            try {
                return JsonParser.parseReader(charset != null ? new InputStreamReader(stream, charset) : new InputStreamReader(stream));
            } catch (@NotNull JsonSyntaxException e) {
                throw new MediaParserException("cannot parse stream as a valid json element", e);
            }
        }
        @Override
        public @NotNull InputStream serialize(@NotNull HttpVersion version, @NotNull JsonElement content, @NotNull Parameter @NotNull ... parameters) {
            @Nullable Parameter parameter = Arrays.stream(parameters).filter(p -> p.getKey().equalsIgnoreCase("charset")).findFirst().orElse(null);
            @Nullable Charset charset = parameter != null ? Deferred.charset(parameter.getValue()).orElse(null) : null;

            return new ByteArrayInputStream(charset != null ? content.toString().getBytes(charset) : content.toString().getBytes());
        }

    }

}
