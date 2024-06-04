package codes.laivy.jhttp.utilities.pseudo.provided;

import codes.laivy.jhttp.utilities.pseudo.PseudoString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.nio.charset.Charset;
import java.util.Objects;

public final class PseudoCharset implements PseudoString<Charset> {

    // Static initializers

    public static @NotNull PseudoCharset create(@NotNull String name, @Nullable Float weight) {
        return new PseudoCharset(name, weight);
    }
    public static @NotNull PseudoCharset create(@NotNull Charset charset, @Nullable Float weight) {
        return new PseudoCharset(charset.name(), weight);
    }

    // Object

    private final @NotNull PseudoString<Charset> pseudo;
    private final @Nullable Float weight;

    private PseudoCharset(@NotNull String id, @Nullable Float weight) {
        this.pseudo = PseudoString.create(id, () -> {
            try {
                Charset.forName(id);
                return true;
            } catch (@NotNull IllegalArgumentException ignore) {
                return false;
            }
        }, () -> {
            try {
                return Charset.forName(id);
            } catch (@NotNull IllegalArgumentException ignore) {
                throw new NullPointerException("there's no charset named '" + id + "' registered");
            }
        });

        this.weight = weight;
    }
    private PseudoCharset(@NotNull Charset charset, @Nullable Float weight) {
        this.pseudo = PseudoString.create(charset.name(), () -> true, () -> charset);
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
    public @UnknownNullability Charset retrieve() throws NullPointerException {
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
        PseudoCharset that = (PseudoCharset) o;
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
