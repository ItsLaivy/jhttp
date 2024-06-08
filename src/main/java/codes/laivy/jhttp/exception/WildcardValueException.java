package codes.laivy.jhttp.exception;

import org.jetbrains.annotations.NotNull;

public class WildcardValueException extends RuntimeException {
    public WildcardValueException(@NotNull Throwable cause) {
        super(cause);
    }
    public WildcardValueException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public WildcardValueException(@NotNull String message) {
        super(message);
    }
    public WildcardValueException() {
    }
}
