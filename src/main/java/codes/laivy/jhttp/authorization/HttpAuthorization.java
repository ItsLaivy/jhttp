package codes.laivy.jhttp.authorization;

import codes.laivy.jhttp.client.HttpClient;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.headers.Headers;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * The authorization class is used to allow only users who provide some degree of authentication to use the proxy
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface HttpAuthorization {

    /**
     * Creates a default implementation of authorization based on the authorization header.
     * You can put your own custom header key, the request must contain this header, and the predicate will validate it.
     *
     * @param key the header key to be used for authorization
     * @param predicate the predicate used to validate the credentials
     * @return an instance of HttpAuthorization
     */
    static @NotNull HttpAuthorization create(final @NotNull HeaderKey<Credentials> key, @NotNull Predicate<Credentials> predicate) {
        return (socket, request) -> {
            // Bad Request (400)
            @NotNull HttpResponse bad = HttpResponse.create(request.getVersion(), HttpStatus.BAD_REQUEST, null);
            // Unauthorized (401)
            @NotNull HttpResponse unauthorized = HttpResponse.create(request.getVersion(), HttpStatus.UNAUTHORIZED, null);

            try {
                // Authorization
                @Nullable Header<Credentials> header = request.getHeaders().first(key).orElse(null);
                if (header == null) return unauthorized;

                @NotNull Credentials credentials = header.getValue();
                return predicate.test(credentials) ? null : unauthorized;
            } catch (@NotNull Throwable ignore) {
                return bad;
            } finally {
                request.getHeaders().remove(key);
            }
        };
    }

    /**
     * Initiates the validation of an authorization. If the validation fails, it returns an {@link HttpResponse} with the error.
     * If the validation is successful, it returns null.
     *
     * <p>This method performs the following steps:
     * <ul>
     *     <li>Extracts necessary authorization data from the {@code HttpRequest}.</li>
     *     <li>Communicates with the given {@code HttpClient} to validate the authorization.</li>
     *     <li>Analyzes the response from the client to determine the validation result.</li>
     *     <li>Returns an appropriate {@code HttpResponse} if validation fails, otherwise returns null.</li>
     * </ul>
     *
     * <p>Example usage:
     * <pre>{@code
     * HttpClient client = ...;
     * HttpRequest request = ...;
     *
     * HttpResponse response = validate(client, request);
     * if (response != null) {
     *     // Handle validation error
     * } else {
     *     // Proceed with authorization granted
     * }
     * }</pre>
     *
     * @param client The HTTP client used to perform the validation request. Must not be null.
     * @param request The HTTP request containing the authorization data to be validated. Must not be null.
     * @return An {@code HttpResponse} containing the error details if validation fails, or null if validation is successful.
     * @throws NullPointerException if {@code client} or {@code request} is null.
     * @throws IOException if an I/O error occurs when sending or receiving.
     * @throws InterruptedException if the operation is interrupted.
     *
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    // todo: improve this
    @ApiStatus.ScheduledForRemoval
    @Deprecated
    @Nullable HttpResponse validate(@NotNull HttpClient client, @NotNull HttpRequest request) throws IOException, InterruptedException;

}
