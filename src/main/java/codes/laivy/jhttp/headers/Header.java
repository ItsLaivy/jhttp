package codes.laivy.jhttp.headers;

import codes.laivy.jhttp.network.BitMeasure;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;

public interface Header<T> {

    @NotNull HeaderKey<T> getKey();
    @UnknownNullability T getValue();

    default @NotNull String getName() {
        return getKey().getName();
    }

    @Override
    boolean equals(@Nullable Object o);
    @Override
    int hashCode();

    static <E> @NotNull Header<E> create(final @NotNull HeaderKey<E> key, final @UnknownNullability E value) {
        return new Header<E>() {
            @Override
            public @NotNull HeaderKey<E> getKey() {
                return key;
            }
            @Override
            @Contract(pure = true)
            public @UnknownNullability E getValue() {
                return value;
            }
            @Override
            public @NotNull String toString() {
                return getName() + "=" + getValue();
            }

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (!(object instanceof BitMeasure)) return false;
                @NotNull Header<?> that = (Header<?>) object;
                return getName().equalsIgnoreCase(that.getName()) && Objects.equals(getValue(), that.getValue());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getName(), getValue());
            }
        };
    }

}
