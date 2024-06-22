package codes.laivy.jhttp.media.json;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StringReader;
import java.util.Objects;

public final class JsonMediaParser implements MediaParser<JsonElement, JsonContent> {

    // Static initializers

    public static @NotNull JsonMediaParser create() {
        return new JsonMediaParser();
    }

    // Object

    private JsonMediaParser() {
    }

    // Modules

    @Override
    public @NotNull JsonContent deserialize(@NotNull MediaType<JsonElement, JsonContent> media, @NotNull String string) throws MediaParserException {
        try {
            // Reader
            @NotNull JsonReader reader = new JsonReader(new StringReader(string));
            reader.setStrictness(Strictness.LEGACY_STRICT);
            Streams.parse(reader);

            // Parse json element
            @NotNull JsonElement element = JsonParser.parseString(string);
            return new JsonContentImpl(media, element);
        } catch (@NotNull JsonSyntaxException e) {
            throw new MediaParserException("cannot parse '" + string + "' as a valid json media", e);
        }
    }
    @Override
    public @NotNull String serialize(@NotNull JsonContent content) {
        return content.getElement().toString();
    }

    @Override
    public boolean validate(@NotNull MediaType<JsonElement, JsonContent> media, @NotNull String string) {
        try {
            // Reader
            @NotNull JsonReader reader = new JsonReader(new StringReader(string));
            reader.setStrictness(Strictness.LEGACY_STRICT);
            Streams.parse(reader);

            // Parse json element
            @NotNull JsonElement element = JsonParser.parseString(string);
            return true;
        } catch (@NotNull JsonSyntaxException ignore) {
            return false;
        }
    }

    // Classes

    private static final class JsonContentImpl implements JsonContent {

        private final @NotNull MediaType<JsonElement, JsonContent> media;

        private final @NotNull String raw;
        private final @NotNull JsonElement element;

        private JsonContentImpl(@NotNull MediaType<JsonElement, JsonContent> media, @NotNull JsonElement element) {
            this.media = media;

            this.raw = element.toString();
            this.element = element;
        }

        // Getters

        @Override
        public @NotNull MediaType<JsonElement, JsonContent> getMediaType() {
            return media;
        }
        @Override
        public @NotNull JsonElement getElement() {
            return element;
        }
        @Override
        public @NotNull String getRaw() {
            return raw;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull JsonContentImpl that = (JsonContentImpl) object;
            return Objects.equals(media, that.media) && Objects.equals(raw, that.raw);
        }
        @Override
        public int hashCode() {
            return Objects.hash(media, raw);
        }

        @Override
        public @NotNull String toString() {
            return "JsonContentImpl{" +
                    "media=" + media +
                    ", raw='" + raw + '\'' +
                    ", element=" + element +
                    '}';
        }

    }

}
