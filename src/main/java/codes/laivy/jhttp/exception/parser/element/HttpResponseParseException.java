package codes.laivy.jhttp.exception.parser.element;

import org.jetbrains.annotations.NotNull;

public class HttpResponseParseException extends Exception {
    public HttpResponseParseException(@NotNull String message) {
        super(message);
    }
    public HttpResponseParseException(@NotNull Throwable cause) {
        super(cause);
    }
    public HttpResponseParseException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
