package codes.laivy.jhttp.element;

import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaderKey;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.module.connection.Connection;
import codes.laivy.jhttp.module.content.ContentDisposition;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

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
     * @return the {@link HttpHeaders} containing the headers of this element.
     */
    @NotNull
    HttpHeaders getHeaders();

    /**
     * Retrieves the body of this HTTP element. The body can be {@code null} if there is no body or if it is empty.
     *
     * @return the {@link HttpBody} representing the message body of this element, or {@code null} if none.
     */
    @Nullable HttpBody getBody();

    // Getters

    /**
     * Retrieves the connection specified in the headers, if present.
     *
     * @return the connection specified in the headers, or {@code null} if not found
     */
    default @Nullable Connection getConnection() {
        return getHeaders().first(HttpHeaderKey.CONNECTION).map(HttpHeader::getValue).orElse(null);
    }

    default @Nullable OffsetDateTime getDate() {
        return getHeaders().first(HttpHeaderKey.DATE).map(HttpHeader::getValue).orElse(null);
    }

    /**
     * Retrieves the content disposition specified in the headers, if present.
     *
     * @return the content disposition, or {@code null} if not found
     */
    default @Nullable ContentDisposition getDisposition() {
        return getHeaders().first(HttpHeaderKey.CONTENT_DISPOSITION).map(HttpHeader::getValue).orElse(null);
    }

    /**
     * Checks if the request involves uploading a file.
     *
     * @return {@code true} if the request is an upload, {@code false} otherwise
     */
    default boolean isUpload() {
        @Nullable ContentDisposition disposition = getDisposition();
        return disposition != null && disposition.getType() == ContentDisposition.Type.ATTACHMENT;
    }

}