package codes.laivy.jhttp.utilities.pseudo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;
import java.util.function.Supplier;

public interface PseudoString<T> extends Pseudo<String, T> {

    // Static initializers

    static <T> @NotNull PseudoString<T> createUnavailable(@NotNull String raw) {
        return create(raw, () -> false, () -> null);
    }
    static <T> @NotNull PseudoString<T> createAvailable(@NotNull String raw, @UnknownNullability T pure) {
        return create(raw, () -> true, () -> pure);
    }

    static <T> @NotNull PseudoString<T> create(@NotNull String raw, @NotNull Supplier<Boolean> available, @NotNull Supplier<T> supplier) {
        return new PseudoString<T>() {
            @Override
            public @NotNull String raw() {
                return raw;
            }

            @Override
            public @UnknownNullability T retrieve() throws NullPointerException {
                if (!available()) {
                    throw new NullPointerException("pseudo data not available with raw value '" + this + "'");
                }
                return supplier.get();
            }

            @Override
            public boolean available() {
                return available.get();
            }

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (!(object instanceof Pseudo<?, ?>)) return false;
                Pseudo<?, ?> that = (Pseudo<?, ?>) object;
                return raw.equals(that.raw());
            }
            @Override
            public int hashCode() {
                return Objects.hash(raw);
            }

            @Override
            public @NotNull String toString() {
                return raw;
            }

        };
    }

}
