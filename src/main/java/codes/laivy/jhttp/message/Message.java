package codes.laivy.jhttp.message;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;

public interface Message {

    @NotNull Charset getCharset();

    byte[] getContent();

    long size();

    static @NotNull StringMessage create(@NotNull String message, @NotNull Charset charset) {
        byte[] bytes = message.getBytes();
        return new StringMessage(bytes, charset);
    }

}
