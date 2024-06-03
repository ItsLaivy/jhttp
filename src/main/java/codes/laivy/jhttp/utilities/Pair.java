package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;

public interface Pair<K, V> {

    // Static initializers

    static <K, V> @NotNull Pair<K, V> create(@NotNull K key, @UnknownNullability V value) {
        return new Pair<K, V>() {
            @Override
            public K getKey() {
                return key;
            }
            @Override
            public V getValue() {
                return value;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (!(object instanceof Pair<?, ?>)) return false;
                Pair<?, ?> that = (Pair<?, ?>) object;
                return key.equals(that.getKey()) && value.equals(that.getValue());
            }
            @Override
            public int hashCode() {
                return Objects.hash(key, value);
            }

            @Override
            public @NotNull String toString() {
                return getKey() + "=" + getValue();
            }

        };
    }

    // Object

    K getKey();
    V getValue();

}
