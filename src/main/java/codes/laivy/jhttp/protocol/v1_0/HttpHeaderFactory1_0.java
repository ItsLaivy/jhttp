package codes.laivy.jhttp.protocol.v1_0;

import codes.laivy.jhttp.element.Target;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.protocol.factory.HttpHeaderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class HttpHeaderFactory1_0 implements HttpHeaderFactory {

    // Object

    private final @NotNull HttpVersion version;

    HttpHeaderFactory1_0(@NotNull HttpVersion1_0 version) {
        this.version = version;
    }

    // Getters

    @Override
    public @NotNull HttpVersion getVersion() {
        return version;
    }

    // Modules

    @Override
    public @NotNull HttpHeaders createMutable(@NotNull Target target) {
        return new HttpHeadersImpl(target);
    }
    @Override
    public @NotNull HttpHeaders createImmutable(@NotNull HttpHeaders clone) {
        return new ImmutableHttpHeadersImpl(clone);
    }

    // Classes

    private class HttpHeadersImpl implements HttpHeaders {

        // Object

        protected final @NotNull List<HttpHeader<?>> list = new LinkedList<>();
        private final @NotNull Target target;

        private HttpHeadersImpl(@NotNull Target target) {
            this.target = target;
        }

        // Natives

        @Override
        public @NotNull HttpHeader<?> @NotNull [] get(@NotNull String name) {
            return list.stream().filter(header -> header.getName().equalsIgnoreCase(name)).toArray(HttpHeader[]::new);
        }
        @Override
        public boolean contains(@NotNull String name) {
            return list.stream().anyMatch(header -> header.getName().equalsIgnoreCase(name));
        }
        @Override
        public @NotNull Stream<HttpHeader<?>> stream() {
            return list.stream();
        }
        @Override
        public int size() {
            return list.size();
        }
        @Override
        public void clear() {
            list.clear();
        }

        @Override
        public boolean put(@NotNull HttpHeader<?> header) {
            remove(header.getKey());
            return add(header);
        }
        @Override
        public boolean add(@NotNull HttpHeader<?> header) {
            if ((target != Target.BOTH && header.getKey().getTarget() != Target.BOTH) && header.getKey().getTarget() != target) {
                throw new IllegalArgumentException("this header collection only accepts " + target.name().toLowerCase() + " headers, the header '" + header.getName() + "' isn't compatible!");
            }
            return list.add(header);
        }
        @Override
        public boolean remove(@NotNull String name) {
            return list.removeIf(header -> header.getName().equalsIgnoreCase(name));
        }
        @Override
        public @NotNull Iterator<HttpHeader<?>> iterator() {
            return list.iterator();
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull HttpHeaderFactory1_0.HttpHeadersImpl headers = (HttpHeadersImpl) object;
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

        @Override
        public @NotNull HttpHeaders clone() {
            try {
                return (HttpHeaders) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("cannot clone " + getVersion() + " headers", e);
            }
        }

    }
    private final class ImmutableHttpHeadersImpl extends HttpHeadersImpl {

        private ImmutableHttpHeadersImpl(@NotNull HttpHeaders headers) {
            super(Target.BOTH);

            for (@NotNull HttpHeader<?> header : headers) {
                list.add(header);
            }
        }

        @Override
        public boolean add(@NotNull HttpHeader<?> header) {
            throw new UnsupportedOperationException("you cannot change the headers of a future request");
        }
        @Override
        public boolean remove(@NotNull String name) {
            throw new UnsupportedOperationException("you cannot change the headers of a future request");
        }
        @Override
        public void clear() {
            throw new UnsupportedOperationException("you cannot change the headers of a future request");
        }

    }

}
