package codes.laivy.jhttp.exception;

import org.jetbrains.annotations.NotNull;

public class ServerNotBindingException extends Exception {
    public ServerNotBindingException(@NotNull Throwable cause) {
        super(cause);
    }
    public ServerNotBindingException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
    public ServerNotBindingException(@NotNull String message) {
        super(message);
    }
    public ServerNotBindingException() {
    }
}
