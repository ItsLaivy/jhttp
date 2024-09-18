package codes.laivy.jhttp.exception.parser;

import org.jetbrains.annotations.NotNull;

public class IllegalHttpMethodException extends Exception {
    public IllegalHttpMethodException(@NotNull Throwable cause) {
        super(cause);
    }
    public IllegalHttpMethodException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public IllegalHttpMethodException(@NotNull String message) {
        super(message);
    }
    public IllegalHttpMethodException() {
    }
}
