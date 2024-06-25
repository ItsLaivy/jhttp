package codes.laivy.jhttp.headers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

final class HeadersImpl implements Headers {

    // Object

    private final @NotNull List<Header<?>> list = new LinkedList<>();

    HeadersImpl() {
    }

    // Natives

    @Override
    public @NotNull Header<?> @NotNull [] get(@NotNull String name) {
        return list.stream().filter(header -> header.getName().equalsIgnoreCase(name)).toArray(Header[]::new);
    }
    @Override
    public boolean contains(@NotNull String name) {
        return list.stream().anyMatch(header -> header.getName().equalsIgnoreCase(name));
    }
    @Override
    public @NotNull Stream<Header<?>> stream() {
        return list.stream();
    }
    @Override
    public int size() {
        return list.size();
    }
    @Override
    public boolean put(@NotNull Header<?> header) {
        remove(header.getKey());
        return add(header);
    }
    @Override
    public boolean add(@NotNull Header<?> header) {
        return list.add(header);
    }
    @Override
    public boolean remove(@NotNull Header<?> header) {
        return list.remove(header);
    }
    @Override
    public boolean remove(@NotNull HeaderKey<?> key) {
        return list.removeIf(header -> header.getName().equalsIgnoreCase(key.getName()));
    }
    @Override
    public boolean remove(@NotNull String name) {
        return list.removeIf(header -> header.getName().equalsIgnoreCase(name));
    }
    @Override
    public @NotNull Iterator<Header<?>> iterator() {
        return list.iterator();
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull HeadersImpl headers = (HeadersImpl) object;
        return Objects.equals(list, headers.list);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(list);
    }
    @Override
    public @NotNull String toString() {
        return list.toString();
    }

}
