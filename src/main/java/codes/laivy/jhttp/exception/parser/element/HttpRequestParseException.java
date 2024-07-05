package codes.laivy.jhttp.exception.parser.element;

import org.jetbrains.annotations.NotNull;

public class HttpRequestParseException extends Exception {
    public HttpRequestParseException(@NotNull String message) {
        super(message);
    }
    public HttpRequestParseException(@NotNull Throwable cause) {
        super(cause);
    }
    public HttpRequestParseException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
