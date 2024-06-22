package codes.laivy.jhttp.url;

import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import codes.laivy.jhttp.url.Host.Name;
import codes.laivy.jhttp.url.domain.Domain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.text.ParseException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Blob implements ContentSecurityPolicy.Source {

    // Static initializers

    public static final @NotNull Pattern BLOB_URL_PATTERN = Pattern.compile("^blob:((?:https?://)?(?<domain>(?:[\\w.-]+\\.)?[^/]+))(?<path>/.+)/(?<uuid>[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$");

    public static boolean validate(@NotNull String string) {
        return BLOB_URL_PATTERN.matcher(string).matches();
    }
    public static @NotNull Blob parse(@NotNull String string) throws ParseException {
        @NotNull Matcher matcher = BLOB_URL_PATTERN.matcher(string);

        if (validate(string)) {
            @NotNull Domain<Name> domain;
            @NotNull URI path = matcher.group("path") != null ? URI.create(matcher.group("path")) : URI.create("");
            @NotNull UUID uuid = UUID.fromString(matcher.group("uuid"));

            try {
                //noinspection unchecked
                domain = (Domain<Name>) Domain.parse(matcher.group("domain"));
            } catch (@NotNull ClassCastException ignore) {
                throw new ParseException("invalid host name '" + matcher.group("domain") + "'", matcher.start("domain"));
            }

            return new Blob(domain, path, uuid);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid blob url", 0);
        }
    }

    public static @NotNull Blob create(@NotNull Domain<Name> domain, @NotNull URI path, @NotNull UUID uuid) {
        return new Blob(domain, path, uuid);
    }

    // Object

    private final @NotNull Domain<Name> domain;
    private final @NotNull URI path;

    private final @NotNull UUID uuid;

    private Blob(@NotNull Domain<Name> domain, @NotNull URI path, @NotNull UUID uuid) {
        this.domain = domain;
        this.path = path;
        this.uuid = uuid;
    }

    // Getters

    public @NotNull Domain<Name> getDomain() {
        return domain;
    }

    public @NotNull URI getPath() {
        return path;
    }

    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    // Implementations

    public @NotNull Scheme getType() {
        return Scheme.BLOB;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Blob blob = (Blob) object;
        return Objects.equals(domain, blob.domain) && Objects.equals(path, blob.path) && Objects.equals(uuid, blob.uuid);
    }
    @Override
    public int hashCode() {
        return Objects.hash(domain, path, uuid);
    }

    @Override
    public @NotNull String toString() {
        // Domain
        @NotNull String domain = getDomain().toString();
        if (!domain.endsWith("/")) domain += "/";
        domain += getPath();

        if (!domain.endsWith("/")) {
            domain += "/";
        }

        // Generate blob
        return "blob:" + domain + getUniqueId();
    }

}
