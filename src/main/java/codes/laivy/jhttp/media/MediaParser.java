package codes.laivy.jhttp.media;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.MediaType.Parameter;
import codes.laivy.jhttp.media.text.TextMediaType;
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

    // Static initializers

    static @NotNull MediaParser<String> getDefault() {
        return TextMediaType.getInstance().getParser();
    }

    // Object

    /**
     * Deserializes a given string into a {@link Content} object based on the specified {@link MediaType}.
     *
     * @param stream the string representation of the content to be deserialized
     * @param parameters the MIME type parameters of this stream
     * @return the deserialized content object
     *
     * @throws IOException if an I/O error occurs during deserialization
     * @throws MediaParserException if an error occurs during deserialization
     */
    @NotNull T deserialize(@NotNull InputStream stream, @NotNull Parameter @NotNull ... parameters) throws MediaParserException, IOException;

    /**
     * Serializes a given {@link Content} object into its string representation.
     *
     * @param content the content object to be serialized
     * @param parameters the MIME type parameters of this stream
     * @return the string representation of the serialized content
     *
     * @throws IOException if an I/O exception occurs, trying to serialize
     */
    @NotNull InputStream serialize(@NotNull T content, @NotNull Parameter @NotNull ... parameters) throws IOException;

}