package codes.laivy.jhttp.message;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;

public final class StringMessage implements Message {

    private final @NotNull Charset charset;
    private final byte @NotNull [] bytes;

    public StringMessage(@NotNull Charset charset, byte @NotNull [] bytes) {
        this.charset = charset;
        this.bytes = bytes;
    }

    // Getters

    @Override
    public @NotNull Charset getCharset() {
        return charset;
    }
    @Override
    public int length() {
        return toString().length();
    }
    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }
    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public @NotNull String toString() {
        return new String(bytes, getCharset());
    }

}
