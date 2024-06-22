package codes.laivy.jhttp.exception.media;

import org.jetbrains.annotations.NotNull;

public class MediaParserException extends Exception {
    public MediaParserException(@NotNull Throwable cause) {
        super(cause);
    }
    public MediaParserException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public MediaParserException(@NotNull String message) {
        super(message);
    }
    public MediaParserException() {
    }
}
