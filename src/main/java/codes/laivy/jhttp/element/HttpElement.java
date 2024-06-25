package codes.laivy.jhttp.element;

import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codes.laivy.jhttp.headers.Headers.MutableHeaders;

/**
 * The {@code HttpElement} interface represents a basic HTTP element that comprises
 * the version, headers, and body.
 * This interface is intended to be implemented by
 * various HTTP components, such as requests and responses, providing a standard way
 * to access their common properties.
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface HttpElement {

    /**
     * Retrieves the version of this HTTP element.
     *
     * @return the {@link HttpVersion} representing the version of this element.
     */
    @NotNull HttpVersion getVersion();

    /**
     * Retrieves the headers of this HTTP element.
     *
     * @return the {@link MutableHeaders} containing the headers of this element.
     */
    @NotNull MutableHeaders getHeaders();

    /**
     * Retrieves the body of this HTTP element. The body can be {@code null} if there is no body or if it is empty.
     *
     * @return the {@link HttpBody} representing the message body of this element, or {@code null} if none.
     */
    @Nullable HttpBody getBody();

}