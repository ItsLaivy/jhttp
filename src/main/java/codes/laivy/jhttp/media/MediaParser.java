package codes.laivy.jhttp.media;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.message.Content;
import org.jetbrains.annotations.NotNull;

public interface MediaParser<T, C extends Content<T>> {

    @NotNull C deserialize(@NotNull MediaType<T, C> media, @NotNull String string) throws MediaParserException;
    @NotNull String serialize(@NotNull C content);

    boolean validate(@NotNull MediaType<T, C> media, @NotNull String string);

}
