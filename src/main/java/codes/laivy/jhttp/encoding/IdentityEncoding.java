package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.TransferEncodingException;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;

public class IdentityEncoding extends TransferEncoding {

    // Static initializers

    public static @NotNull Builder builder() {
        return new Builder();
    }

    // Object

    protected IdentityEncoding() {
        super("identity");
    }

    @Override
    public byte @NotNull [] decompress(@NotNull HttpVersion version, byte @NotNull [] bytes) {
        return bytes;
    }
    @Override
    public byte @NotNull [] compress(@NotNull HttpVersion version, byte @NotNull [] bytes) throws TransferEncodingException {
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
