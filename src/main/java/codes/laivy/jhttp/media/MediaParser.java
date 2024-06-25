package codes.laivy.jhttp.media;

import codes.laivy.jhttp.content.Content;
import codes.laivy.jhttp.exception.media.MediaParserException;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Media parsers are media transformers. Each {@link MediaType} has its own parser. For instance,
 * an application/json parser transforms the string into a functional JSON element.
 *
 * @param <T> the type of the content that this parser handles
 */
public interface MediaParser<T> {

    /**
     * Deserializes a given string into a {@link Content} object based on the specified {@link MediaType}.
     *
     * @param media the media type defining the format of the content
     * @param string the string representation of the content to be deserialized
     * @return the deserialized content object
     * @throws MediaParserException if an error occurs during deserialization
     */
    @NotNull Content<T> deserialize(@NotNull MediaType<T> media, @NotNull String string, @NotNull Locale @NotNull ... locales) throws MediaParserException;

    /**
     * Serializes a given {@link Content} object into its string representation.
     *
     * @param content the content object to be serialized
     * @return the string representation of the serialized content
     */
    @NotNull String serialize(@NotNull Content<T> content);

    /**
     * Validates if a given string conforms to the specified {@link MediaType}.
     *
     * @param media the media type defining the expected format of the string
     * @param string the string to be validated
     * @return true if the string is valid, according to the media type, false otherwise
     */
    boolean validate(@NotNull MediaType<T> media, @NotNull String string);

}