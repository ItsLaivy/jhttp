package codes.laivy.jhttp.exception;

import org.jetbrains.annotations.NotNull;

public class MissingHeaderException extends Exception {
    public MissingHeaderException(@NotNull Throwable cause) {
        super(cause);
    }
    public MissingHeaderException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public MissingHeaderException(@NotNull String message) {
        super(message);
    }
    public MissingHeaderException() {
    }
}
