package codes.laivy.jhttp.url;

import codes.laivy.jhttp.exception.parser.FilesystemProtocolException;
import codes.laivy.jhttp.url.csp.ContentSecurityPolicy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.File;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class FileSystem<T> implements ContentSecurityPolicy.Source {

    // Static initializers

    public static boolean validate(@NotNull String string) {
        return string.contains("filesystem:") && string.split(":").length >= 2;
    }
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static @NotNull FileSystem<?> parse(@NotNull String string) throws FilesystemProtocolException, ParseException {
        if (validate(string)) {
            string = string.substring("filesystem:".length());

            @NotNull String[] parts = string.split(":", 2);
            @NotNull String name = parts[0];
            @NotNull String data = parts[1];

            @NotNull Optional<Protocol<?>> optional = Protocol.retrieve(name);

            if (optional.isPresent()) {
                @NotNull Protocol protocol = optional.get();
                return create(protocol, protocol.read(data));
            } else {
                throw new FilesystemProtocolException("unknown filesystem protocol '" + name + "'");
            }
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid file system url", 0);
        }
    }

    public static @NotNull FileSystem<File> file(@UnknownNullability File file) {
        return create(Protocol.FILE, file);
    }

    public static <E> @NotNull FileSystem<E> create(@NotNull Protocol<E> protocol, @UnknownNullability E value) {
        return new FileSystem<>(protocol, value);
    }

    // Object

    private final @NotNull Protocol<T> protocol;
    private final @UnknownNullability T value;

    private FileSystem(@NotNull Protocol<T> protocol, @UnknownNullability T value) {
        this.protocol = protocol;
        this.value = value;
    }

    // Getters

    public @NotNull Protocol<T> getProtocol() {
        return protocol;
    }
    public @UnknownNullability T getValue() {
        return value;
    }

    @Override
    public @NotNull Type getType() {
        return Type.FILESYSTEM;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull FileSystem<?> that = (FileSystem<?>) object;
        return Objects.equals(protocol, that.protocol) && Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() {
        return Objects.hash(protocol, value);
    }
    @Override
    public @NotNull String toString() {
        return "filesystem:" + getProtocol().getName() + ":" + getProtocol().write(getValue());
    }

    // Classes

    public abstract static class Protocol<T> {

        // Static initializers

        // todo: multi threading
        private static final @NotNull Set<Protocol<?>> collection = ConcurrentHashMap.newKeySet();

        public static @NotNull Collection<Protocol<?>> retrieve() {
            @NotNull Set<Protocol<?>> protocols = new HashSet<>(collection);

            // If there's a custom protocol with any of these names, it will not be added
            // since Sets doesn't allow multiples elements with the same properties.
            // todo: add more default protocols
            protocols.add(FILE);

            return Collections.unmodifiableSet(protocols);
        }
        public static @NotNull Optional<Protocol<?>> retrieve(@NotNull String name) {
            return stream().filter(protocol -> protocol.getName().equalsIgnoreCase(name)).findFirst();
        }

        public static boolean add(@NotNull Protocol<?> protocol) {
            return collection.add(protocol);
        }
        public static boolean remove(@NotNull Protocol<?> protocol) {
            return collection.remove(protocol);
        }

        public static boolean contains(@NotNull Protocol<?> protocol) {
            return retrieve().contains(protocol);
        }
        public static boolean contains(@NotNull String name) {
            return retrieve(name).isPresent();
        }

        public int size() {
            return retrieve().size();
        }

        public static @NotNull Stream<Protocol<?>> stream() {
            return retrieve().stream();
        }
        public static @NotNull Iterator<Protocol<?>> iterator() {
            return retrieve().iterator();
        }

        public static @NotNull Protocol<?>[] toArray() {
            return retrieve().toArray(new Protocol[0]);
        }

        // Defaults

        public static @NotNull Protocol<File> FILE = new Protocol<File>("file") {
            @Override
            public @UnknownNullability File read(@NotNull String content) throws FilesystemProtocolException {
                if (!content.startsWith("//")) {
                    throw new FilesystemProtocolException("invalid filesystem 'file' protocol format '" + content + "'");
                } else {
                    return new File(content.substring(2));
                }
            }
            @Override
            public @NotNull String write(@UnknownNullability File content) {
                return "//" + content.toString().replaceAll("\\\\", File.separator);
            }
        };

        static {
            add(FILE);
        }

        // Object

        private final @NotNull String name;

        protected Protocol(@NotNull String name) {
            this.name = name;
        }

        // Getters

        @Contract(pure = true)
        public final @NotNull String getName() {
            return name;
        }

        public abstract @UnknownNullability T read(final @NotNull String content) throws FilesystemProtocolException;
        public abstract @NotNull String write(final @UnknownNullability T content);

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull Protocol<?> protocol = (Protocol<?>) object;
            return Objects.equals(name, protocol.name);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
        @Override
        public @NotNull String toString() {
            return name;
        }

    }

}
