package codes.laivy.jhttp.headers;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Headers extends Iterable<Header<?>>, Cloneable {

    // Object

    boolean put(@NotNull Header<?> header);
    boolean add(@NotNull Header<?> header);

    default boolean remove(@NotNull Header<?> header) {
        return this.remove(header.getKey().getName());
    }
    default boolean remove(@NotNull HeaderKey<?> key) {
        return this.remove(key.getName());
    }
    boolean remove(@NotNull String name);

    default @NotNull Header<?> @NotNull [] get(@NotNull String name) {
        return stream().filter(header -> header.getName().equalsIgnoreCase(name)).toArray(Header[]::new);
    }

    default boolean contains(@NotNull HeaderKey<?> key) {
        return contains(key.getName());
    }
    default boolean contains(@NotNull String name) {
        return stream().anyMatch(header -> header.getName().equalsIgnoreCase(name));
    }

    @NotNull Stream<Header<?>> stream();

    default int size() {
        return (int) stream().count();
    }
    
    void clear();

    default int count(@NotNull HeaderKey<?> key) {
        return count(key.getName());
    }
    default int count(@NotNull String name) {
        return (int) stream().filter(header -> header.getName().equalsIgnoreCase(name)).count();
    }

    default @NotNull Optional<Header<?>> first(@NotNull String name) {
        return stream().filter(header -> header.getName().equalsIgnoreCase(name)).findFirst();
    }
    default @NotNull Optional<Header<?>> last(@NotNull String name) {
        return stream()
                .filter(header -> header.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList())
                .stream()
                .reduce((first, second) -> second);
    }

    default <E> @NotNull Optional<Header<E>> first(@NotNull HeaderKey<E> key) throws ClassCastException {
        //noinspection unchecked
        return Optional.ofNullable((Header<E>) first(key.getName()).orElse(null));
    }
    default <E> @NotNull Optional<Header<E>> last(@NotNull HeaderKey<E> key) {
        //noinspection unchecked
        return Optional.ofNullable((Header<E>) last(key.getName()).orElse(null));
    }

    @SuppressWarnings("unchecked")
    default <E> @NotNull Header<E> @NotNull [] get(@NotNull HeaderKey<E> key) {
        @NotNull List<Header<E>> headers = new LinkedList<>();

        for (@NotNull Header<?> header : get(key.getName())) {
            headers.add((Header<E>) header);
        }

        return headers.toArray(new Header[0]);
    }
    
    // Implementations
    
    @NotNull Headers clone();

}
