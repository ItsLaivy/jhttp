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

    public static final @NotNull Pattern PARSE_PATTERN = Pattern.compile("^(\\w+) (\\d+)?-(\\d+)?/(\\d+|\\*)$");

    public static boolean isContentRange(@NotNull String string) {
        return PARSE_PATTERN.matcher(string).matches();
    }
    public static @NotNull ContentRange parse(@NotNull String string) throws ParseException {
        @NotNull Matcher matcher = PARSE_PATTERN.matcher(string);

        if (matcher.matches()) {
            @NotNull String unit = matcher.group(1);

            @Nullable Long rangeMinimum = matcher.group(2) != null ? Long.valueOf(matcher.group(2)) : null;
            @Nullable Long rangeMaximum = matcher.group(3) != null ? Long.valueOf(matcher.group(3)) : null;

            @Nullable Long size = !matcher.group(4).equals("*") ? Long.valueOf(matcher.group(4)) : null;

            return new ContentRange(
                    unit,
                    rangeMinimum != null && rangeMaximum != null ? Wildcard.create(new Range<>(rangeMinimum, rangeMaximum)) : Wildcard.create(),
                    size != null ? Wildcard.create(size) : Wildcard.create()
            );
        } else {
            throw new ParseException("cannot parse '" + string + "' into a valid content range", 0);
        }
    }
    public static @NotNull ContentRange create(@NotNull String unit, @NotNull Wildcard<Range<Long>> range, @NotNull Wildcard<Long> size) {
        return new ContentRange(unit, range, size);
    }

    // Object

    private final @NotNull String unit;
    private final @NotNull Wildcard<Range<Long>> range;
    private final @NotNull Wildcard<Long> size;

    private ContentRange(@NotNull String unit, @NotNull Wildcard<Range<Long>> range, @NotNull Wildcard<Long> size) {
        this.unit = unit;
        this.range = range;
        this.size = size;
    }

    // Getters

    public @NotNull String getUnit() {
        return unit;
    }

    public @NotNull Wildcard<Range<Long>> getRange() {
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
        if (getRange().isWildcard()) {
            return getUnit() + " " + "*/" + getSize();
        } else {
            @NotNull Range<Long> range = getRange().getValue();
            return getUnit() + " " + range.getMinimum() + "-" + range.getMaximum() + "/" + getSize();
        }
    }

}
