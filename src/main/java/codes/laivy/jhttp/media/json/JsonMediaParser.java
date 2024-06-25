package codes.laivy.jhttp.media.json;

import codes.laivy.jhttp.content.Content;
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
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public final class JsonMediaParser implements MediaParser<JsonElement> {

    // Static initializers

    public static @NotNull JsonMediaParser create() {
        return new JsonMediaParser();
    }

    // Object

    private JsonMediaParser() {
    }

    // Modules

    @Override
    public @NotNull JsonContent deserialize(@NotNull MediaType<JsonElement> media, @NotNull String string, @NotNull Locale @NotNull ... locales) throws MediaParserException {
        try {
            // Reader
            @NotNull JsonReader reader = new JsonReader(new StringReader(string));
            reader.setStrictness(Strictness.LEGACY_STRICT);
            Streams.parse(reader);

            // Parse json element
            @NotNull JsonElement element = JsonParser.parseString(string);
            return new JsonContentImpl(media, element, locales);
        } catch (@NotNull JsonSyntaxException e) {
            throw new MediaParserException("cannot parse '" + string + "' as a valid json media", e);
        }
    }
    @Override
    public @NotNull String serialize(@NotNull Content<JsonElement> content) {
        return content.getData().toString();
    }

    @Override
    public boolean validate(@NotNull MediaType<JsonElement> media, @NotNull String string) {
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

        private final @NotNull MediaType<JsonElement> media;
        private final @NotNull JsonElement data;
        private final @NotNull Locale @NotNull [] locales;

        private JsonContentImpl(@NotNull MediaType<JsonElement> media, @NotNull JsonElement data, @NotNull Locale @NotNull ... locales) {
            this.media = media;
            this.data = data;
            this.locales = locales;
        }

        // Getters

        @Override
        public @NotNull MediaType<JsonElement> getMediaType() {
            return media;
        }
        @Override
        public @NotNull Locale @NotNull [] getLanguages() {
            return locales;
        }
        @Override
        public @NotNull JsonElement getData() {
            return data;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull JsonContentImpl that = (JsonContentImpl) object;
            return Objects.equals(getMediaType(), that.getMediaType()) && Objects.equals(getData(), that.getData()) && Arrays.equals(getLanguages(), that.getLanguages());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getMediaType(), getData(), Arrays.hashCode(getLanguages()));
        }

        @Override
        public @NotNull String toString() {
            return getData().toString();
        }

    }

}
