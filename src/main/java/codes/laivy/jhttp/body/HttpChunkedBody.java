package codes.laivy.jhttp.body;

import codes.laivy.jhttp.encoding.ChunkedEncoding.Chunk;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class HttpChunkedBody extends HttpBigBody {

    // Static initializers

    public static @NotNull InputStream read(@NotNull Chunk @NotNull ... chunks) {

    }

    // Object

    private final @NotNull Chunk @NotNull [] chunks;

    public HttpChunkedBody(@NotNull Chunk @NotNull ... chunks) throws IOException {
        super(read(chunks));
        this.chunks = chunks;
    }

    public @NotNull Chunk @NotNull [] getChunks() {
        return chunks;
    }

}
