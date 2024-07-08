package codes.laivy.jhttp.module.connection;

import codes.laivy.jhttp.headers.HttpHeaderKey;
import codes.laivy.jhttp.utilities.KeyUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents an HTTP connection header which indicates options that are desired for the connection.
 * <p>
 * This class provides methods to retrieve the connection type and any associated header keys.
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public class Connection {

    // Static initializers

    /**
     * Deserializes a string representation of a {@link Connection} into a {@link Connection} object.
     *
     * @param string The string representation of the {@code Connection}. Must not be null.
     * @return The deserialized {@link Connection} object. Will not be null.
     * @throws IllegalArgumentException If the input string is not a valid {@code Connection}.
     */
    public static @NotNull Connection parse(@NotNull String string) throws IllegalArgumentException {
        if (string.isEmpty()) {
            throw new IllegalArgumentException("cannot parse an empty string into a valid connection");
        }

        @NotNull Map<String, String> keys = KeyUtilities.read(string, null, ',');

        // Type
        @NotNull String typeStr = keys.keySet().stream().findFirst().orElseThrow(() -> new IllegalArgumentException("cannot find connection type"));
        @Nullable Type type = null;

        if (typeStr.equalsIgnoreCase("close") || typeStr.equalsIgnoreCase("keep-alive")) {
            type = Type.getById(typeStr);
            keys.remove(typeStr);
        }

        // Headers
        @NotNull Set<HttpHeaderKey<?>> headers = new LinkedHashSet<>();
        for (@NotNull String name : keys.keySet()) {
            headers.add(HttpHeaderKey.retrieve(name));
        }

        // Finish
        return create(type, headers.toArray(new HttpHeaderKey[0]));
    }

    /**
     * Creates a new {@link Connection} instance with the specified {@link Type} and no header keys.
     *
     * @param type The type of the connection. Must not be null.
     * @return A new {@link Connection} instance.
     * @throws NullPointerException If the {@code type} parameter is null.
     */
    public static @NotNull Connection create(final @NotNull Type type) {
        return create(type, new HttpHeaderKey[0]);
    }

    /**
     * Creates a new {@link Connection} instance with the specified {@link Type} and header keys.
     *
     * @param type The type of the connection. Can be null.
     * @param keys The header keys associated with the connection. Must not be null.
     * @return A new {@link Connection} instance.
     * @throws NullPointerException If the {@code type} or {@code keys} parameter is null.
     */
    public static @NotNull Connection create(final @Nullable Type type, final @NotNull HttpHeaderKey<?> @NotNull [] keys) {
        if (keys.length > 0 && !Arrays.stream(keys).allMatch(HttpHeaderKey::isHopByHop)) {
            throw new IllegalArgumentException("the headers of a connection must be all hop-by-hop");
        } else if (type == null && keys.length == 0) {
            throw new IllegalArgumentException("connection type cannot be null while the headers are empty");
        }

        return new Connection(type, keys);
    }

    // Object

    private final @Nullable Type type;
    private final @NotNull HttpHeaderKey<?> @NotNull [] keys;

    protected Connection(@Nullable Type type, @NotNull HttpHeaderKey<?> @NotNull [] keys) {
        this.type = type;
        this.keys = keys;
    }

    // Getters

    /**
     * Returns the type of this connection.
     *
     * @return The type of this connection. Should be null
     */
    public @Nullable Type getType() {
        return type;
    }

    /**
     * Returns the header keys associated with this connection.
     *
     * @return An array of {@link HttpHeaderKey} objects associated with this connection. Will not be null, but may be empty
     */
    public @NotNull HttpHeaderKey<?> @NotNull [] getKeys() {
        return keys;
    }

    // Implementations

    @Override
    public final boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @NotNull Connection that = (Connection) o;
        return getType() == that.getType() && Objects.deepEquals(getKeys(), that.getKeys());
    }
    @Override
    public final int hashCode() {
        return Objects.hash(getType(), Arrays.hashCode(getKeys()));
    }
    @Override
    public final @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder();

        if (getType() != null) {
            builder.append(getType().getId());
        } for (@NotNull HttpHeaderKey<?> key : getKeys()) {
            builder.append(", ").append(key.getName());
        }

        return builder.toString();
    }

    // Classes

    public enum Type {

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
            System.out.println("Loaded");
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

}
