package codes.laivy.jhttp.url.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a network port, ensuring it adheres to the valid port number range (0 to 65535).
 *
 * <p>A port number is a 16-bit unsigned integer that is used by network protocols, such as TCP and UDP,
 * to identify specific processes or network services. The valid range for a port number is from 0 to 65535.</p>
 *
 * <p>This class enforces that the port number follows these rules:
 * <ul>
 *   <li>Must be an integer between 0 and 65535, inclusive.</li>
 * </ul>
 * </p>
 *
 * <p>Instances of this class are immutable and thread-safe.</p>
 *
 * <p>Examples of valid port numbers include 80 (HTTP), 443 (HTTPS), and 21 (FTP). Examples of invalid port numbers include -1 and 70000.</p>
 *
 * <p>Usage:</p>
 * <pre>
 * {@code
 * Port port = new Port(80);
 * int portNumber = port.intValue();
 * }
 * </pre>
 *
 * <p>This class extends {@link Number} and implements {@link Comparable}, allowing it to be used in numeric contexts and compared with other {@link Port} objects.</p>
 *
 * @see java.lang.Number
 * @see java.lang.Comparable
 * @see java.util.Objects
 */
public final class Port extends Number implements Comparable<Port> {

    // Static initializers

    public static boolean validate(@NotNull String string) {
        try {
            new Port(Integer.parseInt(string));
            return true;
        } catch (@NotNull NumberFormatException ignore) {
            return false;
        }
    }
    public static @NotNull Port create(@NotNull String port) throws NumberFormatException {
        return new Port(Integer.parseInt(port));
    }

    // todo: well known ports list

    // Object

    private final int integer;

    /**
     * Constructs a {@link Port} object with the given integer value.
     *
     * @param integer the integer representing the port number
     * @throws NumberFormatException if the integer is not within the valid port number range (0-65535)
     */
    private Port(int integer) throws NumberFormatException {
        this.integer = integer;

        if (integer < 0 || integer > 65535) {
            throw new NumberFormatException("The port number " + integer + " isn't valid (must be between 0 and 65535)");
        }
    }

    // Getters

    /**
     * Checks if the port is a well-known port (0-1023).
     *
     * @return {@code true} if the port is a well-known port, {@code false} otherwise
     */
    public boolean isWellKnown() {
        return integer >= 0 && integer <= 1023;
    }

    /**
     * Checks if the port is a registered port (1024-49151).
     *
     * @return {@code true} if the port is a registered port, {@code false} otherwise
     */
    public boolean isRegistered() {
        return integer >= 1024 && integer <= 49151;
    }

    /**
     * Checks if the port is a dynamic or private port (49152-65535).
     *
     * @return {@code true} if the port is a dynamic or private port, {@code false} otherwise
     */
    public boolean isDynamicPrivate() {
        return integer >= 49152 && integer <= 65535;
    }

    /**
     * Returns the type of the port based on its range.
     *
     * @return the type of the port
     */
    public @NotNull Type getPortType() {
        if (isWellKnown()) {
            return Type.WELL_KNOWN;
        } else if (isRegistered()) {
            return Type.REGISTERED;
        } else if (isDynamicPrivate()) {
            return Type.DYNAMIC_PRIVATE;
        } else {
            return Type.UNKNOWN;
        }
    }

    // Number

    /**
     * Returns the value of the port number as an {@code int}.
     *
     * @return the port number as an {@code int}
     */
    @Override
    public int intValue() {
        return integer;
    }

    /**
     * Returns the value of the port number as a {@code long}.
     *
     * @return the port number as a {@code long}
     */
    @Override
    public long longValue() {
        return integer;
    }

    /**
     * Returns the value of the port number as a {@code float}.
     *
     * @return the port number as a {@code float}
     */
    @Override
    public float floatValue() {
        return integer;
    }

    /**
     * Returns the value of the port number as a {@code double}.
     *
     * @return the port number as a {@code double}
     */
    @Override
    public double doubleValue() {
        return integer;
    }

    /**
     * Compares this port to another {@link Port} object.
     *
     * @param other the {@link Port} to compare to
     * @return a negative integer, zero, or a positive integer as this port is less than, equal to, or greater than the specified {@link Port}
     */
    @Override
    public int compareTo(@NotNull Port other) {
        return Integer.compare(integer, other.integer);
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
        @NotNull Port port = (Port) object;
        return integer == port.integer;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(integer);
    }

    /**
     * Returns the string representation of the port number.
     *
     * @return the string representation of the port number
     */
    @Override
    public @NotNull String toString() {
        return String.valueOf(integer);
    }

    // Classes

    /**
     * Enumeration representing the type of port based on its range.
     */
    public enum Type {
        WELL_KNOWN,
        REGISTERED,
        DYNAMIC_PRIVATE,
        UNKNOWN
    }

}