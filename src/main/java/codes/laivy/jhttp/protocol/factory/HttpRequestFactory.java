package codes.laivy.jhttp.protocol.factory;

import codes.laivy.jhttp.connection.HttpClient;
import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.element.request.HttpRequest.Future;
import codes.laivy.jhttp.exception.parser.request.HttpRequestParseException;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An abstract class responsible for constructing HTTP requests. This class provides methods for serializing,
 * parsing, and managing HTTP request futures.
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public abstract class HttpRequestFactory {

    // Static initializers

    /**
     * A protected static variable containing the sessions of future HTTP request constructors.
     */
    protected static final @NotNull Map<HttpClient, Future> futures = new HashMap<>();

    /**
     * Retrieves the Future associated with the given HttpClient.
     *
     * @param client The HttpClient for which to retrieve the Future. Must not be null.
     * @return An Optional containing the Future if present, otherwise an empty Optional. Never null.
     */
    public static @NotNull Optional<@NotNull Future> getFuture(@NotNull HttpClient client) {
        return Optional.ofNullable(futures.getOrDefault(client, null));
    }

    /**
     * Retrieves the http request factory according to the http version
     *
     * @param version The http version
     * @return The http request factory according to the http version parameter
     */
    public static @NotNull HttpRequestFactory getInstance(@NotNull HttpVersion version) {
        return version.getRequestFactory();
    }

    // Object

    private final @NotNull HttpVersion version;

    /**
     * Constructor for HttpRequestFactory.
     *
     * @param version The HTTP version used by this factory. Must not be null.
     */
    protected HttpRequestFactory(@NotNull HttpVersion version) {
        this.version = version;
    }

    // Getters

    /**
     * Retrieves the HTTP version used by this factory.
     *
     * @return The HTTP version used by this factory. Never null.
     */
    public final @NotNull HttpVersion getVersion() {
        return version;
    }

    // Modules

    /**
     * Transforms an HTTP request into a string representation.
     *
     * @param request The HTTP request to be serialized. Must not be null.
     * @return The string representation of the HTTP request. Never null.
     */
    public abstract @NotNull String serialize(@NotNull HttpRequest request);

    /**
     * Transforms a string into a valid HTTP request object.
     *
     * @param string The string to be parsed. Must not be null.
     * @throws HttpRequestParseException If a parse exception occurs.
     * @return The parsed HTTP request. Never null.
     */
    public abstract @NotNull HttpRequest parse(@NotNull String string) throws HttpRequestParseException;

    /**
     * Creates a Future for the client's HTTP request. Whenever the client sends new data, it should be sent to this method.
     * It will return a new Future, or an existing Future if it has not been completed previously.
     * <p>
     * The method should return an existing Future if there is a pending one in the {@link #futures} variable, and
     * upon creating a new one, it should add it to this variable.
     *
     * @param client The HttpClient for which to create the Future. Must not be null.
     * @param string The string data to be parsed into the Future. Must not be null.
     * @throws HttpRequestParseException If a parse exception occurs.
     * @return The Future representing the client's HTTP request. Never null.
     */
    public abstract @NotNull Future parse(@NotNull HttpClient client, @NotNull String string) throws HttpRequestParseException;

    /**
     * Checks if a string is a valid HTTP request that can be used without issues in the {@link #parse(String)} method.
     *
     * @param string The string to be validated. Must not be null.
     * @return True if the string is a valid HTTP request, false otherwise.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * String requestString = "GET / HTTP/1.1\r\nHost: example.com\r\n\r\n";
     * boolean isValid = factory.validate(requestString);
     * System.out.println("Is valid HTTP request: " + isValid);
     * }</pre>
     */
    public abstract boolean validate(@NotNull String string);

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull HttpRequestFactory that = (HttpRequestFactory) object;
        return Objects.equals(version, that.version);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(version);
    }

    @Override
    public @NotNull String toString() {
        return "HttpRequestFactory{" +
                "version=" + version +
                '}';
    }

}