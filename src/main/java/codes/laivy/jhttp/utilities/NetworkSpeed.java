package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface NetworkSpeed {

    // Static initializers

    static @NotNull NetworkSpeed create(long bits) {
        return new NetworkSpeed() {
            @Override
            @Contract(pure = true)
            public long getBits() {
                return bits;
            }

            @Override
            @Contract(pure = true)
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (!(object instanceof NetworkSpeed)) return false;
                @NotNull NetworkSpeed that = (NetworkSpeed) object;
                return that.getBits() == bits;
            }
            @Override
            @Contract(pure = true)
            public int hashCode() {
                return Objects.hash(bits);
            }

            @Override
            @Contract(pure = true)
            public @NotNull String toString() {
                return String.valueOf(bits);
            }
        };
    }
    static @NotNull NetworkSpeed create(@NotNull Category category, double value) {
        return create((long) (value * category.getMultiplier()));
    }

    // Getters

    long getBits();

    default double getBits(@NotNull Category category) {
        return getBits() / category.getMultiplier();
    }

    // Classes

    enum Category {
        KILOBITS(1_000D),
        MEGABITS(1_000_000D),
        GIGABITS(1_000_000_000D),
        TERABITS(1_000_000_000_000D),

        KILOBYTES(8 * 1_000D),
        MEGABYTES(8 * 1_000_000D),
        GIGABYTES(8 * 1_000_000_000D),
        TERABYTES(8 * 1_000_000_000_000D),
        ;

        private final double multiplier;

        Category(double multiplier) {
            this.multiplier = multiplier;
        }

        public double getMultiplier() {
            return multiplier;
        }

    }

}
