package codes.laivy.jhttp.module.connection;

import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.utilities.KeyUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.*;

/**
 * Represents an HTTP connection header which indicates options that are desired for the connection.
 * <p>
 * This interface provides methods to retrieve the connection type and any associated header keys.
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface Connection {

    // Static initializers

    /**
     * Creates a new {@link Connection} instance with the specified {@link Type} and no header keys.
     *
     * @param type The type of the connection. Must not be null.
     * @return A new {@link Connection} instance.
     * @throws NullPointerException If the {@code type} parameter is null.
     */
    static @NotNull Connection create(final @NotNull Type type) {
        return create(type, new HeaderKey[0]);
    }

    /**
     * Creates a new {@link Connection} instance with the specified {@link Type} and header keys.
     *
     * @param type The type of the connection. Can be null.
     * @param keys The header keys associated with the connection. Must not be null.
     * @return A new {@link Connection} instance.
     * @throws NullPointerException If the {@code type} or {@code keys} parameter is null.
     */
    static @NotNull Connection create(final @Nullable Type type, final @NotNull HeaderKey<?> @NotNull [] keys) {
        if (keys.length > 0 && !Arrays.stream(keys).allMatch(HeaderKey::isHopByHop)) {
            throw new IllegalArgumentException("the headers of a connection must be all hop-by-hop");
        } else if (type == null && keys.length == 0) {
            throw new IllegalArgumentException("connection type cannot be null while the headers are empty");
        }

        return new Connection() {

            // Object

            @Override
            public @Nullable Type getType() {
                return type;
            }
            @Override
            public @NotNull HeaderKey<?> @NotNull [] getKeys() {
                return keys;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                @NotNull Connection that = (Connection) o;
                return getType() == that.getType() && Objects.deepEquals(getKeys(), that.getKeys());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getType(), Arrays.hashCode(getKeys()));
            }
            @Override
            public @NotNull String toString() {
                return Parser.serialize(this);
            }

        };
    }

    // Getters

    /**
     * Returns the type of this connection.
     *
     * @return The type of this connection. Should be null
     */
    @Nullable Type getType();

    /**
     * Returns the header keys associated with this connection.
     *
     * @return An array of {@link HeaderKey} objects associated with this connection. Will not be null, but may be empty
     */
    @NotNull HeaderKey<?> @NotNull [] getKeys();

    // Classes

    enum Type {

        /**
         * Indicates that the connection should be kept alive.
         */
        KEEP_ALIVE("keep-alive"),

        /**
         * Indicates that the connection should be closed.
         */
        CLOSE("close"),
        ;

        private final @NotNull String id;

        Type(@NotNull String id) {
            this.id = id;
        }

        // Getters

        public @NotNull String getId() {
            return id;
        }

        // Static initializers

        /**
         * Returns the {@link Type} corresponding to the given identifier.
         *
         * @param id The identifier of the connection type. Must not be null.
         * @return The {@link Type} corresponding to the given identifier.
         * @throws NullPointerException If no connection type matches the given identifier.
         */
        public static @NotNull Type getById(@NotNull String id) {
            @NotNull Optional<Type> optional = Arrays.stream(values()).filter(type -> type.getId().equalsIgnoreCase(id)).findFirst();
            return optional.orElseThrow(() -> new NullPointerException("there's no connection type with id '" + id + "'"));
        }

    }

    /**
     * A utility class for serializing and deserializing {@link Connection} objects.
     * <p>
     * This class provides methods to convert {@code Connection} objects to and from their string representation.
     */
    final class Parser {
        private Parser() {
            throw new UnsupportedOperationException();
        }

        // Serializers

        /**
         * Serializes a {@link Connection} object into its string representation.
         * <p>
         * The string representation includes the connection type and any associated header keys.
         *
         * @param connection The {@link Connection} to serialize. Must not be null.
         * @return The string representation of the {@code Connection}. Will not be null.
         */
        public static @NotNull String serialize(@NotNull Connection connection) {
            @NotNull StringBuilder builder = new StringBuilder();

            if (connection.getType() != null) {
                builder.append(connection.getType().getId());
            } for (@NotNull HeaderKey<?> key : connection.getKeys()) {
                builder.append(", ").append(key.getName());
            }

            return builder.toString();
        }

        /**
         * Deserializes a string representation of a {@link Connection} into a {@link Connection} object.
         *
         * @param string The string representation of the {@code Connection}. Must not be null.
         * @return The deserialized {@link Connection} object. Will not be null.
         * @throws ParseException If the input string is not a valid {@code Connection}.
         */
        public static @NotNull Connection deserialize(@NotNull String string) throws ParseException {
            if (string.isEmpty()) {
                throw new ParseException("cannot parse '' into a valid connection", -1);
            }

            @NotNull Map<String, String> keys = KeyUtilities.read(string, null, ',');

            // Type
            @NotNull String typeStr = keys.keySet().stream().findFirst().orElseThrow(() -> new ParseException("cannot find connection type", 0));
            @Nullable Type type = null;

            if (typeStr.equalsIgnoreCase("close") || typeStr.equalsIgnoreCase("keep-alive")) {
                type = Type.getById(typeStr);
                keys.remove(typeStr);
            }

            // Headers
            @NotNull Set<HeaderKey<?>> headers = new LinkedHashSet<>();
            for (@NotNull String name : keys.keySet()) {
                headers.add(HeaderKey.retrieve(name));
            }

            // Finish
            return create(type, headers.toArray(new HeaderKey[0]));
        }

        /**
         * Validates whether a given string is a valid {@link Connection}.
         *
         * @param string The string to validate. Must not be null.
         * @return {@code true} if the string is a valid {@code Connection}; {@code false} otherwise.
         */
        public static boolean validate(@NotNull String string) {
            try {
                deserialize(string);
                return true;
            } catch (@NotNull ParseException ignore) {
                return false;
            }
        }

    }

}
