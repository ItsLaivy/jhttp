package codes.laivy.jhttp.url.email;

import codes.laivy.jhttp.url.Host.Name;
import codes.laivy.jhttp.url.domain.Domain;
import codes.laivy.jhttp.url.domain.Subdomain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email implements CharSequence {

    // Static initializers

    private static final @NotNull Pattern EMAIL_REGEX = Pattern.compile("^(?<username>[a-zA-Z0-9._%+-]{1,64})@(?<domain>[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$");

    public static @NotNull Email parse(@NotNull String string) throws IllegalArgumentException {
        string = string.replace("(dot)", ".").replace("(at)", "@");
        @NotNull Matcher matcher = EMAIL_REGEX.matcher(string);

        if (matcher.matches()) {
            @NotNull String username = matcher.group("username");
            @NotNull Domain<Name> domain;

            try {
                //noinspection unchecked
                domain = (Domain<Name>) Domain.parse(matcher.group("domain"));
            } catch (@NotNull ClassCastException | @NotNull ParseException e) {
                throw new IllegalArgumentException("invalid host name '" + matcher.group("domain") + "'", e);
            }

            return create(username, domain);
        } else {
            throw new IllegalArgumentException("cannot parse '" + string + "' as a valid e-mail");
        }
    }

    public static @NotNull Email create(
            final @NotNull String username,
            final @NotNull Domain<Name> domain
    ) throws IllegalArgumentException {
        @NotNull String string = username.replace("(dot)", ".").replace("(at)", "@");
        return new Email(username, domain);
    }

    // Object

    private final @NotNull String username;
    private final @NotNull Domain<Name> domain;

    protected Email(@NotNull String username, @NotNull Domain<Name> domain) {
        if (!username.matches("[a-zA-Z0-9._%+-]{1,64}")) {
            throw new IllegalArgumentException("invalid email username format '" + username + "'");
        }

        this.username = username;
        this.domain = domain;
    }

    // Getters

    public @NotNull String getUsername() {
        return username;
    }
    public @NotNull Domain<Name> getDomain() {
        return domain;
    }

    // Implementations

    @Override
    public final boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Email email = (Email) object;
        return Objects.equals(getUsername(), email.getUsername()) && Objects.equals(getDomain().getName(), email.getDomain().getName()) && Arrays.equals(getDomain().getSubdomains(), email.domain.getSubdomains());
    }
    @Override
    public final int hashCode() {
        return Objects.hash(getUsername(), getDomain().getName(), Arrays.hashCode(getDomain().getSubdomains()));
    }

    @Override
    public final @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder(getUsername()).append("@");

        for (@NotNull Subdomain subdomain : getDomain().getSubdomains()) {
            builder.append(subdomain.getValue()).append(".");
        }

        builder.append(getDomain().getName());
        return builder.toString();
    }

    // CharSequence Implementations

    @Override
    public final int length() {
        return toString().length();
    }
    @Override
    public final char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public final @NotNull CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

}
