package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Weight<T> {

    // Static initializers

    static <E> @NotNull Weight<E> create(@Nullable Float weight, @NotNull E value) {
        return new Weight<E>() {
            @Override
            public @Nullable Float getWeight() {
                return weight;
            }
            @Override
            public @NotNull E getValue() {
                return value;
            }

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (!(object instanceof Weight<?>)) return false;
                Weight<?> that = (Weight<?>) object;
                return Objects.equals(weight, that.getWeight()) && value.equals(that.getValue());
            }
            @Override
            public int hashCode() {
                return Objects.hash(weight, value);
            }

            @Override
            public @NotNull String toString() {
                return value + (weight != null ? ";q=" + weight : "");
            }
        };
    }

    // Object

    @Nullable Float getWeight();
    @NotNull T getValue();

}
