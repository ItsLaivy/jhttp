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

    public static @NotNull PseudoEncoding create(@NotNull String name, @Nullable Float weight) {
        return new PseudoEncoding(name, weight);
    }
    public static @NotNull PseudoEncoding create(@NotNull Encoding encoding, @Nullable Float weight) {
        return new PseudoEncoding(encoding.getName(), weight);
    }

    // Object

    private final @NotNull PseudoString<Encoding> pseudo;
    private final @Nullable Float weight;

    private PseudoEncoding(@NotNull String name, @Nullable Float weight) {
        this.pseudo = PseudoString.create(name, () -> Encoding.contains(name), () -> Encoding.retrieve(name).orElseThrow(() -> new NullPointerException("there's no encoding with name '" + name + "' registered")));
        this.weight = weight;
    }
    private PseudoEncoding(@NotNull Encoding encoding, @Nullable Float weight) {
        this.pseudo = PseudoString.create(encoding.getName(), () -> true, () -> encoding);
        this.weight = weight;
    }

    // Getters

    @Contract(pure = true)
    public @Nullable Float getWeight() {
        return weight;
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
        return Objects.equals(pseudo.raw(), that.pseudo.raw()) && Objects.equals(weight, that.weight);
    }
    @Override
    public int hashCode() {
        return Objects.hash(pseudo.raw(), weight);
    }

    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        return pseudo.raw() + (weight != null ? ";q=" + weight : "");
    }

}
