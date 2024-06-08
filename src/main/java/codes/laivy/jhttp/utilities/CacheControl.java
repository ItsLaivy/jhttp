package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CacheControl {

    // Static initializers

    public static boolean isCacheControl(@NotNull String string) {
        try {
            parse(string);
            return true;
        } catch (@NotNull ParseException ignore) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static @NotNull CacheControl parse(@NotNull String string) throws ParseException {
        @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
        @NotNull Matcher matcher = pattern.matcher(string);
        @NotNull Map<Key<?>, Object> map = new LinkedHashMap<>();

        int row = 0;
        while (matcher.find()) {
            @NotNull String[] group = matcher.group().split("\\s*=\\s*", 2);
            @NotNull String name = group[0];

            if (group.length == 1) {
                try {
                    @NotNull Key<Void> key = (Key<Void>) Key.getKey(name).orElseThrow(() -> new NullPointerException("unknown cache control key '" + name + "'"));
                    map.put(key, null);
                } catch (@NotNull ClassCastException ignore) {
                    throw new ParseException("cache control key without values '" + name + "'", matcher.start(0));
                }
            } else try {
                @NotNull Long value = Long.parseLong(group[1]);
                @NotNull Key<Long> key = (Key<Long>) Key.getKey(name).orElseThrow(() -> new NullPointerException("unknown cache control key '" + name + "'"));

                map.put(key, value);
            } catch (@NotNull ClassCastException ignore) {
                throw new ParseException("the cache control key '" + name + "' doesn't have values", matcher.start(0));
            } catch (@NotNull NumberFormatException ignore) {
                throw new ParseException("the cache control key '" + name + "' value '" + group[1] + "' isn't valid", matcher.start(1));
            }
        }

        return new CacheControl(map);
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    // Object

    private final @NotNull Map<Key<?>, Object> keys;

    private CacheControl(@NotNull Map<Key<?>, Object> keys) {
        this.keys = keys;
    }

    // Getters

    public boolean has(@NotNull Key<?> key) {
        return keys.containsKey(key);
    }
    public <E> @NotNull Optional<E> get(@NotNull Key<E> key) {
        //noinspection unchecked
        return Optional.ofNullable((E) keys.getOrDefault(key, null));
    }
    public <E> void set(@NotNull Key<E> key, @NotNull E value) {
        keys.put(key, value);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @NotNull CacheControl that = (CacheControl) o;
        return Objects.equals(keys, that.keys);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(keys);
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder();

        for (@NotNull Map.Entry<Key<?>, Object> entry : keys.entrySet()) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(entry.getKey());

            if (entry.getValue() != null) {
                builder.append("=").append(entry.getValue().toString());
            }
        }

        return builder.toString();
    }

    // Classes

    public static final class Key<T> {

        // Static initializers

        public static @NotNull Optional<Key<?>> getKey(@NotNull String name) {
            for (@NotNull Field field : Key.class.getDeclaredFields()) {
                try {
                    if (!field.isAccessible()) continue;
                    @NotNull Key<?> key = (Key<?>) field.get(null);

                    if (key.getName().equalsIgnoreCase(name)) {
                        return Optional.of(key);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("cannot access key field value '" + field.getName() + "'", e);
                }
            }

            return Optional.empty();
        }

        public static final @NotNull Key<Long> MAX_AGE = new Key<>("max-age", Target.BOTH);
        public static final @NotNull Key<Long> MAX_STALE = new Key<>("max-stale", Target.REQUEST);
        public static final @NotNull Key<Long> MIN_FRESH = new Key<>("min-fresh", Target.REQUEST);
        public static final @NotNull Key<Long> S_MAXAGE = new Key<>("s-maxage", Target.RESPONSE);
        public static final @NotNull Key<Long> STALE_WHILE_REVALIDATE = new Key<>("stale-while-revalidate", Target.RESPONSE);
        public static final @NotNull Key<Long> STALE_IF_ERROR = new Key<>("stale-if-error", Target.BOTH);

        public static final @NotNull Key<Void> NO_CACHE = new Key<>("no-cache", Target.BOTH);
        public static final @NotNull Key<Void> NO_STORE = new Key<>("no-store", Target.BOTH);
        public static final @NotNull Key<Void> NO_TRANSFORM = new Key<>("no-transform", Target.BOTH);
        public static final @NotNull Key<Void> ONLY_IF_CACHED = new Key<>("only-if-cached", Target.REQUEST);
        public static final @NotNull Key<Void> MUST_REVALIDATE = new Key<>("must-revalidate", Target.RESPONSE);
        public static final @NotNull Key<Void> PROXY_REVALIDATE = new Key<>("proxy-revalidate", Target.RESPONSE);
        public static final @NotNull Key<Void> MUST_UNDERSTAND = new Key<>("must-understand", Target.RESPONSE);
        public static final @NotNull Key<Void> PRIVATE = new Key<>("private", Target.RESPONSE);
        public static final @NotNull Key<Void> PUBLIC = new Key<>("public", Target.RESPONSE);
        public static final @NotNull Key<Void> IMMUTABLE = new Key<>("immutable", Target.RESPONSE);
        ;

        // Object

        private final @NotNull String name;
        private final @NotNull Target target;

        private Key(@NotNull String name, @NotNull Target target) {
            this.name = name;
            this.target = target;
        }

        // Getters

        public @NotNull String getName() {
            return name;
        }
        public @NotNull Target getTarget() {
            return target;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            @NotNull Key<?> that = (Key<?>) o;
            return name.equalsIgnoreCase(that.name);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(name.toLowerCase());
        }

        @Override
        public @NotNull String toString() {
            return getName().toLowerCase();
        }

    }

    public static final class Builder {

        private final @NotNull Map<Key<?>, Object> keys = new LinkedHashMap<>();

        private Builder() {
        }

        // Modules

        @Contract("_->this")
        public @NotNull Builder maxAge(@NotNull Duration duration) {
            keys.put(Key.MAX_AGE, duration.getSeconds());
            return this;
        }
        @Contract("_->this")
        public @NotNull Builder maxStale(@NotNull Duration duration) {
            keys.put(Key.MAX_STALE, duration.getSeconds());
            return this;
        }
        @Contract("_->this")
        public @NotNull Builder minFresh(@NotNull Duration duration) {
            keys.put(Key.MIN_FRESH, duration.getSeconds());
            return this;
        }
        @Contract("_->this")
        public @NotNull Builder sMaxAge(@NotNull Duration duration) {
            keys.put(Key.S_MAXAGE, duration.getSeconds());
            return this;
        }
        @Contract("_->this")
        public @NotNull Builder staleWhileRevalidate(@NotNull Duration duration) {
            keys.put(Key.STALE_WHILE_REVALIDATE, duration.getSeconds());
            return this;
        }
        @Contract("_->this")
        public @NotNull Builder staleIfError(@NotNull Duration duration) {
            keys.put(Key.STALE_IF_ERROR, duration.getSeconds());
            return this;
        }

        @Contract("->this")
        public @NotNull Builder noCache() {
            keys.put(Key.NO_CACHE, null);
            return this;
        }
        @Contract("->this")
        public @NotNull Builder noStore() {
            keys.put(Key.NO_STORE, null);
            return this;
        }
        @Contract("->this")
        public @NotNull Builder noTransform() {
            keys.put(Key.NO_TRANSFORM, null);
            return this;
        }
        @Contract("->this")
        public @NotNull Builder onlyIfChecked() {
            keys.put(Key.ONLY_IF_CACHED, null);
            return this;
        }
        @Contract("->this")
        public @NotNull Builder mustRevalidate() {
            keys.put(Key.MUST_REVALIDATE, null);
            return this;
        }
        @Contract("->this")
        public @NotNull Builder proxyRevalidate() {
            keys.put(Key.PROXY_REVALIDATE, null);
            return this;
        }
        @Contract("->this")
        public @NotNull Builder mustUnderstand() {
            keys.put(Key.MUST_UNDERSTAND, null);
            return this;
        }
        @Contract("->this")
        public @NotNull Builder private_() {
            keys.put(Key.PRIVATE, null);
            return this;
        }
        @Contract("->this")
        public @NotNull Builder public_() {
            keys.put(Key.PUBLIC, null);
            return this;
        }
        @Contract("->this")
        public @NotNull Builder immutable() {
            keys.put(Key.IMMUTABLE, null);
            return this;
        }

        // Build

        public @NotNull CacheControl build() {
            return new CacheControl(keys);
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            @NotNull Builder builder = (Builder) o;
            return Objects.equals(keys, builder.keys);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(keys);
        }
        @Override
        public @NotNull String toString() {
            return new CacheControl(keys).toString();
        }

    }

}
