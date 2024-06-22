package codes.laivy.jhttp.exception.encoding;

import org.jetbrains.annotations.NotNull;

public class UnknownEncodingException extends Exception {
    public UnknownEncodingException(@NotNull Throwable cause) {
        super(cause);
    }
    public UnknownEncodingException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public UnknownEncodingException(@NotNull String message) {
        super(message);
    }
    public UnknownEncodingException() {
    }
}
