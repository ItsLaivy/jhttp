package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.EncodingException;
import org.jetbrains.annotations.NotNull;

public class IdentityEncoding extends Encoding {

    // Static initializers

    public static @NotNull Builder builder() {
        return new Builder();
    }

    // Object

    protected IdentityEncoding() {
        super("identity");
    }

    // Implementations

    @Override
    public @NotNull String decompress(@NotNull String string) {
        return string;
    }
    @Override
    public @NotNull String compress(@NotNull String string) throws EncodingException {
        return string;
    }

    // Classes

    public static final class Builder {

        private Builder() {
        }

        public @NotNull IdentityEncoding build() {
            return new IdentityEncoding();
        }

    }

}
