package codes.laivy.jhttp.utilities.header;

import codes.laivy.jhttp.exception.WildcardValueException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;

// todo: javadocs
public interface Wildcard<T> {

    // Static initializers

    static <E> @NotNull Wildcard<E> create() {
        return new Wildcard<E>() {
            @Override
            public boolean isWildcard() {
                return true;
            }
            @Override
            public @NotNull E getValue() throws WildcardValueException {
                throw new WildcardValueException("this is a wildcard (*) and doesn't have a value!");
            }

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (!(object instanceof Weight<?>)) return false;
                Wildcard<?> that = (Wildcard<?>) object;
                return that.isWildcard();
            }
            @Override
            public int hashCode() {
                return Objects.hash(isWildcard());
            }

            @Override
            public @NotNull String toString() {
                return "*";
            }
        };
    }
    static <E> @NotNull Wildcard<E> create(@UnknownNullability E value) {
        return new Wildcard<E>() {
            @Override
            public boolean isWildcard() {
                return false;
            }
            @Override
            public @UnknownNullability E getValue() throws WildcardValueException {
                return value;
            }

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (!(object instanceof Weight<?>)) return false;
                Wildcard<?> that = (Wildcard<?>) object;
                return !that.isWildcard() && that.getValue() == getValue();
            }
            @Override
            public int hashCode() {
                return Objects.hash(isWildcard(), getValue());
            }

            @Override
            public @NotNull String toString() {
                return getValue().toString();
            }
        };
    }

    // Object

    boolean isWildcard();
    @UnknownNullability T getValue() throws WildcardValueException;

}
