package codes.laivy.jhttp.protocol.factory;

import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.parser.element.HttpBodyParseException;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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

}