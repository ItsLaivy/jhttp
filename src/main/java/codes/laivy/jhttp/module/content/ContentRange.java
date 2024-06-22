package codes.laivy.jhttp.module.content;

import codes.laivy.jhttp.utilities.Range;
import codes.laivy.jhttp.headers.Wildcard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContentRange extends Range<Long> {

    // Static initializers

    public static @NotNull ContentRange create(@NotNull Range<Long> range, @NotNull String unit, @NotNull Wildcard<Long> size) {
        return new ContentRange(range.getMinimum(), range.getMaximum(), unit, size);
    }

    // Object

    private final @NotNull String unit;
    private final @NotNull Wildcard<Long> size;

    private ContentRange(long first, long second, @NotNull String unit, @NotNull Wildcard<Long> size) {
        super(first, second);

        this.unit = unit;
        this.size = size;
    }

    // Getters

    public @NotNull String getUnit() {
        return unit;
    }
    public @NotNull Wildcard<Long> getSize() {
        return size;
    }

    public boolean isBoundless() {
        return getMinimum() == Long.MIN_VALUE && getMaximum() == Long.MAX_VALUE;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        @NotNull ContentRange range = (ContentRange) object;
        return Objects.equals(unit, range.unit) && Objects.equals(size, range.size);
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), unit, size);
    }

    @Override
    public @NotNull String toString() {
        return Parser.serialize(this);
    }

    // Classes

    public static final class Parser {
        private Parser() {
            throw new UnsupportedOperationException();
        }

        // Serializers

        // todo: this regex has vulnerabilities
        public static final @NotNull Pattern PARSE_PATTERN = Pattern.compile("^(\\w+) (\\*|\\d+)?-?(\\d+)?/(\\d+|\\*)$");

        public static @NotNull String serialize(@NotNull ContentRange range) {
            // Wildcard check
            if (range.getMinimum() == Long.MIN_VALUE && range.getMaximum() == Long.MAX_VALUE) {
                return range.getUnit() + " " + "*/" + range.getSize();
            } else {
                return range.getUnit() + " " + range.getMinimum() + "-" + range.getMaximum() + "/" + range.getSize();
            }
        }
        public static @NotNull ContentRange deserialize(@NotNull String string) throws ParseException {
            @NotNull Matcher matcher = PARSE_PATTERN.matcher(string);

            if (matcher.matches()) {
                @NotNull String unit = matcher.group(1);
                @NotNull Wildcard<Long> size = !matcher.group(4).equals("*") ? Wildcard.create(Long.valueOf(matcher.group(4))) : Wildcard.create();

                @NotNull Range<Long> range = new Range<>(
                        matcher.group(2).trim().equals("*") ? Long.MIN_VALUE : Long.parseLong(matcher.group(2)),
                        matcher.group(3) != null ? Long.parseLong(matcher.group(3)) : Long.MAX_VALUE
                );

                return new ContentRange(range.getMinimum(), range.getMaximum(), unit, size);
            } else {
                throw new ParseException("cannot parse '" + string + "' into a valid content range", 0);
            }
        }

        public static boolean validate(@NotNull String string) {
            return PARSE_PATTERN.matcher(string).matches();
        }

    }

}
