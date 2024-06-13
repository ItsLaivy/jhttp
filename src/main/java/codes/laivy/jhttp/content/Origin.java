package codes.laivy.jhttp.content;

import codes.laivy.jhttp.url.domain.Domain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Objects;

public final class Origin {

    // Static initializers

    public static boolean isContentLocation(@NotNull String string) {
        try {
            parse(string);
            return true;
        } catch (UnknownHostException | ParseException | URISyntaxException e) {
            return false;
        }
    }
    public static @NotNull Origin parse(@NotNull String string) throws ParseException, UnknownHostException, URISyntaxException {
        try {
            // todo: content location parser

            return new Origin(authority, path);
        } catch (@NotNull URISyntaxException syntax) {
            throw new ParseException("cannot parse '" + string + "' into a valid location uri", 0);
        }
    }
    public static @NotNull Origin create(@Nullable Domain<?> domain, @NotNull URI path) {
        return new Origin(domain, path);
    }

    // Object

    private final @Nullable Domain<?> domain;
    private final @NotNull URI uri;

    private Origin(@Nullable Domain<?> domain, @NotNull URI uri) {
        this.domain = domain;
        this.uri = uri;
    }

    // Getters

    public @Nullable Domain<?> getDomain() {
        return domain;
    }
    public @NotNull URI getURI() {
        return uri;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @NotNull Origin origin = (Origin) o;
        return Objects.equals(domain, origin.domain) && Objects.equals(uri, origin.uri);
    }
    @Override
    public int hashCode() {
        return Objects.hash(domain, uri);
    }

    @Override
    public @NotNull String toString() {
        if (getDomain() != null) {
            return getDomain() + "/" + getURI().getPath();
        } else {
            return getURI().toString();
        }
    }

}
