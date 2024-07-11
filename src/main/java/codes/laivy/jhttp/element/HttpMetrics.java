package codes.laivy.jhttp.element;

import codes.laivy.jhttp.network.BitMeasure;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Interface representing metrics related to HTTP requests and responses.
 * This interface provides methods to retrieve various metrics such as byte counts,
 * element counts, and average processing times for HTTP requests and responses.
 * The metrics can be filtered based on targets (requests, responses, or both) and
 * HTTP status codes.
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface HttpMetrics {

    /**
     * Represents the last interaction instant.
     * An interaction can be a received request or a delivered response.
     * Anything that means a single interaction to avoid keep-alive timeout.
     * <p>
     * This value is never null, the first interaction is the time when the client connected.
     *
     * @return the instant period of the last interaction
     */
    @NotNull Instant getLastInteraction();

    /**
     * Retrieve the last http version from the client.
     * It should be null if the client didn't send any valid request yet.
     *
     * @return the last http version used by the client
     */
    @Nullable HttpVersion getVersion();

    /**
     * Retrieves the total number of bytes processed for a specific target.
     *
     * @param target the target for which the byte count is to be retrieved;
     *               must not be null.
     * @return the number of bytes processed for the specified target;
     *         never null.
     */
    @NotNull BitMeasure getBytes(@NotNull Target target);

    /**
     * Returns the number of HTTP elements (requests or responses) that have been
     * received or sent using the specified target.
     *
     * @param target the target for which the element count is to be retrieved;
     *               must not be null. The target can be REQUEST, RESPONSE, or BOTH.
     * @return the count of HTTP elements for the specified target.
     */
    long getElements(@NotNull Target target);

    /**
     * Returns the number of HTTP responses that have been sent with the specified
     * status code using the given target.
     *
     * @param status the HTTP status code for filtering the responses;
     *               must not be null.
     * @return the count of HTTP responses with the specified status code
     *         for the given target.
     */
    long getResponses(@NotNull HttpStatus status);

    /**
     * Returns the number of HTTP responses that have been sent
     *
     * @return the map of HTTP responses with the specified status code and count.
     */
    @NotNull Map<@NotNull HttpStatus, @NotNull Long> getResponses();

    /**
     * Returns the average processing time for a specific target.
     * If no elements have been processed, a duration of zero will be returned.
     *
     * @param target the target for which the average processing time is to be retrieved;
     *               must not be null.
     * @return the average processing time for the specified target;
     *         can be null if there's no available data to read the average time.
     */
    @Nullable Duration getAverageTime(@NotNull Target target);

    // Implementations

    /**
     * Returns the count of successful HTTP responses (status codes 200-299) for
     * the specified target.
     *
     * @param target the target for which the successful responses count is to be retrieved;
     *               must not be null.
     * @return the count of successful HTTP responses for the specified target.
     */
    default long getSuccessfulResponses(@NotNull Target target) {
        long elements = 0;

        for (int status = 200; status < 300; status++) {
            elements += getResponses(HttpStatus.getByCode(status));
        }

        return elements;
    }

    /**
     * Returns the count of error HTTP responses (client errors 400-499 and server
     * errors 500-599) for the specified target.
     *
     * @param target the target for which the error responses count is to be retrieved;
     *               must not be null.
     * @return the count of error HTTP responses for the specified target.
     */
    default long getErrorResponses(@NotNull Target target) {
        return getClientErrorResponses() + getServerErrorResponses(target);
    }

    /**
     * Returns the count of client error HTTP responses (status codes 400-499)
     * for the specified target.
     *
     * @return the count of client error HTTP responses for the specified target.
     */
    default long getClientErrorResponses() {
        long elements = 0;

        for (int status = 400; status < 500; status++) {
            elements += getResponses(HttpStatus.getByCode(status));
        }

        return elements;
    }

    /**
     * Returns the count of server error HTTP responses (status codes 500-599)
     * for the specified target.
     *
     * @param target the target for which the server error responses count is to be retrieved;
     *               must not be null.
     * @return the count of server error HTTP responses for the specified target.
     */
    default long getServerErrorResponses(@NotNull Target target) {
        long elements = 0;

        for (int status = 500; status < 600; status++) {
            elements += getResponses(HttpStatus.getByCode(status));
        }

        return elements;
    }
}