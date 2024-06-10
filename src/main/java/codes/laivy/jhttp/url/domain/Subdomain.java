package codes.laivy.jhttp.url.domain;

import codes.laivy.jhttp.exception.WildcardValueException;
import codes.laivy.jhttp.utilities.header.Wildcard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Subdomain implements CharSequence, Wildcard<String> {

    // Static initializers

    public static final @NotNull Pattern PARSE_PATTERN = Pattern.compile("^(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)*[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]$");

    public static @NotNull Subdomain wildcard() {
        return new Subdomain();
    }
    public static @NotNull Subdomain create(@NotNull String string) {
        return new Subdomain(string);
    }

    // Object

    private final @Nullable String content;

    private Subdomain() {
        this.content = null;
    }
    private Subdomain(@NotNull String content) {
        this.content = content;

        if (!PARSE_PATTERN.matcher(content).matches()) {
            throw new IllegalArgumentException("the subdomain name '" + content + "' isn't valid");
        }
    }

    // Implementations

    @Override
    public int length() {
        return toString().length();
    }
    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }
    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return toString().substring(start, end);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Subdomain subdomain = (Subdomain) object;
        return Objects.equals(content, subdomain.content);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(content);
    }

    @Override
    public @NotNull String toString() {
        return content == null ? "*" : content;
    }

    // Wildcard

    @Override
    public boolean isWildcard() {
        return content != null;
    }

    @Override
    public @UnknownNullability String getValue() throws WildcardValueException {
        if (content == null) {
            throw new WildcardValueException("this subdomain is a wildcard!");
        }
        return content;
    }

}
