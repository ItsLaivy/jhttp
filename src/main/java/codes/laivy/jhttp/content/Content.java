package codes.laivy.jhttp.content;

import codes.laivy.jhttp.media.MediaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a piece of content with an associated media type. The content can be of any type,
 * and the media type specifies the format or nature of the data. This interface extends {@link Cloneable},
 * indicating that implementing classes should support cloning.
 *
 * @param <T> the type of the content data
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface Content<T> extends Cloneable {

    // Static initializers

    /**
     * Static factory method to create a new {@link Content} instance with the specified media type and data.
     *
     * @param media the media type associated with the content
     * @param data  the data of the content
     * @param <T>   the type of the content data
     * @return a new {@link Content} instance
     */
    static <T> @NotNull Content<T> create(@NotNull MediaType<T> media, @NotNull T data) {
        return new Content<T>() {

            // Object

            @Override
            public @NotNull MediaType<T> getMediaType() {
                return media;
            }
            @Override
            public @NotNull T getData() {
                return data;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                @NotNull Content<?> that = (Content<?>) o;
                return Objects.equals(getMediaType(), that.getMediaType()) && Objects.equals(getData(), that.getData());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getMediaType(), Objects.hash(getData()));
            }
            @Override
            public @NotNull String toString() {
                return getMediaType().getParser().serialize(this);
            }

        };
    }

    // Object

    /**
     * Returns the media type associated with this content. The media type defines the format or nature
     * of the content, such as "application/json", "text/plain", etc.
     *
     * @return the media type of this content
     */
    @NotNull MediaType<T> getMediaType();

    /**
     * Returns the data of this content. The data is of the type specified by the generic parameter T.
     *
     * @return the data of this content
     */
    @NotNull T getData();

}
