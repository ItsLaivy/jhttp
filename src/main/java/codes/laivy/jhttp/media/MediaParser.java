package codes.laivy.jhttp.media;

import codes.laivy.jhttp.exception.media.MediaParserException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Media parsers are media transformers.
 * Each {@link MediaType} has its own parser.
 * <p>
 * For example, an "application/json" parser transforms the string into a functional JSON element.
 *
 * @param <T> the type of the content that this parser handles
 */
public interface MediaParser<T> {

    /**
     * Retrieves the current media type of this media parser
     *
     * @return the media type associated with this parser
     */
    @NotNull MediaType<T> getMediaType();

    /**
     * Deserializes a given string into a {@link Content} object based on the specified {@link MediaType}.
     *
     * @param stream the string representation of the content to be deserialized
     * @return the deserialized content object
     * @throws IOException if an I/O error occurs during deserialization
     * @throws MediaParserException if an error occurs during deserialization
     */
    @NotNull T deserialize(@NotNull InputStream stream) throws MediaParserException, IOException;

    /**
     * Serializes a given {@link Content} object into its string representation.
     *
     * @param content the content object to be serialized
     * @return the string representation of the serialized content
     */
    @NotNull InputStream serialize(@NotNull T content);

}