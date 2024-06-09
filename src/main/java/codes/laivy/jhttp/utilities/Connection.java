package codes.laivy.jhttp.utilities;

import codes.laivy.jhttp.headers.HeaderKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Connection {

    // Static initializers

    // todo 08/06/2024: regex performance improvement
    public static boolean isConnection(@NotNull String string) {
        try {
            parse(string);
            return true;
        } catch (@NotNull ParseException ignore) {
            return false;
        }
    }
    public static @NotNull Connection parse(@NotNull String string) throws ParseException {
        @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
        @NotNull Matcher matcher = pattern.matcher(string);

        @NotNull Type type;
        @NotNull Set<HeaderKey<?>> keys = new LinkedHashSet<>();

        if (!matcher.find()) {
            throw new ParseException("cannot parse '" + string + "' into a valid connection", 0);
        } else {
            @NotNull Optional<Type> optional = Arrays.stream(Type.values()).filter(t -> t.getId().equalsIgnoreCase(matcher.group())).findFirst();

            if (optional.isPresent()) {
                type = optional.get();
            } else {
                throw new ParseException("unknown connection type '" + matcher.group() + "'", matcher.start());
            }
        }

        while (matcher.find()) {
            @NotNull String name = matcher.group();
            keys.add(HeaderKey.create(name));
        }

        return new Connection(type, keys.toArray(new HeaderKey[0]));
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

    }

}
