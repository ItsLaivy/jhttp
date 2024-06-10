package codes.laivy.jhttp.exception.parser;

import org.jetbrains.annotations.NotNull;

public class FilesystemProtocolException extends Exception {
    public FilesystemProtocolException(@NotNull Throwable cause) {
        super(cause);
    }
    public FilesystemProtocolException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public FilesystemProtocolException(@NotNull String message) {
        super(message);
    }
    public FilesystemProtocolException() {
    }
}
