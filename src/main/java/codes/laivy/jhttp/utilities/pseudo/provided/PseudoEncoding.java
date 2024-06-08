package codes.laivy.jhttp.utilities.pseudo.provided;

import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.utilities.pseudo.PseudoString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;

public final class PseudoEncoding implements PseudoString<Encoding> {

    // Static initializers

    public static @NotNull PseudoEncoding create(@NotNull String name) {
        return new PseudoEncoding(name);
    }
    public static @NotNull PseudoEncoding create(@NotNull Encoding encoding) {
        return new PseudoEncoding(encoding.getName());
    }

    // Object

    private final @NotNull PseudoString<Encoding> pseudo;

    private PseudoEncoding(@NotNull String name) {
        this.pseudo = PseudoString.create(name, () -> Encoding.contains(name), () -> Encoding.retrieve(name).orElseThrow(() -> new NullPointerException("there's no encoding with name '" + name + "' registered")));
    }
    private PseudoEncoding(@NotNull Encoding encoding) {
        this.pseudo = PseudoString.create(encoding.getName(), () -> true, () -> encoding);
    }

    // Pseudo Getters

    @Override
    @Contract(pure = true)
    public @NotNull String raw() {
        return pseudo.raw();
    }

    @Override
    public @UnknownNullability Encoding retrieve() throws NullPointerException {
        return pseudo.retrieve();
    }

    @Override
    public boolean available() {
        return pseudo.available();
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PseudoEncoding that = (PseudoEncoding) o;
        return Objects.equals(pseudo.raw(), that.pseudo.raw());
    }
    @Override
    public int hashCode() {
        return Objects.hash(pseudo.raw());
    }

    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        return pseudo.raw();
    }

}
