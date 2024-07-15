package codes.laivy.jhttp.body;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.Content;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.protocol.HttpVersion;
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
            return create(HttpVersion.HTTP1_1(), new byte[0]);
        } catch (@NotNull IOException e) {
            throw new RuntimeException(e);
        }
    }

    static @NotNull HttpBody create(@NotNull HttpVersion version, byte @NotNull [] bytes) throws IOException {
        if (bytes.length >= HttpBigBody.MIN_BIG_BODY_SIZE.getBytes()) {
            return new HttpBigBody(version, bytes);
        } else {
            return new HttpSimpleBody(version, bytes);
        }
    }
    static @NotNull HttpBody create(@NotNull HttpVersion version, @NotNull InputStream stream) throws IOException {
        if (stream.available() >= HttpBigBody.MIN_BIG_BODY_SIZE.getBytes()) {
            return new HttpBigBody(version, stream);
        } else {
            return new HttpSimpleBody(version, stream);
        }
    }
    static <T> @NotNull Content<T> create(@NotNull HttpVersion version, @NotNull MediaType<T> mediaType, @NotNull T data) {
        try (@NotNull InputStream stream = mediaType.getParser().serialize(version, data, mediaType.getParameters())) {
            int available = stream.available();

            @NotNull HttpBody body;
            if (available >= HttpBigBody.MIN_BIG_BODY_SIZE.getBytes()) {
                body = new HttpBigBody(version, stream);
            } else {
                body = new HttpSimpleBody(version, stream);
            }

            return body.getContent(mediaType);
        } catch (@NotNull IOException | @NotNull MediaParserException e) {
            throw new RuntimeException(e);
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
     * The version instance used to create this http body. The body is important to serialize/deserialize contents
     *
     * @return the http version
     */
    @NotNull HttpVersion getVersion();

    /**
     * Provides an {@link InputStream} for reading the raw content of the HTTP body.
     *
     * @return an input stream for reading the raw HTTP body content, never null
     * @throws IOException if an I/O exception occurs while perform
     */
    @NotNull InputStream getInputStream() throws IOException;

    /**
     * Clones the {@link HttpBody} using the specified version.
     *
     * @return a new http body with the selected version
     * @throws IOException if an I/O exception occurs while perform
     */
    default @NotNull HttpBody clone(@NotNull HttpVersion version) throws IOException {
        return create(version, getInputStream());
    }

}
