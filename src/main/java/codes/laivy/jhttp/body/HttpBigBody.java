package codes.laivy.jhttp.body;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.Content;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.network.BitMeasure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class is designed for handling large HTTP bodies by creating a temporary file
 * and saving all data to it. When accessing the data, a new {@link FileInputStream} is created,
 * which is returned by {@link #getInputStream()}.
 * <p>
 * This approach is not recommended for small bodies/data as the overhead of creating, writing,
 * and reading from a temporary file may outweigh the benefits compared to using
 * {@link HttpSimpleBody}, as example.
 */
public class HttpBigBody implements HttpBody, Closeable {

    // Static initializers

    /**
     * This variable defines the minimum amount of data for a body to be automatically considered a big body.
     * It is used by the default factories of JHTTP. If the size of a content exceeds the value of this variable,
     * it will automatically be considered a big body by the factory.
     */
    public static @NotNull BitMeasure MIN_BIG_BODY_SIZE = BitMeasure.create(BitMeasure.Level.MEGABYTES, 1D);

    // Object

    protected final @NotNull Object lock = new Object();
    protected final @NotNull Map<MediaType<?>, Content<?>> contentMap = new HashMap<>();
    private final @NotNull File file;

    /**
     * Constructs an instance of {@link HttpBigBody} with the provided {@link Byte} array.
     * A temporary file is created and data from the byte array is saved into it.
     *
     * @param bytes the bytes containing the HTTP body data
     * @throws IOException if an I/O error occurs
     */
    public HttpBigBody(byte @NotNull [] bytes) throws IOException {
        file = File.createTempFile("jhttp-", "-big_body");
        file.deleteOnExit();

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
        file = File.createTempFile("jhttp-", "-big_body");
        file.deleteOnExit();

        update(stream);
    }

    // Getters

    /**
     * Returns the temporary file where the HTTP body data is stored.
     *
     * @return the temporary file
     */
    public final @NotNull File getFile() {
        return file;
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
     * @param mediaType the media type to retrieve
     * @param <T> the type of content
     * @return the content of the specified media type
     * @throws MediaParserException if a parsing error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public @NotNull <T> Content<T> getContent(@NotNull MediaType<T> mediaType) throws MediaParserException, IOException {
        @NotNull Content<T> content;

        if (contentMap.containsKey(mediaType)) {
            //noinspection unchecked
            content = (Content<T>) contentMap.get(mediaType);
        } else {
            synchronized (lock) {
                @NotNull T data = mediaType.getParser().deserialize(getInputStream(), mediaType.getParameters());
                content = new BigContent<>(mediaType, data);
            }

            contentMap.put(mediaType, content);
        }

        return content;
    }

    /**
     * Returns an input stream for reading the temporary file containing the HTTP body data.
     *
     * @return the input stream for reading the file
     * @throws IOException if an I/O error occurs
     */
    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        synchronized (lock) {
            return Files.newInputStream(file.toPath());
        }
    }

    /**
     * Closes this {@code HttpBigBody} by deleting the temporary file.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        synchronized (lock) {
            Files.delete(file.toPath());
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull HttpBigBody that = (HttpBigBody) object;
        return Objects.equals(getFile(), that.getFile());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getFile());
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

        private final @NotNull MediaType<T> mediaType;
        private volatile @NotNull T data;

        /**
         * Constructs an instance of {@code BigContent} with the specified media type and data.
         *
         * @param mediaType the media type of the content
         * @param data the content data
         */
        public BigContent(@NotNull MediaType<T> mediaType, @NotNull T data) {
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
         * @throws IOException if an I/O error occurs during writing to the file
         */
        @Override
        public void flush() throws IOException {
            synchronized (lock) {
                try (@NotNull InputStream stream = getMediaType().getParser().serialize(getData(), getMediaType().getParameters())) {
                    update(stream);
                }
            }
        }
    }
}