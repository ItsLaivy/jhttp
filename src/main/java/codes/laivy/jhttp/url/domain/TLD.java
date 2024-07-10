package codes.laivy.jhttp.url.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents the Top-Level Domain (TLD) of a domain name, ensuring it adheres to the format of a valid TLD.
 *
 * <p>A TLD is the last part of a domain name, following the final dot. For example, in the domain name "example.com",
 * "com" is the TLD. TLDs are used to classify domains into different categories such as generic top-level domains (gTLDs)
 * and country code top-level domains (ccTLDs).</p>
 *
 * <p>This class enforces that the TLD follows these rules:</p>
 * <ul>
 *   <li>Must consist of only alphabetic characters (A-Z, case insensitive).</li>
 *   <li>Must be between 2 and 6 characters in length.</li>
 * </ul>
 *
 * <p>Instances of this class are immutable and thread-safe.</p>
 *
 * <p>Examples of valid TLDs include "com", "net", "org", and "uk". Examples of invalid TLDs include "com1", "c", and "toolong".</p>
 *
 * <p>Usage:</p>
 * <pre>
 * {@code
 * TLD tld = TLD.parse("com");
 * boolean isValid = TLD.validate("com");
 * }
 * </pre>
 *
 * <p>This class implements {@link CharSequence}, allowing it to be used wherever a {@link CharSequence} is required.</p>
 *
 * @see java.lang.CharSequence
 * @see java.util.Objects
 * @see java.lang.String
 */
public final class TLD implements CharSequence {

    // Static initializers

    /**
     * Validates whether a given string is a valid TLD.
     *
     * @param string the string to validate
     * @return {@code true} if the string is a valid TLD, {@code false} otherwise
     */
    public static boolean validate(@NotNull String string) {
        return string.matches("^[A-Za-z]{2,6}$");
    }

    /**
     * Parses a given string into a {@link TLD} object.
     *
     * @param string the string to parse
     * @return a new {@link TLD} object
     * @throws IllegalArgumentException if the string is not a valid TLD
     */
    public static @NotNull TLD parse(@NotNull String string) {
        return new TLD(string);
    }

    /**
     * Predefined {@link TLD} instances for common TLDs.
     */
    public static final @NotNull TLD COM = new TLD("com");
    public static final @NotNull TLD NET = new TLD("net");

    // Object

    private final @NotNull String string;

    /**
     * Constructs a {@link TLD} object with the given string.
     *
     * @param string the string representing the TLD
     * @throws IllegalArgumentException if the string is not a valid TLD
     */
    private TLD(@NotNull String string) {
        this.string = string;

        if (!validate(string)) {
            throw new IllegalArgumentException("The string '" + string + "' cannot be parsed as a valid TLD");
        }
    }

    // Getters

    /**
     * Returns the length of the TLD string.
     *
     * @return the length of the TLD string
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
     * Returns a subsequence of the TLD string.
     *
     * @param start the start index, inclusive
     * @param end the end index, exclusive
     * @return the specified subsequence
     * @throws IndexOutOfBoundsException if start or end are out of range
     */
    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    /**
     * Compares this TLD to another string, ignoring case considerations.
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
        @NotNull TLD tld = (TLD) object;
        return Objects.equals(string.toLowerCase(), tld.string.toLowerCase());
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
     * Returns the string representation of the TLD.
     *
     * @return the string representation of the TLD
     */
    @Override
    public @NotNull String toString() {
        return string;
    }

}