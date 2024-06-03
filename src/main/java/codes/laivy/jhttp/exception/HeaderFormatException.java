package codes.laivy.jhttp.exception;

import org.jetbrains.annotations.NotNull;

public class HeaderFormatException extends Exception {
    public HeaderFormatException(@NotNull Throwable cause) {
        super(cause);
    }
    public HeaderFormatException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public HeaderFormatException(@NotNull String message) {
        super(message);
    }
    public HeaderFormatException() {
    }
}
