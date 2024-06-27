package codes.laivy.jhttp.element;

import codes.laivy.jhttp.content.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents the body of an HTTP request/response. The raw content is represented as a {@link CharSequence},
 * which reflects the request in its original form, excluding any encodings (such as those specified by the
 * "Content-Encoding" header) or transformations.
 * <p>
 * Note: This interface is designed to handle HTTP bodies and provides methods to retrieve decoded content
 * and transformed content when possible.
 * </p>
 *
 * @see CharSequence
 * @see Content
 * @since 1.0-SNAPSHOT
 */
// todo: refactoration
public interface HttpBody extends CharSequence {

    // Static initializers

    static <T> @NotNull HttpBody create(
            final @NotNull Content<T> content
    ) {
        return create(content, content.getMediaType().getParser().serialize(content));
    }
    static @NotNull HttpBody create(
            final @Nullable Content<?> content,
            final @NotNull String decoded
    ) {
        return create(content, decoded, decoded);
    }
    static @NotNull HttpBody create(
            final @Nullable Content<?> content,
            final @NotNull String decoded,
            final @NotNull String raw
    ) {
        return new HttpBody() {

            // Object

            @Override
            public @Nullable Content<?> getContent() {
                return content;
            }
            @Override
            public @NotNull CharSequence getDecoded() {
                return decoded;
            }

            // CharSequence implementation

            @Override
            public int length() {
                return raw.length();
            }
            @Override
            public char charAt(int index) {
                return raw.charAt(index);
            }
            @Override
            public @NotNull CharSequence subSequence(int start, int end) {
                return raw.subSequence(start, end);
            }

            // Implementation

            @Override
            public boolean equals(@Nullable Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                @NotNull HttpBody that = (HttpBody) o;
                return Objects.equals(getContent(), that.getContent()) && Objects.equals(getDecoded(), that.getDecoded()) && Objects.equals(toString(), that.toString());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getContent(), getDecoded(), raw);
            }
            @Override
            public @NotNull String toString() {
                return raw;
            }

        };
    }

    // Object

    /**
     * Returns the transformed content of this body, if available. The content is only generated in specific situations
     * where it can be created. The request/response must have a "Content-Type" header. The returned content is already
     * decoded, meaning any "Content-Encoding" headers have been applied.
     *
     * @return the transformed content of this body, or null if it cannot be generated
     */
    @Nullable Content<?> getContent();

    /**
     * Returns the content of this body in a decoded form, if the body has any encodings applied.
     *
     * @return the decoded content of this body
     */
    @NotNull
    CharSequence getDecoded();

}
