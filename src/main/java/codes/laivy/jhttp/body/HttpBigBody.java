package codes.laivy.jhttp.body;

import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.encoding.ChunkedEncoding;
import codes.laivy.jhttp.encoding.ChunkedEncoding.Chunk;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.media.Content;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.network.BitMeasure;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static codes.laivy.jhttp.headers.HttpHeaderKey.CONTENT_LENGTH;
import static codes.laivy.jhttp.headers.HttpHeaderKey.TRANSFER_ENCODING;

/**
 * This class is designed for handling large HTTP bodies by creating a temporary file
 * and saving all data to it. When accessing the data, a new {@link FileInputStream} is created,
 * which is returned by {@link #getInputStream()}.
 * <p>
 * This approach is not recommended for small bodies/data as the overhead of creating, writing,
 * and reading from a temporary file may outweigh the benefits compared to using
 * {@link HttpSimpleBody}, as example.
 */
// todo: rename this to HttpCacheBody
public class HttpBigBody implements HttpBody {

    // Static initializers

    /**
     * This variable defines the minimum amount of data for a body to be automatically considered a big body.
     * It is used by the default factories of JHTTP. If the size of a content exceeds the value of this variable,
     * it will automatically be considered a big body by the factory. The default value is 32 kilobytes.
     */
    public static @NotNull BitMeasure MIN_BIG_BODY_SIZE = BitMeasure.create(BitMeasure.Level.KILOBYTES, 32D);

    // Object

    protected final @NotNull Object lock = new Object();
    protected final @NotNull Map<MediaType<?>, Content<?>> contentMap = new HashMap<>();
    private final @NotNull File file;

    protected volatile boolean closed = false;

    /**
     * Constructs an instance of {@link HttpBigBody} with the provided {@link Byte} array.
     * A temporary file is created and data from the byte array is saved into it.
     *
     * @param bytes the bytes containing the HTTP body data
     * @throws IOException if an I/O error occurs
     */
    public HttpBigBody(byte @NotNull [] bytes) throws IOException {
        this.file = File.createTempFile("jhttp-", "-big_body");
        this.file.deleteOnExit();

        update(new ByteArrayInputStream(bytes));
    }

    /**
     * Constructs an instance of {@link HttpBigBody} with the provided {@link InputStream}.
     * A temporary file is created and data from the stream is saved into it.
     *
     * @param stream the input stream containing the HTTP body data
     * @throws IOException if an I/O error occurs
     */
    public HttpBigBody(@NotNull InputStream stream) throws IOException {
        this.file = File.createTempFile("jhttp-", "-big_body");
        this.file.deleteOnExit();

        update(stream);
    }

    // Getters

    /**
     * Returns the temporary file where the HTTP body data is stored.
     *
     * @return the temporary file
     */
    public final @NotNull File getFile() {
        synchronized (lock) {
            return file;
        }
    }

    /**
     * Updates the content of the temporary file with the data from the provided input stream.
     *
     * @param stream the input stream containing new data
     * @throws IOException if an I/O error occurs during writing to the file
     */
    protected void update(@NotNull InputStream stream) throws IOException {
        try (@NotNull FileOutputStream output = new FileOutputStream(file)) {
            @NotNull BitMeasure size = BitMeasure.create(BitMeasure.Level.KILOBYTES, 8D);
            byte[] bytes = new byte[(int) size.getBytes()];

            int read;
            while ((read = stream.read(bytes)) != -1) {
                output.write(bytes, 0, read);
                output.flush();
            }
        }
    }

    /**
     * Retrieves the content of the specified media type from the temporary file.
     *
     * @param version the http version of the content
     * @param mediaType the media type to retrieve
     * @param <T> the type of content
     *
     * @return the content of the specified media type
     * @throws MediaParserException if a parsing error occurs
     * @throws IOException if an I/O error occurs or if the http body is closed
     */
    @Override
    public @NotNull <T> Content<T> getContent(@NotNull HttpVersion<?> version, @NotNull MediaType<T> mediaType) throws MediaParserException, IOException {
        if (closed) {
            throw new IOException("this http body is closed");
        }

        @NotNull Content<T> content;

        if (contentMap.containsKey(mediaType)) {
            //noinspection unchecked
            content = (Content<T>) contentMap.get(mediaType);
        } else {
            synchronized (lock) {
                @NotNull T data = mediaType.getParser().deserialize(version, getInputStream(), mediaType.getParameters());
                content = new BigContent<>(version, mediaType, data);
            }

            contentMap.put(mediaType, content);
        }

        return content;
    }

