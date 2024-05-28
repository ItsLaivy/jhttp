package codes.laivy.jhttp.headers;

import codes.laivy.jhttp.exception.HeaderFormatException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Header {

    @NotNull HeaderKey<?> getKey();
    @NotNull String getValue();

    // @UnknownNullability T getParsed();

    default @NotNull String getName() {
        return getKey().getName();
    }

    @Override
    boolean equals(@Nullable Object o);
    @Override
    int hashCode();

    static @NotNull Header create(final @NotNull HeaderKey<?> key, final @NotNull String value) {
        return new Header() {
            @Override
            public @NotNull HeaderKey<?> getKey() {
                return key;
            }
            @Override
            @Contract(pure = true)
            public @NotNull String getValue() {
                return value;
            }
            @Override
            public @NotNull String toString() {
                return getName() + "=" + getValue();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Header && ((Header) obj).getName().equalsIgnoreCase(getName());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getName());
            }
        };
    }
    static @NotNull Header create(final @NotNull HeaderKey<?> key, final @NotNull String value, boolean unsafe) throws HeaderFormatException {
        if (unsafe && key.getPattern() != null && key.getPattern().matcher(value).matches()) {
            throw new HeaderFormatException("the value '" + value + "' cannot be applied to header '" + key.getName() + "'. The pattern is '" + key.getPattern().pattern() + "'");
        }

        return create(key, value);
    }

}
