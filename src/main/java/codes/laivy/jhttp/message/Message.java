package codes.laivy.jhttp.message;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;

public interface Message extends CharSequence {

    // Static initializers

    static @NotNull StringMessage create(@NotNull Charset charset, @NotNull String message) {
        return new StringMessage(charset, message.getBytes(charset));
    }

    // Object

    @NotNull Charset getCharset();

}
