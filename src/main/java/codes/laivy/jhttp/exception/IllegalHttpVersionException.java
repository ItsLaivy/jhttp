package codes.laivy.jhttp.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalHttpVersionException extends Exception {
    public IllegalHttpVersionException(@NotNull Throwable cause) {
        super(cause);
    }
    public IllegalHttpVersionException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public IllegalHttpVersionException(@NotNull String message) {
        super(message);
    }
    public IllegalHttpVersionException() {
    }
}
