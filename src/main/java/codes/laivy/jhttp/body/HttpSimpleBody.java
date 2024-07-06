package codes.laivy.jhttp.body;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.Content;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.network.BitMeasure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents a simple HTTP body stored as a byte array.
 * It provides methods to access and update the content in various media types.
 */
public class HttpSimpleBody implements HttpBody {

    protected final @NotNull Map<MediaType<?>, Content<?>> contentMap = new HashMap<>();
    protected byte @NotNull [] bytes;

    /**
     * Constructs an instance of {@code HttpSimpleBody} with the provided byte array.
     *
     * @param bytes the byte array containing the HTTP body data
     */
    public HttpSimpleBody(byte @NotNull [] bytes) {
        this.bytes = bytes;
    }

    // Getters

    /**
     * Returns the byte array containing the HTTP body data.
     *
     * @return the byte array
     */
    public final byte @NotNull [] getBytes() {
        return bytes;
    }

    /**
     * Retrieves the content of the specified media type from the byte array.
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
            @NotNull T data = mediaType.getParser().deserialize(getInputStream(), mediaType.getParameters());
            content = new SimpleContent<>(mediaType, data);

            contentMap.put(mediaType, content);
        }

        return content;
    }

    /**
     * Returns an input stream for reading the byte array containing the HTTP body data.
     *
     * @return the input stream for reading the byte array
     * @throws IOException if an I/O error occurs
     */
    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull HttpSimpleBody that = (HttpSimpleBody) object;
        return Objects.deepEquals(getBytes(), that.getBytes());
    }
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(getBytes()));
    }

    @Override
    public @NotNull String toString() {
        return new String(getBytes());
    }

    // Classes

    /**
     * This inner class represents the content of a specific media type stored in the byte array.
     *
     * @param <T> the type of the content
     */
    protected class SimpleContent<T> implements Content<T> {

        private final @NotNull MediaType<T> mediaType;
        private volatile @NotNull T data;

        /**
         * Constructs an instance of {@code SimpleContent} with the specified media type and data.
         *
         * @param mediaType the media type of the content
         * @param data the content data
         */
        public SimpleContent(@NotNull MediaType<T> mediaType, @NotNull T data) {
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
            return HttpSimpleBody.this;
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
         * Sets the content data and optionally flushes the data to the byte array.
         *
         * @param data the new content data
         * @param autoFlush if {@code true}, flushes the data to the byte array
         * @throws IOException if an I/O error occurs
         */
        @Override
        public void setData(@NotNull T data, boolean autoFlush) throws IOException {
            this.data = data;
            if (autoFlush) flush();
        }

        // Modules

        /**
         * Flushes the current content data to the byte array.
         *
         * @throws IOException if an I/O error occurs during writing to the byte array
         */
        @Override
        public void flush() throws IOException {
            try (
                    @NotNull InputStream stream = getMediaType().getParser().serialize(getData(), getMediaType().getParameters());
                    @NotNull ByteArrayOutputStream output = new ByteArrayOutputStream()
            ) {
                @NotNull BitMeasure size = BitMeasure.create(BitMeasure.Level.KILOBYTES, 2D);
                byte[] buffer = new byte[(int) size.getBytes()];

                int read;
                while ((read = stream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                    output.flush();
                }

                HttpSimpleBody.this.bytes = output.toByteArray();
            }
        }
    }
}