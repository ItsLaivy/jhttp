package codes.laivy.jhttp.media.text;

import codes.laivy.jhttp.content.Content;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class TextMediaParser implements MediaParser<String> {

    // Static initializers

    public static @NotNull TextMediaParser create() {
        return new TextMediaParser();
    }

    // Object

    private TextMediaParser() {
    }

    // Getters

    @Override
    public @NotNull Content<String> deserialize(@NotNull MediaType<String> media, @NotNull String string, @NotNull Locale @NotNull ... locales) throws MediaParserException {
        return Content.create(media, string, locales);
    }
    @Override
    public @NotNull String serialize(@NotNull Content<String> content) {
        return content.getData();
    }

    @Override
    public boolean validate(@NotNull MediaType<String> media, @NotNull String string) {
        return true;
    }

}
