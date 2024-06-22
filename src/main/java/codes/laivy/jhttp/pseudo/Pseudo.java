package codes.laivy.jhttp.pseudo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * This interface represents a data that may not be available.
 * The `raw` method returns the raw data that will always be available for this data,
 * like a reference or ID. The `retrieve` method retrieves this data using the raw value.
 * The `available` method returns whether this pseudo data is available for retrieval.
 * <p>
 * Example: In a Pseudo<String, Charset>, the String represents the charset ID,
 * and the charset may not exist on the operating system, so it is the final type.
 * The String is the raw data, the charset ID.
 *
 * @param <R> The raw data type
 * @param <T> The final type that may not be available
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface Pseudo<R, T> {

    // Static initializers

    static <R, T> @NotNull Pseudo<R, T> createUnavailable(@NotNull R raw) {
        return create(raw, () -> false, () -> null);
    }
    static <R, T> @NotNull Pseudo<R, T> createAvailable(@NotNull R raw, @UnknownNullability T pure) {
        return create(raw, () -> true, () -> pure);
    }

    static <R, T> @NotNull Pseudo<R, T> create(@NotNull R raw, @NotNull Supplier<Boolean> available, @NotNull Supplier<T> supplier) {
        return new Pseudo<R, T>() {
            @Override
            public @NotNull R raw() {
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
                return raw.toString();
            }
            
        };
    }

    // Object

    /**
     * Returns the raw data that will always be available.
     *
     * @return The raw data
     */
    @NotNull R raw();

    /**
     * Retrieves the data represented by this pseudo.
     *
     * @return The retrieved data
     * @throws NullPointerException if this data isn't available
     */
    @UnknownNullability T retrieve() throws NullPointerException;

    /**
     * Checks if the data represented by this pseudo is available for retrieval.
     *
     * @return True if available, false otherwise
     */
    boolean available();

}
