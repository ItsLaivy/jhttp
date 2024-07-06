package codes.laivy.jhttp.body;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.Content;
import codes.laivy.jhttp.media.MediaType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

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
public interface HttpBody {

    // Static initializers

    static @NotNull HttpBody empty() {
        try {
            return create(new byte[0]);
        } catch (@NotNull IOException e) {
            throw new RuntimeException(e);
        }
    }

    static @NotNull HttpBody create(byte @NotNull [] bytes) throws IOException {
        if (bytes.length >= HttpBigBody.MIN_BIG_BODY_SIZE.getBytes()) {
            return new HttpBigBody(bytes);
        } else {
            return new HttpSimpleBody(bytes);
        }
    }
    static <T> @NotNull HttpBody create(@NotNull MediaType<T> mediaType, @NotNull T data) throws IOException {
        try (@NotNull InputStream stream = mediaType.getParser().serialize(data, mediaType.getParameters())) {
            int available = stream.available();

            if (available >= HttpBigBody.MIN_BIG_BODY_SIZE.getBytes()) {
                return new HttpBigBody(stream);
            } else {
                return new HttpSimpleBody(stream);
            }
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
    @NotNull InputStream getInputStream() throws IOException;

}