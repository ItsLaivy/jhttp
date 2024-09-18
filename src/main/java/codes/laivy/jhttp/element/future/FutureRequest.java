package codes.laivy.jhttp.element.future;

import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.exception.parser.IllegalHttpMethodException;
import codes.laivy.jhttp.exception.parser.IllegalHttpVersionException;
import codes.laivy.jhttp.exception.parser.element.HttpBodyParseException;
import codes.laivy.jhttp.exception.parser.element.HttpRequestParseException;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.url.URIAuthority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.concurrent.Future;

/**
 * A {@code FutureRequest} represents a future HTTP request that has not yet been completed.
 * <p>
 * Users who create an instance of a {@code FutureRequest} must feed this class with new bytes using
 * the {@link #feed(Object)} or {@link #feed(InputStream)} methods until those methods return {@code true},
 * indicating that the request has been fully constructed from the provided data.
 * </p>
 * <p>
 * The HTTP factory that generates this class must carefully handle the feeding process, considering several factors.
 * For example, if the body size provided in the {@code Content-Length} header does not match the current size of the
 * body supplied through the {@code feed} method, the {@code FutureRequest} must wait until more bytes are provided,
 * among other potential conditions.
 * </p>
 * <p>
 * This class was designed to maintain asynchronicity in the deserialization of HTTP requests, allowing users to implement
 * their own method for asynchronous deserialization while feeding this class with new bytes as they arrive. However, the
 * HTTP factory remains responsible for the correct deserialization process.
 * </p>
 *
 * <p><strong>Usage:</strong></p>
 * <p>Users should call {@link #feed(Object)} or {@link #feed(InputStream)} iteratively to provide incoming data.
 * The request will be considered ready only when the method returns {@code true}. The factory will handle all of the
 * validation logic, such as verifying that the provided data meets HTTP request structure requirements (e.g.,
 * validating the {@code Content-Length} header).</p>
 *
 * <p><strong>Asynchronous Flow:</strong></p>
 * <p>The asynchronous design of {@code FutureRequest} helps to avoid blocking operations while deserializing large
 * or incomplete HTTP requests. Users can feed partial data to the {@code FutureRequest} instance and continue with
 * other operations until the request is fully constructed.</p>
 *
 * @param <F> the type of the object used to feed this {@code FutureRequest}
 *
 * @author Daniel Meinicke (Laivy)
 * @version 1.0-SNAPSHOT
 *
 * @see HttpRequest
 * @see Future
 */
public interface FutureRequest<F> extends Future<HttpRequest> {

    /**
     * Returns the creation timestamp of this {@code FutureRequest}.
     *
     * @return a non-null {@link Instant} representing the creation time of this request.
     */
    @NotNull Instant getCreation();

    /**
     * Returns the timestamp when the request was completed, or {@code null} if it has not yet been completed.
     *
     * @return a nullable {@link Instant} representing the time the request was completed, or {@code null} if
     *         the request is still pending.
     */
    @Nullable Instant getFinish();

    /**
     * Indicates whether this {@code FutureRequest} has been completed.
     *
     * @return {@code true} if the request has been fully constructed and completed, {@code false} otherwise.
     */
    default boolean isCompleted() {
        return getFinish() != null;
    }

    /**
     * Returns an {@link InputStream} containing all the raw bytes that have been fed into this {@code FutureRequest}.
     * <p>This method provides an alternative way to access the request data in a stream format, which may be useful
     * for cases where sequential reading of the data is required. It can be used to process the body of the request
     * in real-time as more bytes are fed into the {@code FutureRequest}.
     *
     * @return an {@link InputStream} representing the raw bytes fed into the request.
     */
    @NotNull InputStream getBytes();

    // Future methods

    /**
     * Returns the {@link HttpVersion} of the current HTTP request.
     * <p>The version of the HTTP request will be determined as soon as the request line is fully parsed. If the
     * version is not yet available, this method will return null.
     *
     * @return the {@link HttpVersion} of the request.
     */
    @NotNull HttpVersion<F> getVersion();

    /**
     * Returns the HTTP method of the request, or {@code null} if the method has not yet been determined.
     * <p>Typically, the method will be available once the request line has been fully fed into the {@code FutureRequest}.</p>
     *
     * @return the HTTP {@link Method}, or {@code null} if the request method is not yet available.
     */
    @Nullable Method getMethod();

