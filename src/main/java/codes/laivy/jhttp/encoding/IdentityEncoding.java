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
    public byte @NotNull [] decompress(byte @NotNull [] bytes) {
        return bytes;
    }
    @Override
    public byte @NotNull [] compress(byte @NotNull [] bytes) throws EncodingException {
        return bytes;
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
