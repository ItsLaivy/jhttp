package codes.laivy.jhttp.deferred;

import codes.laivy.jhttp.encoding.Encoding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Objects;

/**
 * A deferred class represents data that is not yet available but can be accessed
 * through its raw string representation.
 *
 * @param <T> The type of data that will be retrieved.
 */
public class Deferred<T> {

    // Static initializers

    public static @NotNull Deferred<Encoding> encoding(final @NotNull String name) {
        return new Deferred<Encoding>(name) {
            @Override
            public @NotNull Encoding retrieve() {
                data = Encoding.retrieve(name).orElse(null);
                return super.retrieve();
            }
            @Override
            public boolean available() {
                data = Encoding.retrieve(name).orElse(null);
                return super.available();
            }
        };
    }
    public static @NotNull Deferred<Charset> charset(@NotNull String name) {
        return new Deferred<Charset>(name) {
            private void sync() {
                try {
                    data = Charset.forName(name);
                } catch (@NotNull UnsupportedCharsetException ignore) {
                    data = null;
                }
            }

            @Override
            public @NotNull Charset retrieve() {
                sync();
                return super.retrieve();
            }
            @Override
            public boolean available() {
                sync();
                return super.available();
            }

        };
    }

    // Object

    private final @NotNull String raw;
    protected @Nullable T data;

    /**
     * Constructs a new Deferred instance with the given raw string.
     *
     * @param raw The raw string representing the deferred data.
     */
    public Deferred(@NotNull String raw) {
        this.raw = raw;
    }

    /**
     * Constructs a new Deferred instance with the given raw string and initial data.
     *
     * @param raw  The raw string representing the deferred data.
     * @param data The initial data to be stored in the Deferred.
     */
    public Deferred(@NotNull String raw, @Nullable T data) {
        this.raw = raw;
        this.data = data;
    }

    // Getters

    /**
     * Retrieves the stored data if available, otherwise throws a NullPointerException.
     *
     * @return The retrieved data.
     * @throws NullPointerException If the deferred data is not available (null).
     */
    public @NotNull T retrieve() {
        if (this.data == null) {
            throw new NullPointerException("Deferred data not available");
        } else {
            return this.data;
        }
    }

    /**
     * Checks if the deferred data is available.
     *
     * @return true if the data is available, false otherwise.
     */
    public boolean available() {
        return data != null;
    }

    // Implementations

    @Override
    public final boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Deferred<?> deferred = (Deferred<?>) object;
        return Objects.equals(raw, deferred.raw) && Objects.equals(data, deferred.data);
    }
    @Override
    public final int hashCode() {
        return Objects.hash(raw, data);
    }

    @Override
    public final @NotNull String toString() {
        return raw;
    }

}
