package codes.laivy.jhttp.utilities;

import codes.laivy.jhttp.connection.HttpClient;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.headers.Headers;
import codes.laivy.jhttp.request.HttpRequest;
import codes.laivy.jhttp.response.HttpResponse;
import codes.laivy.jhttp.utilities.Credentials.Basic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The authorization class is used to allow only users who provide some degree of authentication to use the proxy
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface HttpAuthorization {

    /**
     * Creates an authorization object that uses the bearer token scheme
     * <p style="color:red">Note: After the first successfully validation, it removes the authorization header. It means if you try to validate again, it will return false.</p>
     *
     * @param predicate a function that checks if the token is valid
     * @return an authentication object that implements the bearer token logic
     * @see <a href="https://apidog.com/articles/what-is-bearer-token/">Bearer Authorization</a>
     *
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    // todo: same method without header key parameter
    static @NotNull HttpAuthorization bearer(final @NotNull HeaderKey<?> key, @NotNull Predicate<String> predicate) {
        return (socket, request) -> {
            // Bad Request (400)
            @NotNull HttpResponse bad = request.getVersion().getFactory().getResponse().build(HttpStatus.BAD_REQUEST, Headers.createMutable(), null);
            // Unauthorized (401)
            @NotNull HttpResponse unauthorized = request.getVersion().getFactory().getResponse().build(HttpStatus.UNAUTHORIZED, Headers.createMutable(), null);
            unauthorized.getHeaders().add(Header.create(HeaderKey.WWW_AUTHENTICATE, "Bearer"));

            try {
                // Authorization
                @Nullable Header header = request.getHeaders().first(key).orElse(null);
                if (header == null) return unauthorized;

                @NotNull String[] auth = header.getValue().split(" ");

                if (auth.length < 2) {
                    return unauthorized;
                } else if (!auth[0].equalsIgnoreCase("Bearer")) {
                    return unauthorized;
                }

                int row = 0;
                @NotNull StringBuilder merged = new StringBuilder();
                for (@NotNull String part : Arrays.stream(auth).skip(1).toArray(String[]::new)) {
                    if (row > 0) merged.append(" ");
                    merged.append(part);
                    row++;
                }

                return predicate.test(merged.toString()) ? null : unauthorized;
            } catch (@NotNull Throwable ignore) {
                return bad;
            } finally {
                request.getHeaders().remove(key);
            }
        };
    }

    /**
     * Creates an authorization object that uses the basic token scheme
     * <p style="color:red">Note: After the first successfully validation, it removes the authorization header. It means if you try to validate again, it will return false.</p>
     *
     * @param predicate a function that checks if the token is valid
     * @return an authentication object that implements the bearer token logic
     * @see <a href="https://en.wikipedia.org/wiki/Basic_access_authentication">Basic Authorization</a>
     *
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    // todo: same method without header key parameter
    static @NotNull HttpAuthorization basic(@NotNull HeaderKey<?> key, @NotNull Predicate<Basic> predicate) {
        return (socket, request) -> {
            // Bad Request (400)
            @NotNull HttpResponse bad = request.getVersion().getFactory().getResponse().build(HttpStatus.BAD_REQUEST, Headers.createMutable(), null);
            // Unauthorized (401)
            @NotNull HttpResponse unauthorized = request.getVersion().getFactory().getResponse().build(HttpStatus.UNAUTHORIZED, Headers.createMutable(), null);
            unauthorized.getHeaders().add(Header.create(HeaderKey.WWW_AUTHENTICATE, "Basic"));

            try {
                // Authorization
                @Nullable Header header = request.getHeaders().first(key).orElse(null);
                if (header == null) return unauthorized;

                @NotNull String[] auth = header.getValue().split(" ");

                if (auth.length < 2) {
                    return unauthorized;
                } else if (!auth[0].equalsIgnoreCase("Basic")) {
                    return unauthorized;
                }

                @NotNull String encoded = Arrays.stream(auth).skip(1).map(string -> string + " ").collect(Collectors.joining());
                @NotNull String[] decoded = new String(Base64.getDecoder().decode(encoded)).split(":");

                return predicate.test(new Basic(decoded[0], decoded[1].toCharArray())) ? null : unauthorized;
            } catch (@NotNull Throwable ignore) {
                return bad;
            } finally {
                request.getHeaders().remove(key);
            }
        };
    }

    /**
     * Initiates the validation of an authorization. If the validation fails, it returns an HttpResponse with the error.
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
    @Nullable HttpResponse validate(@NotNull HttpClient client, @NotNull HttpRequest request) throws IOException, InterruptedException;

}