    /**
     * Returns an input stream for reading the temporary file containing the HTTP body data.
     *
     * @return the input stream for reading the file
     * @throws IOException if an I/O error occurs or if the http body is closed
     */
    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        if (closed) {
            throw new IOException("this http body is closed");
        } else synchronized (lock) {
            return Files.newInputStream(file.toPath());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(@NotNull HttpHeaders headers, @NotNull OutputStream out) throws IOException, EncodingException {
        @Nullable Long limit = headers.first(CONTENT_LENGTH).map(HttpHeader::getValue).map(BitMeasure::getBytes).orElse(null);
        @Nullable ChunkedEncoding chunked = (ChunkedEncoding) Arrays.stream(headers.first(TRANSFER_ENCODING).map(HttpHeader::getValue).orElse(new Deferred[0])).filter(deferred -> deferred.toString().equalsIgnoreCase("chunked")).map(Deferred::retrieve).findFirst().orElse(null);
        @NotNull InputStream stream = getInputStream();

        // Dynamic buffer
        final int bufferSize = Math.min(64000, Math.max(4096, stream.available() / 16));
        byte[] buffer = new byte[bufferSize];

        long bytesReadTotal = 0;
        int bytesRead;

        while (true) {
            int bytesToRead = (limit != null) ? (int) Math.min(bufferSize, limit - bytesReadTotal) : bufferSize;
            bytesRead = stream.read(buffer, 0, bytesToRead);
            if (bytesRead == -1) break;

            byte[] processed = Arrays.copyOf(buffer, bytesRead);
            processed = BodyUtils.encode(headers, processed);

            if (chunked != null) {
                @NotNull Chunk chunk = new Chunk(Chunk.Length.create(processed.length), new ByteArrayInputStream(processed));

                chunk.write(out);
                out.flush();
            } else {
                out.write(processed);
                if (limit != null) out.flush();
            }

            bytesReadTotal += bytesRead;

            if (limit != null && bytesReadTotal >= limit) {
                break;
            }
        }

        // If it's chunked, send the empty (final) chunk; All the data was delivered successfully.
        if (chunked != null) {
            Chunk.empty().write(out);
            out.flush();
        }

        stream.close();
    }

    /**
     * Closes this {@code HttpBigBody} by deleting the temporary file.
     *
     * @throws IOException if an I/O error occurs, or if this http body is already closed
     */
    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException("this http body is already closed");
        } else try {
            synchronized (lock) {
                // Close contents
                for (@NotNull Content<?> content : contentMap.values()) {
                    content.flush();
                }

                // Delete file
                Files.delete(file.toPath());
            }
        } finally {
            closed = true;
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull HttpBigBody that = (HttpBigBody) object;
        return Objects.equals(file, that.file);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }

    @Override
    public @NotNull String toString() {
        return "HttpBigBody{" +
                "file=" + file +
                '}';
    }

    // Classes

    /**
     * This inner class represents the content of a specific media type stored in the temporary file.
     *
     * @param <T> the type of the content
     */
    protected class BigContent<T> implements Content<T> {

        private final @NotNull HttpVersion<?> version;
        private final @NotNull MediaType<T> mediaType;
        private volatile @NotNull T data;

        /**
         * Constructs an instance of {@code BigContent} with the specified media type and data.
         *
         * @param version the http version of the content
         * @param mediaType the media type of the content
         * @param data the content data
         */
        public BigContent(@NotNull HttpVersion<?> version, @NotNull MediaType<T> mediaType, @NotNull T data) {
            this.version = version;
            this.mediaType = mediaType;
            this.data = data;
        }

        // Getters

        /**
         * Returns the media type of this content.
         *
         * @return the media type
         */
        @Override
        public @NotNull MediaType<T> getMediaType() {
            return mediaType;
        }

        /**
         * Returns the {@code HttpBody} that contains this content.
         *
         * @return the HTTP body
         */
        @Override
        public @NotNull HttpBody getBody() {
            return HttpBigBody.this;
        }

        /**
         * Gets the http version this content is from
         *
         * @return the http version
         */
        @Override
        public @NotNull HttpVersion<?> getVersion() {
            return version;
        }

        /**
         * Returns the content data.
         *
         * @return the content data
         */
        @Override
        public @NotNull T getData() {
            return data;
        }

        /**
         * Sets the content data and optionally flushes the data to the temporary file.
         *
         * @param data the new content data
         * @param autoFlush if {@code true}, flushes the data to the file
         * @throws IOException if an I/O error occurs
         */
        @Override
        public void setData(@NotNull T data, boolean autoFlush) throws IOException {
            this.data = data;
            if (autoFlush) flush();
        }

        // Modules

        /**
         * Flushes the current content data to the temporary file.
         *
         * @throws IOException if an I/O error occurs during writing to the file or if the http body is closed
         */
        @Override
        public void flush() throws IOException {
            if (closed) {
                throw new IOException("this http body is closed");
            }

            synchronized (lock) {
                try (@NotNull InputStream stream = getMediaType().getParser().serialize(getVersion(), getData(), getMediaType().getParameters())) {
                    update(stream);
                } catch (@NotNull MediaParserException e) {
                    throw new RuntimeException("cannot flush http big body", e);
                }
            }
        }
    }
}