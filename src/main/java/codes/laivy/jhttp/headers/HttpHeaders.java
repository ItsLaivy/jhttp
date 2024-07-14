package codes.laivy.jhttp.headers;

import codes.laivy.jhttp.element.Target;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface HttpHeaders extends Iterable<HttpHeader<?>>, Cloneable {

    // Static initializers

    static @NotNull HttpHeaders create(@NotNull HttpVersion version, @NotNull HttpHeader<?> @NotNull ... headers) {
        return create(version, Target.BOTH, headers);
    }
    static @NotNull HttpHeaders create(@NotNull HttpVersion version, @NotNull Target target, @NotNull HttpHeader<?> @NotNull ... headers) {
        @NotNull HttpHeaders a = version.getHeaderFactory().createMutable(target);

        for (@NotNull HttpHeader<?> header : headers) {
            a.add(header);
        }

        return a;
    }
    static @NotNull HttpHeaders empty() {
        return new HttpHeaders() {
            @Override
            public boolean put(@NotNull HttpHeader<?> header) {
                throw new UnsupportedOperationException("you cannot put headers into a empty http header");
            }
            @Override
            public boolean add(@NotNull HttpHeader<?> header) {
                throw new UnsupportedOperationException("you cannot add headers into a empty http header");
            }
            @Override
            public boolean remove(@NotNull String name) {
                throw new UnsupportedOperationException("you cannot remove headers into a empty http header");
            }

            @Override
            public @NotNull Stream<HttpHeader<?>> stream() {
                return Stream.empty();
            }

            @Override
            public void clear() {
            }

            @Override
            @SuppressWarnings("MethodDoesntCallSuperMethod")
            public @NotNull HttpHeaders clone() {
                return HttpHeaders.empty();
            }

            @Override
            public @NotNull Iterator<HttpHeader<?>> iterator() {
                return stream().iterator();
            }
        };
    }

    // Object

    boolean put(@NotNull HttpHeader<?> header);
    boolean add(@NotNull HttpHeader<?> header);

    default boolean remove(@NotNull HttpHeader<?> header) {
        return this.remove(header.getKey().getName());
    }
    default boolean remove(@NotNull HttpHeaderKey<?> key) {
        return this.remove(key.getName());
    }
    boolean remove(@NotNull String name);

    default @NotNull HttpHeader<?> @NotNull [] get(@NotNull String name) {
        return stream().filter(header -> header.getName().equalsIgnoreCase(name)).toArray(HttpHeader[]::new);
    }

    default boolean contains(@NotNull HttpHeaderKey<?> key) {
        return contains(key.getName());
    }
    default boolean contains(@NotNull String name) {
        return stream().anyMatch(header -> header.getName().equalsIgnoreCase(name));
    }

    @NotNull Stream<HttpHeader<?>> stream();

    default int size() {
        return (int) stream().count();
    }
    
    void clear();

    default int count(@NotNull HttpHeaderKey<?> key) {
        return count(key.getName());
    }
    default int count(@NotNull String name) {
        return (int) stream().filter(header -> header.getName().equalsIgnoreCase(name)).count();
    }

    default @NotNull Optional<HttpHeader<?>> first(@NotNull String name) {
        return stream().filter(header -> header.getName().equalsIgnoreCase(name)).findFirst();
    }
    default @NotNull Optional<HttpHeader<?>> last(@NotNull String name) {
        return stream()
                .filter(header -> header.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList())
                .stream()
                .reduce((first, second) -> second);
    }

    default <E> @NotNull Optional<HttpHeader<E>> first(@NotNull HttpHeaderKey<E> key) throws ClassCastException {
        //noinspection unchecked
        return Optional.ofNullable((HttpHeader<E>) first(key.getName()).orElse(null));
    }
    default <E> @NotNull Optional<HttpHeader<E>> last(@NotNull HttpHeaderKey<E> key) {
        //noinspection unchecked
        return Optional.ofNullable((HttpHeader<E>) last(key.getName()).orElse(null));
    }

    @SuppressWarnings("unchecked")
    default <E> @NotNull HttpHeader<E> @NotNull [] get(@NotNull HttpHeaderKey<E> key) {
        @NotNull List<HttpHeader<E>> headers = new LinkedList<>();

        for (@NotNull HttpHeader<?> header : get(key.getName())) {
            headers.add((HttpHeader<E>) header);
        }

        return headers.toArray(new HttpHeader[0]);
    }
    
    // Implementations
    
    @NotNull
    HttpHeaders clone();

}
