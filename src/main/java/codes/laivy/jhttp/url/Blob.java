package codes.laivy.jhttp.url;

import codes.laivy.jhttp.url.csp.ContentSecurityPolicy;
import codes.laivy.jhttp.url.domain.Domain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Blob implements ContentSecurityPolicy.Source {

    // Static initializers

    public static final @NotNull Pattern BLOB_URL_PATTERN = Pattern.compile("^blob:((?:https?://)?(?:[\\w.-]+\\.)?[^/]+)/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$");

    public static boolean validate(@NotNull String string) {
        return BLOB_URL_PATTERN.matcher(string).matches();
    }
    public static @NotNull Blob parse(@NotNull String string) throws ParseException {
        @NotNull Matcher matcher = BLOB_URL_PATTERN.matcher(string);

        if (validate(string)) {
            @NotNull Domain domain = Domain.parse(matcher.group(0));
            @NotNull UUID uuid = UUID.fromString(matcher.group(1));

            return new Blob(domain, uuid);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid blob url", 0);
        }
    }

    public static @NotNull Blob create(@NotNull Domain domain, @NotNull UUID uuid) {
        return new Blob(domain, uuid);
    }

    // Object

    private final @NotNull Domain domain;
    private final @NotNull UUID uuid;

    private Blob(@NotNull Domain domain, @NotNull UUID uuid) {
        this.domain = domain;
        this.uuid = uuid;
    }

    // Getters

    public @NotNull Domain getDomain() {
        return domain;
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
        return Objects.equals(domain, blob.domain) && Objects.equals(uuid, blob.uuid);
    }
    @Override
    public int hashCode() {
        return Objects.hash(domain, uuid);
    }

    @Override
    public @NotNull String toString() {
        return "blob:" + getDomain() + (getDomain().toString().endsWith("/") ? "" : "/") + getUniqueId();
    }

}
