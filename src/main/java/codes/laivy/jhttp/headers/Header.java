package codes.laivy.jhttp.headers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Header<T> {

    @NotNull HeaderKey<T> getKey();
    @NotNull T getValue();

    default @NotNull String getName() {
        return getKey().getName();
    }

    @Override
    boolean equals(@Nullable Object o);
    @Override
    int hashCode();

    static <E> @NotNull Header<E> create(final @NotNull HeaderKey<E> key, final @NotNull E value) {
        return new Header<E>() {
            @Override
            public @NotNull HeaderKey<E> getKey() {
                return key;
            }
            @Override
            @Contract(pure = true)
            public @NotNull E getValue() {
                return value;
            }
            @Override
            public @NotNull String toString() {
                return getName() + "=" + getValue();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Header && ((Header<?>) obj).getName().equalsIgnoreCase(getName());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getName());
            }
        };
    }

}
