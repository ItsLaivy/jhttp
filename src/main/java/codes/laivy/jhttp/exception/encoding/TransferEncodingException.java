package codes.laivy.jhttp.exception.encoding;

import org.jetbrains.annotations.NotNull;

public class TransferEncodingException extends Exception {
    public TransferEncodingException(@NotNull Throwable cause) {
        super(cause);
    }
    public TransferEncodingException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public TransferEncodingException(@NotNull String message) {
        super(message);
    }
    public TransferEncodingException() {
    }
}
