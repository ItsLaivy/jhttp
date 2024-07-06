package codes.laivy.jhttp.protocol.factory;

import codes.laivy.jhttp.client.HttpClient;
import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.element.request.HttpRequest.Future;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.parser.element.HttpBodyParseException;
import codes.laivy.jhttp.exception.parser.element.HttpRequestParseException;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.url.URIAuthority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;

/**
 * An abstract class responsible for constructing HTTP requests. This class provides methods for serializing,
 * parsing, and managing HTTP request futures.
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface HttpRequestFactory {

    /**
     * Retrieves the HTTP version used by this factory.
     *
     * @return The HTTP version used by this factory. Never null.
     */
    @NotNull HttpVersion getVersion();

    // Modules

    @NotNull HttpRequest create(@NotNull Method method, @Nullable URIAuthority authority, @NotNull URI uri, @NotNull HttpHeaders headers, @NotNull HttpBody body);

    /**
     * Transforms an HTTP request into a string representation.
     *
     * @param request The HTTP request to be serialized. Must not be null.
     * @return The string representation of the HTTP request. Never null.
     * @throws EncodingException if any exception occurs, trying to encode body
     * @throws IOException if any exception occurs, trying to read body
     */
    @NotNull String serialize(@NotNull HttpRequest request) throws EncodingException, IOException;

    /**
     * Transforms a string into a valid HTTP request object.
     *
     * @param string The string to be parsed. Must not be null.
     * @return The parsed HTTP request. Never null.
     *
     * @throws HttpRequestParseException If a parse exception occurs.
     * @throws HttpBodyParseException If a parse exception occurs, trying to read the body.
     */
    @NotNull HttpRequest parse(@NotNull String string) throws HttpRequestParseException, HttpBodyParseException;

    /**
     * Creates a Future for the client's HTTP request. Whenever the client sends new data, it should be sent to this method.
     * It will return a new Future, or an existing Future if it has not been completed previously.
     * <p>
     * The method should return an existing Future if there is a pending one for this same client, and
     * upon creating a new one, it should add it to this variable.
     *
     * @param client The HttpClient for which to create the Future. Must not be null.
     * @param string The string data to be parsed into the Future. Must not be null.
     * @return The Future representing the client's HTTP request. Never null.
     *
     * @throws HttpRequestParseException If a parse exception occurs.
     */
    @NotNull Future parse(@NotNull HttpClient client, @NotNull String string) throws HttpRequestParseException;

    /**
     * Checks if a string is a valid HTTP request that can be used without issues in the {@link #parse(String)} method.
     *
     * @param string The string to be validated. Must not be null.
     * @return True if the string is a valid HTTP request, false otherwise.
     */
    boolean validate(@NotNull String string);

}