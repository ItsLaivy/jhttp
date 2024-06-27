package codes.laivy.jhttp.protocol.factory;

import codes.laivy.jhttp.connection.HttpClient;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.element.response.HttpResponse.Future;
import codes.laivy.jhttp.exception.parser.request.HttpResponseParseException;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class HttpResponseFactory {

    // Static initializers

    // Object

    private final @NotNull HttpVersion version;

    protected HttpResponseFactory(@NotNull HttpVersion version) {
        this.version = version;
    }

    /**
     * Retrieves the http response factory according to the http version
     *
     * @param version The http version
     * @return The http response factory according to the http version parameter
     */
    public static @NotNull HttpResponseFactory getInstance(@NotNull HttpVersion version) {
        return version.getResponseFactory();
    }

    // Getters

    public @NotNull HttpVersion getVersion() {
        return version;
    }

    // Modules

    public abstract @NotNull String serialize(@NotNull HttpResponse response);

    public abstract @NotNull HttpResponse parse(@NotNull String string) throws HttpResponseParseException;

    public abstract @NotNull Future parse(@NotNull HttpClient client, @NotNull String string) throws HttpResponseParseException;

    public abstract boolean validate(@NotNull String string);

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull HttpResponseFactory that = (HttpResponseFactory) object;
        return Objects.equals(version, that.version);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(version);
    }

    @Override
    public @NotNull String toString() {
        return "HttpResponseFactory{" +
                "version=" + version +
                '}';
    }

}
