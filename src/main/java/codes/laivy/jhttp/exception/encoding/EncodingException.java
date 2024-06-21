package codes.laivy.jhttp.exception.encoding;

import org.jetbrains.annotations.NotNull;

public class EncodingException extends Exception {
    public EncodingException(@NotNull Throwable cause) {
        super(cause);
    }
    public EncodingException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public EncodingException(@NotNull String message) {
        super(message);
    }
    public EncodingException() {
    }
}
