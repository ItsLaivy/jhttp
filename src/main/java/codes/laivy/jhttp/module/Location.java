package codes.laivy.jhttp.module;

import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import codes.laivy.jhttp.url.domain.Domain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Location implements ContentSecurityPolicy.Source {

    // Static initializers

    private static final @NotNull Pattern LOCATION_PATTERN = Pattern.compile("^(?<domain>(https?://)?(localhost|(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,})(?::\\d+)?)?(/.*)??(?<path>/?\\S*)?$");

    public static boolean validate(@NotNull String string) {
        return LOCATION_PATTERN.matcher(string).matches();
    }
    public static @NotNull Location parse(@NotNull String string) {
        @NotNull Matcher matcher = LOCATION_PATTERN.matcher(string);

        if (matcher.matches()) {
            @Nullable Domain<?> domain = matcher.group("domain") != null ? Domain.parse(matcher.group("domain")) : null;
            @NotNull URI uri = matcher.group("path") != null ? URI.create(matcher.group("path")) : URI.create("");

            return create(domain, uri);
        } else {
            throw new IllegalArgumentException("cannot parse '" + string + "' into a valid origin");
        }
    }
    public static @NotNull Location create(@Nullable Domain<?> domain, @NotNull URI uri) {
        return new Location(domain, uri);
    }

    // Object

    private final @Nullable Domain<?> domain;
    private final @NotNull URI uri;

    protected Location(@Nullable Domain<?> domain, @NotNull URI uri) {
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
    public final boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @NotNull Location that = (Location) o;
        return Objects.equals(getDomain(), that.getDomain()) && Objects.equals(getURI(), that.getURI());
    }
    @Override
    public final int hashCode() {
        return Objects.hash(getDomain(), getURI());
    }

    @Override
    public final @NotNull String toString() {
        if (getDomain() != null) {
            @NotNull StringBuilder builder = new StringBuilder(getDomain().toString());

            if (!getURI().toString().isEmpty() && !getURI().toString().startsWith("/")) {
                builder.append("/");
            }

            builder.append(getURI());
            return builder.toString();
        } else {
            return getURI().toString();
        }
    }

}
