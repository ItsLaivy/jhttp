package codes.laivy.jhttp.headers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

final class ImmutableHeadersProvider implements Headers {

    // Object

    private final @NotNull Header<?>[] headers;

    ImmutableHeadersProvider(@NotNull Header<?>[] headers) {
        this.headers = Arrays.copyOf(headers, headers.length);
    }

    // Natives

    @Override
    public @NotNull Header<?> @NotNull [] get(@NotNull String name) {
        return stream().filter(header -> header.getName().equalsIgnoreCase(name)).toArray(Header[]::new);
    }
    @Override
    public boolean contains(@NotNull String name) {
        return stream().anyMatch(header -> header.getName().equalsIgnoreCase(name));
    }
    @Override
    public @NotNull Stream<Header<?>> stream() {
        return Arrays.stream(headers);
    }
    @Override
    public int size() {
        return headers.length;
    }
    @Override
    public @NotNull Iterator<Header<?>> iterator() {
        return stream().iterator();
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull ImmutableHeadersProvider headers1 = (ImmutableHeadersProvider) object;
        return Objects.deepEquals(headers, headers1.headers);
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(headers);
    }
    @Override
    public @NotNull String toString() {
        return Arrays.toString(headers);
    }
    
}
