package codes.laivy.jhttp.exception;

import org.jetbrains.annotations.NotNull;

public final class DeferredException extends NullPointerException {

    public DeferredException() {
    }
    public DeferredException(@NotNull String s) {
        super(s);
    }

}
