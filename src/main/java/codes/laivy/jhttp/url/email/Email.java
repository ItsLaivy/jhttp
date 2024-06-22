package codes.laivy.jhttp.url.email;

import codes.laivy.jhttp.url.Host.Name;
import codes.laivy.jhttp.url.domain.Domain;
import codes.laivy.jhttp.url.domain.Subdomain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Email implements CharSequence {

    // Static initializers

    private static final @NotNull Pattern EMAIL_REGEX = Pattern.compile("^(?<username>[a-zA-Z0-9._%+-]{1,64})@(?<domain>[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$");

    public static boolean validate(@NotNull String string) {
        return EMAIL_REGEX.matcher(string).matches();
    }
    public static @NotNull Email parse(@NotNull String string) throws ParseException {
        @NotNull Matcher matcher = EMAIL_REGEX.matcher(string);

        if (matcher.matches()) {
            @NotNull String username = matcher.group("username");
            @NotNull Domain<Name> domain;

            try {
                //noinspection unchecked
                domain = (Domain<Name>) Domain.parse(matcher.group("domain"));
            } catch (@NotNull ClassCastException ignore) {
                throw new ParseException("invalid host name '" + matcher.group("domain") + "'", matcher.start("domain"));
            }

            return new Email(username, domain);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid e-mail", 0);
        }
    }

    public static @NotNull Email create(@NotNull String username, @NotNull Domain<Name> domain) throws IllegalArgumentException {
        return new Email(username, domain);
    }

    // Object

    private final @NotNull String username;
    private final @NotNull Domain<Name> domain;

    private Email(@NotNull String username, @NotNull Domain<Name> domain) {
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
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Email email = (Email) object;
        return Objects.equals(username, email.username) && Objects.equals(domain, email.domain);
    }
    @Override
    public int hashCode() {
        return Objects.hash(username, domain);
    }

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
        return toString().subSequence(start, end);
    }
    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder(getUsername()).append("@");

        for (@NotNull Subdomain subdomain : getDomain().getSubdomains()) {
            builder.append(subdomain.getValue()).append(".");
        }

        builder.append(getDomain().getName());
        return builder.toString();
    }

}
