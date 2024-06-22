package codes.laivy.jhttp.message;

import codes.laivy.jhttp.media.MediaType;
import org.jetbrains.annotations.NotNull;

public interface Content<T> extends Cloneable {

    @NotNull MediaType<T, ?> getMediaType();

    @NotNull T getElement();
    @NotNull String getRaw();

}
