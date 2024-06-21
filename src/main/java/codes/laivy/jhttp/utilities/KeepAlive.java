package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface KeepAlive {

    // Static initializers

    static @NotNull KeepAlive create(final @NotNull Duration timeout) {
        return create(timeout, null);
    }
    static @NotNull KeepAlive create(final @NotNull Duration timeout, final @Nullable Duration maximum) {
        return new KeepAlive() {

            // Object

            @Override
            public @NotNull Duration getTimeout() {
                return timeout;
            }
            @Override
            public @Nullable Duration getMaximum() {
                return maximum;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull KeepAlive keepAlive = (KeepAlive) object;
                return Objects.equals(getTimeout(), keepAlive.getTimeout()) && Objects.equals(getMaximum(), keepAlive.getMaximum());
            }
            @Override
            public int hashCode() {
                return Objects.hash(timeout, maximum);
            }
            @Override
            public @NotNull String toString() {
                return Parser.serialize(this);
            }

        };
    }

    // Getters

    @NotNull Duration getTimeout();
    @Nullable Duration getMaximum();

    // Classes

    final class Parser {
        private Parser() {
            throw new UnsupportedOperationException();
        }

        // Serializers

        private static final @NotNull Pattern KEEP_ALIVE_PATTERN = Pattern.compile("^timeout=(?<timeout>\\d+)(?:\\s*,\\s*max=(?<max>\\d+))?$");

        public static @NotNull String serialize(@NotNull KeepAlive keepAlive) {
            return "timeout=" + keepAlive.getTimeout().getSeconds() + ", max=" + (keepAlive.getMaximum() != null ? keepAlive.getMaximum().getSeconds() : 0L);
        }
        public static @NotNull KeepAlive deserialize(@NotNull String string) throws ParseException {
            @NotNull Matcher matcher = KEEP_ALIVE_PATTERN.matcher(string);

            if (matcher.matches()) {
                @NotNull Duration timeout = Duration.ofSeconds(Long.parseLong(matcher.group("timeout")));
                @Nullable Duration maximum = matcher.group("max") != null ? Duration.ofSeconds(Long.parseLong(matcher.group("max"))) : null;

                return create(timeout, maximum);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid keep alive object", 0);
            }
        }

        public static boolean validate(@NotNull String string) {
            return KEEP_ALIVE_PATTERN.matcher(string).matches();
        }

    }

}
