package codes.laivy.jhttp.message;

import codes.laivy.jhttp.encoding.Encoding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

// todo: enhance whole message systems and remove that
public final class EncodedMessage implements Message {

    // Static initializers

    public static @NotNull EncodedMessage create(
            @NotNull Charset charset,
            @NotNull Encoding @NotNull [] encodings,
            @NotNull String pure,
            @NotNull String decoded
    ) {
        return new EncodedMessage(charset, encodings, pure, decoded);
    }

    // Object

    private final @NotNull Charset charset;
    private final @NotNull Encoding @NotNull [] encodings;

    private final @NotNull String pure;
    private final @NotNull String decoded;

    private EncodedMessage(
            @NotNull Charset charset,
            @NotNull Encoding @NotNull [] encodings,
            @NotNull String pure,
            @NotNull String decoded
    ) {
        this.charset = charset;
        this.encodings = encodings;
        this.pure = pure;
        this.decoded = decoded;
    }

    // Getters

    public @NotNull Encoding @NotNull [] getEncodings() {
        return encodings;
    }
    public @NotNull String getPure() {
        return pure;
    }
    public @NotNull String getDecoded() {
        return decoded;
    }

    // Natives

    @Override
    public @NotNull Charset getCharset() {
        return charset;
    }
    @Override
    public int length() {
        return getPure().length();
    }
    @Override
    public char charAt(int index) {
        return getPure().charAt(index);
    }
    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return getPure().subSequence(start, end);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull EncodedMessage that = (EncodedMessage) object;
        return Objects.equals(getCharset(), that.getCharset()) && Objects.deepEquals(getEncodings(), that.getEncodings()) && Objects.equals(getPure(), that.getPure());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getCharset(), Arrays.hashCode(getEncodings()), getPure());
    }

    @Override
    public @NotNull String toString() {
        return getPure();
    }

}
