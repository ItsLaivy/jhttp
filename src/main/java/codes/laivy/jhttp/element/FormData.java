package codes.laivy.jhttp.element;

import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaderKey;
import codes.laivy.jhttp.module.content.ContentDisposition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a form data entity that can be used in HTTP requests. This interface provides methods to retrieve
 * the key, body, headers, and content disposition associated with a form data item.
 * <p>
 * Form data items can be used in different content types such as "application/x-www-form-urlencoded" and "multipart/form-data".
 * The availability of certain properties (like headers and content disposition) depends on the content type.
 *
 * @author Daniel Richard (Laivy)
 * @version 1.0-SNAPSHOT
 */
public interface FormData {

    // Static initializers

    static @NotNull FormData create(@NotNull String key, @Nullable HttpBody body, @NotNull HttpHeader<?> @NotNull ... keys) {
        return new FormData() {

            // Object

            @Override
            public @NotNull String getName() {
                return key;
            }
            @Override
            public @Nullable HttpBody getBody() {
                return body;
            }

            @Override
            public @Nullable String getValue() {
                @Nullable HttpBody body = getBody();

                if (body != null) try {
                    @NotNull StringBuilder builder = new StringBuilder();

                    try (@NotNull InputStreamReader reader = new InputStreamReader(body.getInputStream(), StandardCharsets.UTF_8)) {
                        while (reader.ready()) builder.append((char) reader.read());
                    }

                    return builder.toString();
                } catch (@NotNull IOException e) {
                    throw new RuntimeException("cannot read form data body as a string", e);
                } else {
                    return null;
                }
            }

            @Override
            public @NotNull HttpHeader<?> @NotNull [] getHeaders() {
                return keys;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull FormData that = (FormData) object;
                return Objects.equals(getName(), that.getName()) && Objects.equals(getBody(), that.getBody()) && Arrays.equals(getHeaders(), that.getHeaders());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getName(), getBody(), Arrays.hashCode(getHeaders()));
            }

        };
    }

    // Object

    /**
     * Retrieves the name associated with this form data item.
     * The name is a non-null value that identifies the form field.
     *
     * @return the name associated with this form data item, never null.
     */
    @NotNull String getName();

    /**
     * Retrieves the body of the form data item.
     * The body can sometimes be null because not all keys have associated values
     * in "application/x-www-form-urlencoded" content type.
     *
     * @return the body associated with this form data item, or null if no body is present.
     */
    @Nullable HttpBody getBody();

    /**
     * Retrieves the value (body) as a string.
     * This method is just a simple way to retrieve the form data value, you
     * must use {@link #getBody()} if you want more details.
     *
     * @return the body associated with this form data item as a string, or null if no value is present.
     */
    @Nullable String getValue();

    /**
     * Retrieves the headers associated with this form data item.
     * Headers are only available in "multipart/form-data" content type,
     * as form data items in "application/x-www-form-urlencoded" do not include headers.
     *
     * @return an array of headers associated with this form data item, never null but can be empty.
     */
    @NotNull HttpHeader<?> @NotNull [] getHeaders();

    /**
     * Retrieves the content disposition of the form data item.
     * The content disposition is only available in "multipart/form-data" content type.
     * For other content types, this will return null.
     * <p>
     * This method provides a default implementation that extracts the content disposition
     * from the headers if it is present.
     *
     * @return the content disposition of the form data item, or null if not available.
     */
    default @UnknownNullability ContentDisposition getDisposition() {
        return (ContentDisposition) Arrays.stream(getHeaders())
                .filter(header -> header.getKey().equals(HttpHeaderKey.CONTENT_DISPOSITION))
                .findFirst()
                .map(HttpHeader::getValue)
                .orElse(null);
    }
}