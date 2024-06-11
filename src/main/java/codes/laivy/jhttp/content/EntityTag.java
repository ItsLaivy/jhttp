package codes.laivy.jhttp.content;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EntityTag {

    // Static initializers

    /**
     * A regular expression pattern to validate the name of an EntityTag (ETag) according to the HTTP/1.1 specification.
     * <p>
     * An ETag name is a string of ASCII characters excluding double quotes ("") and backslashes (\).
     * It is used to uniquely identify a version of a resource.
     * This pattern ensures that ETag names adhere to the restrictions specified in RFC 7232.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7232#section-2.3">RFC 7232 Section 2.3</a>
     */
    public static final @NotNull Pattern ETAG_NAME_PATTERN = Pattern.compile("^[\\x21\\x23-\\x5B\\x5D-\\x7E]+$");

    /**
     * Validate if a string is a valid EntityTag, according to the HTTP/1.1 specification.
     *
     * @param string The string to validate.
     * @return True if the string is a valid EntityTag, false otherwise.
     */
    public static boolean validate(@NotNull String string) {
        if ((string.startsWith("W/\"") || string.startsWith("\"")) && string.endsWith("\"")) {
            @NotNull Pattern internal = Pattern.compile("\"([^\"]*)\"");
            @NotNull Matcher matcher = internal.matcher(string);
            if (!matcher.find()) return false;

            return matcher.group().matches(ETAG_NAME_PATTERN.pattern());
        } else {
            return false;
        }
    }

    /**
     * Parse a string representation of an EntityTag into an EntityTag object.
     *
     * @param string The string representation of the EntityTag.
     * @return The parsed EntityTag object.
     * @throws IllegalArgumentException If the input string is not a valid EntityTag.
     */
    public static @NotNull EntityTag parse(@NotNull String string) throws ParseException {
        @NotNull Pattern internal = Pattern.compile("\"([^\"]*)\"");
        @NotNull Matcher matcher = internal.matcher(string);

        if (!validate(string) || !matcher.find()) {
            throw new ParseException("cannot parse '" + string + "' as a valid entity tag", 0);
        } else {
            boolean weak = string.startsWith("W/");
            @NotNull String name = matcher.group();

            return create(name, weak);
        }
    }

    public static @NotNull EntityTag create(@NotNull String name, boolean weak) {
        return new EntityTag(name, weak);
    }

    // Object

    private final @NotNull String name;
    private final boolean weak;

    private EntityTag(@NotNull String name, boolean weak) {
        this.name = name;
        this.weak = weak;
    }

    // Getters

    public @NotNull String getName() {
        return name;
    }
    public boolean isWeak() {
        return weak;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull EntityTag entityTag = (EntityTag) object;
        return weak == entityTag.weak && Objects.equals(name, entityTag.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, weak);
    }

    @Override
    public @NotNull String toString() {
        return (isWeak() ? "W/" : "") + "\"" + getName() + "\"";
    }

}
