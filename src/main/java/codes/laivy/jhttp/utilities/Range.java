package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Range<T extends Comparable<T>> {

    private final @NotNull T minimum;
    private final @NotNull T maximum;

    public Range(@NotNull T first, @NotNull T second) {
        if (first.compareTo(second) <= 0) {
            this.minimum = first;
            this.maximum = second;
        } else {
            this.minimum = second;
            this.maximum = first;
        }
    }

    // Getters

    public @NotNull T getMinimum() {
        return minimum;
    }
    public @NotNull T getMaximum() {
        return maximum;
    }

    // Modules

    public boolean isAfter(@NotNull T value) {
        return maximum.compareTo(value) > 0;
    }
    public boolean isBefore(@NotNull T value) {
        return minimum.compareTo(value) < 0;
    }

    public boolean contains(@NotNull T value) {
        return (minimum.compareTo(value) >= 0) && (maximum.compareTo(value) <= 0);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @NotNull Range<?> range = (Range<?>) o;
        return Objects.equals(minimum, range.minimum) && Objects.equals(maximum, range.maximum);
    }
    @Override
    public int hashCode() {
        return Objects.hash(minimum, maximum);
    }

    @Override
    public @NotNull String toString() {
        return "Range{" +
                "minimum=" + minimum +
                ", maximum=" + maximum +
                '}';
    }

}
