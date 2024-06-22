package codes.laivy.jhttp.network;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface BitMeasure {

    // Static initializers

    static @NotNull BitMeasure create(final long bits) {
        return new BitMeasure() {
            @Override
            @Contract(pure = true)
            public long getBits() {
                return bits;
            }

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (!(object instanceof BitMeasure)) return false;
                @NotNull BitMeasure that = (BitMeasure) object;
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
    static @NotNull BitMeasure create(@NotNull Level level, double value) {
        return create((long) (value * level.getMultiplier()));
    }

    // Getters

    long getBits();

    default long getBytes() {
        return (long) getBits(Level.BYTES);
    }
    default double getKilobytes() {
        return getBits(Level.KILOBYTES);
    }
    default double getMegabytes() {
        return getBits(Level.MEGABYTES);
    }
    default double getGigabytes() {
        return getBits(Level.GIGABYTES);
    }

    default double getBits(@NotNull Level level) {
        return getBits() / level.getMultiplier();
    }

    // Classes

    enum Level {
        BITS(1L),
        KILOBITS(1_000D),
        MEGABITS(1_000_000D),
        GIGABITS(1_000_000_000D),
        TERABITS(1_000_000_000_000D),

        BYTES(8L),
        KILOBYTES(8 * 1_000D),
        MEGABYTES(8 * 1_000_000D),
        GIGABYTES(8 * 1_000_000_000D),
        TERABYTES(8 * 1_000_000_000_000D),
        ;

        private final double multiplier;

        Level(double multiplier) {
            this.multiplier = multiplier;
        }

        public double getMultiplier() {
            return multiplier;
        }

    }

}
