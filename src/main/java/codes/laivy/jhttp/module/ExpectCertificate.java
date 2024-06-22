package codes.laivy.jhttp.module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.text.ParseException;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ExpectCertificate {

    // Static initializers

    static @NotNull ExpectCertificate create(final @NotNull Duration age, final @Nullable URI uri, final boolean enforce) {
        return new ExpectCertificate() {

            // Object

            @Override
            public @NotNull Duration getAge() {
                return age;
            }
            @Override
            public @Nullable URI getUri() {
                return uri;
            }
            @Override
            public boolean isEnforce() {
                return enforce;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull ExpectCertificate that = (ExpectCertificate) object;
                return isEnforce() == that.isEnforce() && Objects.equals(getAge(), that.getAge()) && Objects.equals(getUri(), that.getUri());
            }
            @Override
            public int hashCode() {
                return Objects.hash(age, uri, enforce);
            }

            @Override
            public @NotNull String toString() {
                return Parser.serialize(this);
            }

        };
    }

    // Getters

    @NotNull Duration getAge();
    @Nullable URI getUri();
    boolean isEnforce();

    // Classes

    final class Parser {
        private Parser() {
            throw new UnsupportedOperationException();
        }

        // Serializers

        public static @NotNull String serialize(@NotNull ExpectCertificate certificate) {
            @NotNull StringBuilder builder = new StringBuilder("max-age=").append(certificate.getAge().getSeconds());

            if (certificate.getUri() != null) {
                builder.append(", report-uri=\"").append(certificate.getUri()).append("\"");
            } if (certificate.isEnforce()) {
                builder.append(", enforce");
            }

            return builder.toString();
        }
        public static @NotNull ExpectCertificate deserialize(@NotNull String string) throws ParseException {
            if (validate(string)) {
                @NotNull Matcher reportUri = Pattern.compile("(?<key>report-uri=\"(?<value>[^\"]*)\")").matcher(string);
                string = string.replaceAll("(report-uri=\"[^\"]*\")", "");

                @NotNull Duration age = Duration.ofSeconds(Integer.parseInt(Pattern.compile("(max-age=(?<age>\\d+))").matcher(string).group("age")));
                @Nullable URI uri = reportUri.group("value") != null ? URI.create(reportUri.group("value")) : null;
                boolean enforce = string.contains("enforce");

                return create(age, uri, enforce);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid expect certificate", 0);
            }
        }

        public static boolean validate(@NotNull String string) {
            // todo: expect certificate validation
            return true;
        }

    }

}
