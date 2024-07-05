package codes.laivy.jhttp.element;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.Content;
import codes.laivy.jhttp.media.MediaType;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
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
public interface HttpBody extends Closeable {

    /**
     * Retrieves or create the content of the HTTP body, decoded and transformed to the specified media type.
     *
     * @param mediaType the media type to which the content should be transformed must not be null
     * @param <T> the type of the content after transformation
     * @return the content of the HTTP body transformed to the specified media type
     * @throws NullPointerException if the media type is null
     */
    <T> @NotNull Content<T> getContent(@NotNull MediaType<T> mediaType) throws MediaParserException, IOException;

    /**
     * Provides an {@link InputStream} for reading the raw content of the HTTP body.
     *
     * @return an input stream for reading the raw HTTP body content, never null
     */
    @NotNull InputStream getInputStream();

}
