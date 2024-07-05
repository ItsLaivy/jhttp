package codes.laivy.jhttp.exception.parser.element;

import org.jetbrains.annotations.NotNull;

public class HttpBodyParseException extends Exception {
    public HttpBodyParseException(@NotNull String message) {
        super(message);
    }
    public HttpBodyParseException(@NotNull Throwable cause) {
        super(cause);
    }
    public HttpBodyParseException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}