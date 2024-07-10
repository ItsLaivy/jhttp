package codes.laivy.jhttp.url.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a subdomain in a domain name system (DNS), ensuring it adheres to the valid subdomain format.
 *
 * <p>A subdomain is a subdivision of a domain and is often used to organize and navigate different sections
 * of a website or application. A subdomain typically appears before the main domain name, separated by a dot.</p>
 *
 * <p>This class enforces that the subdomain follows these rules:</p>
 * <ul>
 *   <li>Must start and end with an alphanumeric character.</li>
 *   <li>May contain alphanumeric characters and hyphens in between.</li>
 *   <li>Must be between 1 and 63 characters long.</li>
 * </ul>
 *
 * <p>Instances of this class are immutable and thread-safe.</p>
 *
 * <p>Examples of valid subdomains include "www", "mail", and "blog". Examples of invalid subdomains include "-start" and "end-".</p>
 *
 * <p>Usage:</p>
 * <pre>
 * {@code
 * Subdomain subdomain = Subdomain.create("www");
 * int length = subdomain.length();
 * }
 * </pre>
 *
 * <p>This class implements {@link CharSequence}, allowing it to be used in string manipulations and comparisons.</p>
 *
 * @see java.lang.CharSequence
 * @see java.util.regex.Pattern
 * @see java.util.Objects
 */
public final class Subdomain implements CharSequence {

    // Static initializers

    /**
     * Validates if the given string is a valid subdomain.
     *
     * @param string the string to validate
     * @return {@code true} if the string is a valid subdomain, {@code false} otherwise
     */
    public static boolean validate(@NotNull String string) {
        return string.matches("[A-Za-z0-9](?:[A-Za-z0-9\\-]{0,61}[A-Za-z0-9])?");
    }

    /**
     * Creates a new {@link Subdomain} instance if the given string is valid.
     *
     * @param string the string to create the subdomain from
     * @return a new {@link Subdomain} instance
     * @throws IllegalArgumentException if the string is not a valid subdomain
     */
    public static @NotNull Subdomain create(@NotNull String string) {
        return new Subdomain(string);
    }

    // Object

    private final @NotNull String content;

    /**
     * Constructs a {@link Subdomain} object with the given content.
     *
     * @param content the string representing the subdomain
     * @throws IllegalArgumentException if the content is not a valid subdomain
     */
    private Subdomain(@NotNull String content) {
        this.content = content;

        if (!validate(content)) {
            throw new IllegalArgumentException("cannot parse '" + content + "' as a valid subdomain");
        }
    }

    // Natives

    /**
     * Returns the length of the subdomain.
     *
     * @return the number of characters in the subdomain
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
     * Returns a new {@code CharSequence} that is a subsequence of this sequence.
     *
     * @param start the start index, inclusive
     * @param end the end index, exclusive
     * @return the specified subsequence
     * @throws IndexOutOfBoundsException if start or end are out of range
     */
    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return toString().substring(start, end);
    }

    /**
     * Compares this subdomain to another string, ignoring case considerations.
     *
     * @param string the string to compare to
     * @return {@code true} if the strings are equal, ignoring case; {@code false} otherwise
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
        @NotNull Subdomain subdomain = (Subdomain) object;
        return Objects.equals(content, subdomain.content);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(content);
    }

    /**
     * Returns the string representation of the subdomain.
     *
     * @return the string representation of the subdomain
     */
    @Override
    public @NotNull String toString() {
        return content;
    }
}