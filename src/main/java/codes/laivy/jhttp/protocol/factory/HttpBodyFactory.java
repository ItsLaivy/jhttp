package codes.laivy.jhttp.protocol.factory;

import codes.laivy.jhttp.element.HttpBody;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.exception.parser.element.HttpBodyParseException;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * A factory interface for creating and handling {@link HttpBody} instances. This interface provides methods
 *
 * @see HttpVersion
 * @see HttpHeaders
 * @see HttpBody
 */
public interface HttpBodyFactory {

    // Getters

    /**
     * Retrieves the HTTP version used by this factory.
     *
     * @return The HTTP version used by this factory. Never null.
     */
    @NotNull HttpVersion getVersion();

    // Modules

    @NotNull HttpBody parse(@NotNull HttpHeaders headers, @NotNull String content) throws HttpBodyParseException;
    @NotNull String serialize(@NotNull HttpHeaders headers, @NotNull HttpBody body) throws IOException, EncodingException;

    /**
     * Creates a HttpBody using the media parser (nullable) and the input stream.
     * If the media parser is null, the http body will not have contents, just the raw input stream;
     * otherwise, it will have a default content assigned that can be retrieved using {@link HttpBody#getContent(MediaType)}
     *
     * @param type the media type of the body
     * @param stream the input stream of the content
     * @return the http body
     * @throws MediaParserException if the input stream cannot be parsed using media parser
     * @throws IOException if an I/O exception occurs
     */
    @NotNull HttpBody create(@Nullable MediaType<?> type, @NotNull InputStream stream) throws MediaParserException, IOException;

}