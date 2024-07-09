package codes.laivy.jhttp.url.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents the Second-Level Domain (SLD) of a domain name, ensuring it adheres to the format of a valid SLD.
 *
 * <p>An SLD is the part of a domain name that is directly to the left of the top-level domain (TLD). For example, in the domain name "example.com",
 * "example" is the SLD and "com" is the TLD.</p>
 *
 * <p>This class enforces that the SLD follows these rules:
 * <ul>
 *   <li>Must consist of only alphanumeric characters and hyphens ('-').</li>
 *   <li>Cannot start or end with a hyphen ('-').</li>
 *   <li>Must be between 1 and 63 characters in length.</li>
 * </ul>
 * </p>
 *
 * <p>Instances of this class are immutable and thread-safe.</p>
 *
 * <p>Examples of valid SLDs include "example", "my-domain", and "test123". Examples of invalid SLDs include "-example", "example-", and "ex@mple".</p>
 *
 * <p>Usage:</p>
 * <pre>
 * {@code
 * SLD sld = SLD.parse("example");
 * boolean isValid = SLD.validate("example");
 * }
 * </pre>
 *
 * <p>This class implements {@link CharSequence}, allowing it to be used wherever a {@link CharSequence} is required.</p>
 *
 * @see java.lang.CharSequence
 * @see java.util.Objects
 * @see java.lang.String
 */
public final class SLD implements CharSequence {

    // Static initializers

    /**
     * Validates whether a given string is a valid SLD.
     *
     * @param string the string to validate
     * @return {@code true} if the string is a valid SLD, {@code false} otherwise
     */
    public static boolean validate(@NotNull String string) {
        return string.matches("^(?!-)[A-Za-z0-9-]{1,63}(?<!-)$");
    }

    /**
     * Parses a given string into an {@link SLD} object.
     *
     * @param string the string to parse
     * @return a new {@link SLD} object
     * @throws IllegalArgumentException if the string is not a valid SLD
     */
    public static @NotNull SLD parse(@NotNull String string) {
        return new SLD(string);
    }

    private final @NotNull String string;

    /**
     * Constructs an {@link SLD} object with the given string.
     *
     * @param string the string representing the SLD
     * @throws IllegalArgumentException if the string is not a valid SLD
     */
    private SLD(@NotNull String string) {
        this.string = string;

        if (!validate(string)) {
            throw new IllegalArgumentException("The string '" + string + "' cannot be parsed as a valid SLD");
        }
    }

    // Getters

    /**
     * Returns the length of the SLD string.
     *
     * @return the length of the SLD string
     */
    @Override
    public int length() {
        return toString().length();
    }

    /**
     * Returns the character at the specified index.
     *
     * @param index the index of the character to return
     * @return the character at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    /**
     * Returns a subsequence of the SLD string.
     *
     * @param start the start index, inclusive
     * @param end the end index, exclusive
     * @return the specified subsequence
     * @throws IndexOutOfBoundsException if start or end is out of range
     */
    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    /**
     * Compares this SLD to another string, ignoring case considerations.
     *
     * @param string the string to compare to
     * @return {@code true} if the strings are equal ignoring case, {@code false} otherwise
     */
    public boolean equalsIgnoreCase(@NotNull String string) {
        return toString().equalsIgnoreCase(string);
    }

    // Implementations

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param object the reference object with which to compare
     * @return {@code true} if this object is the same as the object argument; {@code false} otherwise
     */
    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull SLD sld = (SLD) object;
        return Objects.equals(string.toLowerCase(), sld.string.toLowerCase());
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(string.toLowerCase());
    }

    /**
     * Returns the string representation of the SLD.
     *
     * @return the string representation of the SLD
     */
    @Override
    public @NotNull String toString() {
        return string;
    }

}