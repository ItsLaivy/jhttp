package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class KeepAlive {

    // Static initializers

    public static final @NotNull Pattern KEEP_ALIVE_PATTERN = Pattern.compile("^timeout=(?<timeout>\\d+)(?:\\s*,\\s*max=(?<max>\\d+))?$");

    public static boolean validate(@NotNull String string) {
        return KEEP_ALIVE_PATTERN.matcher(string).matches();
    }
    public static @NotNull KeepAlive parse(@NotNull String string) throws ParseException {
        @NotNull Matcher matcher = KEEP_ALIVE_PATTERN.matcher(string);

        if (matcher.matches()) {
            @NotNull Duration timeout = Duration.ofSeconds(Long.parseLong(matcher.group("timeout")));
            @Nullable Duration maximum = matcher.group("max") != null ? Duration.ofSeconds(Long.parseLong(matcher.group("max"))) : null;

            return new KeepAlive(timeout, maximum);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid keep alive object", 0);
        }
    }

    public static @NotNull KeepAlive create(@NotNull Duration timeout) {
        return new KeepAlive(timeout, null);
    }
    public static @NotNull KeepAlive create(@NotNull Duration timeout, @Nullable Duration maximum) {
        return new KeepAlive(timeout, maximum);
    }

    // Object

    private final @NotNull Duration timeout;
    private final @Nullable Duration maximum;

    private KeepAlive(@NotNull Duration timeout, @Nullable Duration maximum) {
        this.timeout = timeout;
        this.maximum = maximum;
    }

    // Getters

    public @NotNull Duration getTimeout() {
        return timeout;
    }
    public @Nullable Duration getMaximum() {
        return maximum;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull KeepAlive keepAlive = (KeepAlive) object;
        return Objects.equals(timeout, keepAlive.timeout) && Objects.equals(maximum, keepAlive.maximum);
    }
    @Override
    public int hashCode() {
        return Objects.hash(timeout, maximum);
    }

    @Override
    public @NotNull String toString() {
        return "timeout=" + getTimeout().getSeconds() + ", max=" + (getMaximum() != null ? getMaximum().getSeconds() : 0L);
    }

}
