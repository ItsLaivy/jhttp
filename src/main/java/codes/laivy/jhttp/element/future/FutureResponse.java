package codes.laivy.jhttp.element.future;

import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.exception.parser.IllegalHttpMethodException;
import codes.laivy.jhttp.exception.parser.IllegalHttpVersionException;
import codes.laivy.jhttp.exception.parser.element.HttpBodyParseException;
import codes.laivy.jhttp.exception.parser.element.HttpRequestParseException;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.concurrent.Future;

/**
 * A {@code FutureResponse} represents a future HTTP response that has not yet been completed.
 * <p>
 * Users who create an instance of a {@code FutureResponse} must feed this class with new bytes using
 * the {@link #feed(Object)} or {@link #feed(InputStream)} methods until those methods return {@code true},
 * indicating that the response has been fully constructed from the provided data.
 * </p>
 * <p>
 * The HTTP factory that generates this class must carefully handle the feeding process, considering several factors.
 * For example, if the body size provided in the {@code Content-Length} header does not match the current size of the
 * body supplied through the {@code feed} method, the {@code FutureResponse} must wait until more bytes are provided,
 * among other potential conditions.
 * </p>
 * <p>
 * This class was designed to maintain asynchronicity in the deserialization of HTTP responses, allowing users to implement
 * their own method for asynchronous deserialization while feeding this class with new bytes as they arrive. However, the
 * HTTP factory remains responsible for the correct deserialization process.
 * </p>
 *
 * <p><strong>Usage:</strong></p>
 * <p>Users should call {@link #feed(Object)} or {@link #feed(InputStream)} iteratively to provide incoming data.
 * The response will be considered ready only when the method returns {@code true}. The factory will handle all of the
 * validation logic, such as verifying that the provided data meets HTTP response structure requirements (e.g.,
 * validating the {@code Content-Length} header).</p>
 *
 * <p><strong>Asynchronous Flow:</strong></p>
 * <p>The asynchronous design of {@code FutureResponse} helps to avoid blocking operations while deserializing large
 * or incomplete HTTP response. Users can feed partial data to the {@code FutureResponse} instance and continue with
 * other operations until the response is fully constructed.</p>
 *
 * @param <F> the type of the object used to feed this {@code FutureResponse}
 *
 * @author Daniel Meinicke (Laivy)
 * @version 1.0-SNAPSHOT
 *
 * @see HttpResponse
 * @see Future
 */
public interface FutureResponse<F> extends Future<HttpResponse> {

    /**
     * Returns the creation timestamp of this {@code FutureResponse}.
     *
     * @return a non-null {@link Instant} representing the creation time of this response.
     */
    @NotNull Instant getCreation();

    /**
     * Returns the timestamp when the response was completed, or {@code null} if it has not yet been completed.
     *
     * @return a nullable {@link Instant} representing the time the response was completed, or {@code null} if
     *         the response is still pending.
     */
    @Nullable Instant getFinish();

    /**
     * Indicates whether this {@code FutureResponse} has been completed.
     *
     * @return {@code true} if the response has been fully constructed and completed, {@code false} otherwise.
     */
    default boolean isCompleted() {
        return getFinish() != null;
    }

    /**
     * Returns an {@link InputStream} containing all the raw bytes that have been fed into this {@code FutureResponse}.
     * <p>This method provides an alternative way to access the response data in a stream format, which may be useful
     * for cases where sequential reading of the data is required. It can be used to process the body of the response
     * in real-time as more bytes are fed into the {@code FutureResponse}.
     *
     * @return an {@link InputStream} representing the raw bytes fed into the response.
     */
    @NotNull InputStream getBytes();

    // Future methods

    /**
     * Returns the {@link HttpVersion} of the current HTTP response.
     * <p>The version of the HTTP response will be determined as soon as the response line is fully parsed. If the
     * version is not yet available, this method will return null.
     *
     * @return the {@link HttpVersion} of the response.
     */
    @NotNull HttpVersion<F> getVersion();

    /**
     * Returns the HTTP status code of the response, or {@code null} if the status has not yet been determined.
     * <p>The status code represents the outcome of the HTTP request, such as {@code 200 OK} or {@code 404 Not Found}.
     *
     * @return the {@link HttpStatus} of the response, or {@code null} if it has not been fully constructed
     */
    @Nullable HttpStatus getStatus();

    /**
     * Returns the HTTP headers of the response, or {@code null} if they have not yet been fully determined.
     * <p>Headers are available once enough of the response has been fed in to parse the headers section.</p>
     *
     * @return the {@link HttpHeaders}, or {@code null} if the headers have not yet been parsed.
     */
    @Nullable HttpHeaders getHeaders();

    /**
     * Returns the body of the HTTP response, or {@code null} if it has not yet been fully constructed.
     * <p>The body is available only after the full content has been fed into the {@code FutureResponse},
     * typically indicated by the {@code Content-Length} header or a completed chunked transfer.</p>
     *
     * @return the {@link HttpBody}, or {@code null} if the body has not yet been fully provided.
     */
    @Nullable HttpBody getBody();

    // Future

    /**
     * Feeds data into this {@code FutureResponse}.
     * <p>This method processes the provided object, checking if enough data has been supplied to construct the full response.
     * The method returns {@code true} if the response has been fully constructed, or {@code false} if more data is still required.</p>
     *
     * @param object the object representing new data to be fed into this response
     * @return {@code true} if the response is fully constructed, {@code false} otherwise
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
     * Feeds data from an {@link InputStream} into this {@code FutureResponse}.
     * <p>This method reads from the provided input stream and checks if enough data has been supplied to construct the full response.
     * The method returns {@code true} if the response has been fully constructed, or {@code false} if more data is still required.</p>
     *
     * @param stream the input stream containing new data to be fed into this response
     * @return {@code true} if the response is fully constructed, {@code false} otherwise
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
     * Returns the fully constructed {@link HttpResponse}, or throws a {@link NullPointerException} if the response is not yet available.
     * <p>There are several ways to check if the response is ready:
     * <ol>
     *     <li>The {@link #feed(Object)} or {@link #feed(InputStream)} methods return {@code true}, indicating that the response is complete.</li>
     *     <li>The {@link #getFinish()} method returns a non-null value, indicating that the response has been finalized.</li>
     *     <li>The {@link #isCompleted()} method returns {@code true}, confirming that the response is ready.</li>
     * </ol></p>
     *
     * @return the fully constructed {@link HttpResponse}
     * @throws NullPointerException if the response is not yet fully constructed
     */
    @NotNull HttpResponse getResponse() throws NullPointerException;

}