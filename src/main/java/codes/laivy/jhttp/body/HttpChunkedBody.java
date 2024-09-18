package codes.laivy.jhttp.body;

import codes.laivy.jhttp.encoding.ChunkedEncoding.Chunk;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.headers.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class HttpChunkedBody extends HttpBigBody {

    // Static initializers

    private static @NotNull InputStream read(@NotNull Chunk @NotNull ... chunks) {
        @NotNull Collection<InputStream> collection = Arrays.stream(chunks).map(Chunk::getContent).collect(Collectors.toList());
        return new SequenceInputStream(Collections.enumeration(collection));
    }

    // Object

    private final @NotNull Chunk @NotNull [] chunks;

    public HttpChunkedBody(@NotNull Chunk @NotNull ... chunks) throws IOException {
        super(read(chunks));
        this.chunks = chunks;
    }

    // Getters

    public @NotNull Chunk @NotNull [] getChunks() {
        return chunks;
    }

    // Modules

    @Override
    public void write(@NotNull HttpHeaders headers, @NotNull OutputStream out) throws IOException, EncodingException {
        super.write(headers, out);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof HttpChunkedBody)) return false;
        if (!super.equals(object)) return false;
        @NotNull HttpChunkedBody that = (HttpChunkedBody) object;
        return Objects.deepEquals(getChunks(), that.getChunks());
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), Arrays.hashCode(getChunks()));
    }
    
}
