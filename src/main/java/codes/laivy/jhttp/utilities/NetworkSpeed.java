package codes.laivy.jhttp.utilities;

import codes.laivy.jhttp.utilities.header.Weight;
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
                if (!(object instanceof Weight<?>)) return false;
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

    // Getters

    long getBits();

    default double getBits(@NotNull Category category) {
        return category.convert(getBits());
    }

    // Classes

    enum Category {
        KILOBITS() {
            @Override
            public double convert(long bits) {
                return bits / 1_000D;
            }
        },
        MEGABITS() {
            @Override
            public double convert(long bits) {
                return bits / 1_000_000D;
            }
        },
        GIGABITS() {
            @Override
            public double convert(long bits) {
                return bits / 1_000_000_000D;
            }
        },
        TERABITS() {
            @Override
            public double convert(long bits) {
                return bits / 1_000_000_000_000D;
            }
        },

        KILOBYTES {
            @Override
            public double convert(long bits) {
                return bits / (8 * 1_000D);
            }
        },
        MEGABYTES {
            @Override
            public double convert(long bits) {
                return bits / (8 * 1_000_000D);
            }
        },
        GIGABYTES {
            @Override
            public double convert(long bits) {
                return bits / (8 * 1_000_000_000D);
            }
        },
        TERABYTES {
            @Override
            public double convert(long bits) {
                return bits / (8 * 1_000_000_000_000D);
            }
        }
        ;

        public abstract double convert(long bits);

    }

}
