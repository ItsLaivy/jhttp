package codes.laivy.jhttp.content;

import codes.laivy.jhttp.utilities.URIAuthority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Objects;

public final class ContentLocation {

    // Static initializers

    public static boolean isContentLocation(@NotNull String string) {
        try {
            parse(string);
            return true;
        } catch (UnknownHostException | ParseException | URISyntaxException e) {
            return false;
        }
    }
    public static @NotNull ContentLocation parse(@NotNull String string) throws ParseException, UnknownHostException, URISyntaxException {
        try {
            @Nullable URIAuthority authority = URIAuthority.isUriAuthority(string) ? URIAuthority.parse(string) : null;
            @NotNull URI path = new URI(string);

            return new ContentLocation(authority, path);
        } catch (@NotNull URISyntaxException syntax) {
            throw new ParseException("cannot parse '" + string + "' into a valid location uri", 0);
        }
    }
    public static @NotNull ContentLocation create(@NotNull URIAuthority authority, @NotNull URI path) {
        return new ContentLocation(authority, path);
    }

    // Object

    private final @Nullable URIAuthority authority;
    private final @NotNull URI uri;

    private ContentLocation(@Nullable URIAuthority authority, @NotNull URI uri) {
        this.authority = authority;
        this.uri = uri;
    }

    // Getters

    public @Nullable URIAuthority getAuthority() {
        return authority;
    }
    public @NotNull URI getURI() {
        return uri;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @NotNull ContentLocation contentLocation = (ContentLocation) o;
        return Objects.equals(authority, contentLocation.authority) && Objects.equals(uri, contentLocation.uri);
    }
    @Override
    public int hashCode() {
        return Objects.hash(authority, uri);
    }

    @Override
    public @NotNull String toString() {
        if (getAuthority() != null) {
            return getAuthority() + "/" + getURI().getPath();
        } else {
            return getURI().toString();
        }
    }

}
