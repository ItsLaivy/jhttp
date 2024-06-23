package codes.laivy.jhttp.element;

import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codes.laivy.jhttp.headers.Headers.MutableHeaders;

public interface HttpElement {

    /**
     * Retrieves the version of this HTTP element
     * @return the version of this element
     */
    @NotNull HttpVersion getVersion();

    /**
     * Retrieves the headers of this element.
     * @return The headers of the element
     */
    @NotNull MutableHeaders getHeaders();

    /**
     * Retrieves the body of the element. It can be null if there is nobody, or it's empty.
     * @return The message body of the element
     */
    @Nullable HttpBody getBody();

}
