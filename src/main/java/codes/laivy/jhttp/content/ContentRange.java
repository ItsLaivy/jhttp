package codes.laivy.jhttp.content;

import codes.laivy.jhttp.utilities.Range;
import codes.laivy.jhttp.utilities.header.Wildcard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContentRange {

    // Static initializers

    // todo: this regex has vulnerabilities
    public static final @NotNull Pattern PARSE_PATTERN = Pattern.compile("^(\\w+) (\\*|\\d+)?-?(\\d+)?/(\\d+|\\*)$");

    public static boolean isContentRange(@NotNull String string) {
        return PARSE_PATTERN.matcher(string).matches();
    }
    public static @NotNull ContentRange parse(@NotNull String string) throws ParseException {
        @NotNull Matcher matcher = PARSE_PATTERN.matcher(string);

        if (matcher.matches()) {
            @NotNull String unit = matcher.group(1);
            @NotNull Wildcard<Long> size = !matcher.group(4).equals("*") ? Wildcard.create(Long.valueOf(matcher.group(4))) : Wildcard.create();

            @NotNull Range<Long> range = new Range<>(
                    matcher.group(2).trim().equals("*") ? Long.MIN_VALUE : Long.parseLong(matcher.group(2)),
                    matcher.group(3) != null ? Long.parseLong(matcher.group(3)) : Long.MAX_VALUE
            );

            return new ContentRange(unit, range, size);
        } else {
            throw new ParseException("cannot parse '" + string + "' into a valid content range", 0);
        }
    }
    public static @NotNull ContentRange create(@NotNull String unit, @NotNull Range<Long> range, @NotNull Wildcard<Long> size) {
        return new ContentRange(unit, range, size);
    }

    // Object

    private final @NotNull String unit;
    private final @NotNull Range<Long> range;
    private final @NotNull Wildcard<Long> size;

    private ContentRange(@NotNull String unit, @NotNull Range<Long> range, @NotNull Wildcard<Long> size) {
        this.unit = unit;
        this.range = range;
        this.size = size;
    }

    // Getters

    public @NotNull String getUnit() {
        return unit;
    }
    public @NotNull Range<Long> getRange() {
        return range;
    }
    public @NotNull Wildcard<Long> getSize() {
        return size;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull ContentRange that = (ContentRange) object;
        return Objects.equals(unit, that.unit) && Objects.equals(range, that.range) && Objects.equals(size, that.size);
    }
    @Override
    public int hashCode() {
        return Objects.hash(unit, range, size);
    }

    @Override
    public @NotNull String toString() {
        // Wildcard check
        if (getRange().getMinimum() == Long.MIN_VALUE && getRange().getMaximum() == Long.MAX_VALUE) {
            return getUnit() + " " + "*/" + getSize();
        } else {
            @NotNull Range<Long> range = getRange();
            return getUnit() + " " + range.getMinimum() + "-" + range.getMaximum() + "/" + getSize();
        }
    }

}
