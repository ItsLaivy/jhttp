package codes.laivy.jhttp.exception;

import org.jetbrains.annotations.NotNull;

public final class TransferEncodingException extends Exception {
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
