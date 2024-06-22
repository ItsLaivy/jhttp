package codes.laivy.jhttp.media.text;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.message.Content;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull Content<String> deserialize(@NotNull MediaType<String> media, @NotNull String string) throws MediaParserException {
        return Content.create(media, string);
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
