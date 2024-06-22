package codes.laivy.jhttp.message;

import codes.laivy.jhttp.media.MediaType;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;

public interface Message extends CharSequence {

    // Static initializers

    static @NotNull StringMessage create(@NotNull Charset charset, @NotNull String message) {
        return new StringMessage(charset, message.getBytes(charset));
    }

    // Object

    @NotNull MediaType<?, ?> getMediaType();
    @NotNull Charset getCharset();

}
