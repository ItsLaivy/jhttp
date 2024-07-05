package codes.laivy.jhttp.element;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.Content;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Represents the body of an HTTP request or response. The raw content is represented as a {@link CharSequence},
 * which reflects the request in its original form, excluding any encodings (such as those specified by the
 * "Content-Encoding" header) or transformations.
 * <p>
 * This interface is designed to handle HTTP bodies and provides methods to retrieve decoded and transformed content
 * when possible. It allows for the creation of an {@link HttpBody} instance from a given {@link Content} object,
 * and offers methods to access the content in its various forms.
 * </p>
 *
 * @see CharSequence
 * @see Content
 * @since 1.0-SNAPSHOT
 */
public interface HttpBody extends Closeable {

    // Static initializers

    static @NotNull HttpBody empty() {
        try {
            return create(new InputStream() {
                @Override
                public int read() {
                    return -1;
                }
            });
        } catch (@NotNull IOException e) {
            throw new RuntimeException(e);
        }
    }

    static @NotNull HttpBody create(@NotNull String string) throws MediaParserException, IOException {
        return create(MediaType.TEXT_PLAIN(), new ByteArrayInputStream(string.getBytes()));
    }
    static @NotNull HttpBody create(@NotNull String string, @NotNull Charset charset) throws MediaParserException, IOException {
        return create(MediaType.TEXT_PLAIN(), new ByteArrayInputStream(string.getBytes(charset)));
    }

    static <T> @NotNull HttpBody create(@NotNull InputStream stream) throws IOException {
        try {
            return HttpVersion.HTTP1_1().getBodyFactory().create(null, stream);
        } catch (@NotNull MediaParserException e) {
            throw new RuntimeException("cannot parse input stream", e);
        }
    }
    static @NotNull HttpBody create(@NotNull MediaType<?> mediaType, @NotNull InputStream stream) throws MediaParserException, IOException {
        return HttpVersion.HTTP1_1().getBodyFactory().create(mediaType, stream);
    }
    static <T> @NotNull HttpBody create(@NotNull MediaType<T> mediaType, @NotNull T type) {
        try {
            return HttpVersion.HTTP1_1().getBodyFactory().create(mediaType, mediaType.getParser().serialize(type, mediaType.getParameters()));
        } catch (@NotNull MediaParserException | @NotNull IOException e) {
            throw new RuntimeException("cannot create http body with the given parameters", e);
        }
    }

    // Object

    /**
     * Retrieves or create the content of the HTTP body, decoded and transformed to the specified media type.
     *
     * @param mediaType the media type to which the content should be transformed must not be null
     * @param <T> the type of the content after transformation
     * @return the content of the HTTP body transformed to the specified media type
     *
     * @throws MediaParserException if an exception occurs, trying to parse the content
     * @throws IOException if an exception occurs, trying to read content
     */
    <T> @NotNull Content<T> getContent(@NotNull MediaType<T> mediaType) throws MediaParserException, IOException;

    /**
     * Provides an {@link InputStream} for reading the raw content of the HTTP body.
     *
     * @return an input stream for reading the raw HTTP body content, never null
     */
    @NotNull InputStream getInputStream();

}
