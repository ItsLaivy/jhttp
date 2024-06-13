package codes.laivy.jhttp.utilities;

import codes.laivy.jhttp.headers.HeaderKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.*;

public final class Connection {

    // Static initializers

    // todo 08/06/2024: regex performance improvement
    public static boolean validate(@NotNull String string) {
        try {
            parse(string);
            return true;
        } catch (@NotNull ParseException ignore) {
            return false;
        }
    }
    public static @NotNull Connection parse(@NotNull String string) throws ParseException {
        @NotNull Map<String, String> keys = KeyReader.read(string, null, ',');

        // Type
        @NotNull Type type = Type.getById(keys.keySet().stream().findFirst().orElseThrow(() -> new ParseException("cannot find connection type", 0)));
        keys.remove(type.getId().toLowerCase());

        // Headers
        @NotNull Set<HeaderKey<?>> headers = new LinkedHashSet<>();
        for (@NotNull String name : keys.keySet()) headers.add(HeaderKey.create(name));

        // Finish
        return new Connection(type, headers.toArray(new HeaderKey[0]));
    }

    public static @NotNull Connection create(@NotNull Type type) {
        return new Connection(type, new HeaderKey[0]);
    }
    public static @NotNull Connection create(@NotNull Type type, @NotNull HeaderKey<?> @NotNull [] keys) {
        return new Connection(type, keys);
    }

    // Object

    private final @NotNull Type type;
    private final @NotNull HeaderKey<?>[] keys;

    private Connection(@NotNull Type type, @NotNull HeaderKey<?> @NotNull [] keys) {
        this.type = type;
        this.keys = keys;
    }

    // Getters

    public @NotNull Type getType() {
        return type;
    }
    public @NotNull HeaderKey<?> @NotNull [] getKeys() {
        return keys;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return type == that.type && Objects.deepEquals(keys, that.keys);
    }
    @Override
    public int hashCode() {
        return Objects.hash(type, Arrays.hashCode(keys));
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder(getType().getId());

        for (@NotNull HeaderKey<?> key : getKeys()) {
            builder.append(", ").append(key.getName());
        }

        return builder.toString();
    }

    // Classes

    public enum Type {

        KEEP_ALIVE("keep-alive"),
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

        public static @NotNull Type getById(@NotNull String id) {
            @NotNull Optional<Type> optional = Arrays.stream(values()).filter(type -> type.getId().equalsIgnoreCase(id)).findFirst();
            return optional.orElseThrow(() -> new NullPointerException("there's no connection type with id '" + id + "'"));
        }

    }

}