    /**
     * Returns the authority component (e.g., the host) of the request, or {@code null} if it has not yet been determined.
     * <p>The authority is typically parsed from the request line once sufficient data has been provided.</p>
     *
     * @return the {@link URIAuthority}, or {@code null} if the authority has not yet been extracted.
     */
    @Nullable URIAuthority getAuthority();

    /**
     * Returns the URI of the request, or {@code null} if it has not yet been determined.
     * <p>The URI will typically be available once enough data has been fed into the {@code FutureRequest} to parse the request line.</p>
     *
     * @return the request {@link URI}, or {@code null} if the URI has not yet been determined.
     */
    @Nullable URI getUri();

    /**
     * Returns the HTTP headers of the request, or {@code null} if they have not yet been fully determined.
     * <p>Headers are available once enough of the request has been fed in to parse the headers section.</p>
     *
     * @return the {@link HttpHeaders}, or {@code null} if the headers have not yet been parsed.
     */
    @Nullable HttpHeaders getHeaders();

    /**
     * Returns the body of the HTTP request, or {@code null} if it has not yet been fully constructed.
     * <p>The body is available only after the full content has been fed into the {@code FutureRequest},
     * typically indicated by the {@code Content-Length} header or a completed chunked transfer.</p>
     *
     * @return the {@link HttpBody}, or {@code null} if the body has not yet been fully provided.
     */
    @Nullable HttpBody getBody();

    // Future

    /**
     * Feeds data into this {@code FutureRequest}.
     * <p>This method processes the provided object, checking if enough data has been supplied to construct the full request.
     * The method returns {@code true} if the request has been fully constructed, or {@code false} if more data is still required.</p>
     *
     * @param object the object representing new data to be fed into this request
     * @return {@code true} if the request is fully constructed, {@code false} otherwise
     *
     * @throws HttpRequestParseException if the request cannot be parsed with the passed bytes
     * @throws IllegalHttpVersionException if the http version used doesn't match with the {@link #getVersion()}
     * @throws HttpBodyParseException if the body cannot be parsed due to any exception or invalid format
     * @throws IOException if an I/O exception occurs
     * @throws IllegalHttpMethodException if the method isn't allowed here
     * @throws HeaderFormatException if the header cannot be parsed
     */
    boolean feed(@NotNull F object) throws HttpRequestParseException, IllegalHttpVersionException, HttpBodyParseException, IOException, IllegalHttpMethodException, HeaderFormatException;

    /**
     * Feeds data from an {@link InputStream} into this {@code FutureRequest}.
     * <p>This method reads from the provided input stream and checks if enough data has been supplied to construct the full request.
     * The method returns {@code true} if the request has been fully constructed, or {@code false} if more data is still required.</p>
     *
     * @param stream the input stream containing new data to be fed into this request
     * @return {@code true} if the request is fully constructed, {@code false} otherwise
     * @throws IOException if an I/O error occurs while reading from the stream
     *
     * @throws HttpRequestParseException if the request cannot be parsed with the passed bytes
     * @throws IllegalHttpVersionException if the http version used doesn't match with the {@link #getVersion()}
     * @throws HttpBodyParseException if the body cannot be parsed due to any exception or invalid format
     * @throws IOException if an I/O exception occurs
     * @throws IllegalHttpMethodException if the method isn't allowed here
     * @throws HeaderFormatException if the header cannot be parsed
     */
    boolean feed(@NotNull InputStream stream) throws HttpRequestParseException, IllegalHttpVersionException, HttpBodyParseException, IOException, IllegalHttpMethodException, HeaderFormatException;

    /**
     * Returns the fully constructed {@link HttpRequest}, or throws a {@link NullPointerException} if the request is not yet available.
     * <p>There are several ways to check if the request is ready:
     * <ol>
     *     <li>The {@link #feed(Object)} or {@link #feed(InputStream)} methods return {@code true}, indicating that the request is complete.</li>
     *     <li>The {@link #getFinish()} method returns a non-null value, indicating that the request has been finalized.</li>
     *     <li>The {@link #isCompleted()} method returns {@code true}, confirming that the request is ready.</li>
     * </ol></p>
     *
     * @return the fully constructed {@link HttpRequest}
     * @throws NullPointerException if the request is not yet fully constructed
     */
    @NotNull HttpRequest getRequest() throws NullPointerException;

}