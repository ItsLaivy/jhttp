package codes.laivy.jhttp.message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class BlankMessage implements Message {

    // Static initializers

    public static @NotNull BlankMessage create(@NotNull Charset charset) {
        return new BlankMessage(charset);
    }
    public static @NotNull BlankMessage create() {
        return new BlankMessage(StandardCharsets.UTF_8);
    }

    // Object

    private final @NotNull Charset charset;

    private BlankMessage(@NotNull Charset charset) {
        this.charset = charset;
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

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull BlankMessage that = (BlankMessage) object;
        return Objects.equals(charset, that.charset);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(charset);
    }

    @Override
    public @NotNull String toString() {
        return new String(new byte[0], getCharset());
    }

}
