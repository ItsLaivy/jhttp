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

public interface Email extends CharSequence {

    // Static initializers

    static @NotNull Email create(
            final @NotNull String username,
            final @NotNull Domain<Name> domain
    ) throws IllegalArgumentException {
        if (!username.matches("[a-zA-Z0-9._%+-]{1,64}")) {
            throw new IllegalArgumentException("invalid email username format '" + username + "'");
        }

        return new Email() {

            @Override
            public @NotNull String getUsername() {
                return username;
            }
            @Override
            public @NotNull Domain<Name> getDomain() {
                return domain;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull Email email = (Email) object;
                return Objects.equals(getUsername(), email.getUsername()) && Objects.equals(getDomain(), email.getDomain());
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
                return Parser.serialize(this);
            }

        };
    }

    // Getters

    @NotNull String getUsername();
    @NotNull Domain<Name> getDomain();

    // Classes

    final class Parser {
        private Parser() {
            throw new UnsupportedOperationException();
        }

        // Serializers

        private static final @NotNull Pattern EMAIL_REGEX = Pattern.compile("^(?<username>[a-zA-Z0-9._%+-]{1,64})@(?<domain>[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$");

        public static @NotNull String serialize(@NotNull Email email) {
            if (!email.getUsername().matches("[a-zA-Z0-9._%+-]{1,64}")) {
                throw new IllegalArgumentException("invalid email username format '" + email.getUsername() + "'");
            }

            @NotNull StringBuilder builder = new StringBuilder(email.getUsername()).append("@");

            for (@NotNull Subdomain subdomain : email.getDomain().getSubdomains()) {
                builder.append(subdomain.getValue()).append(".");
            }

            builder.append(email.getDomain().getName());
            return builder.toString();
        }
        public static @NotNull Email deserialize(@NotNull String string) throws ParseException {
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

                return create(username, domain);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid e-mail", 0);
            }
        }
        public static boolean validate(@NotNull String string) {
            return EMAIL_REGEX.matcher(string).matches();
        }
    }

}
