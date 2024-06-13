package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.text.ParseException;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExpectCertificate {

    // Static initializers

    public static final @NotNull Pattern PATTERN = Pattern.compile("(report-uri=\"[^\"]*\")");

    public static boolean validate(@NotNull String string) {
        // todo: expect certificate validation
        return true;
    }
    public static @NotNull ExpectCertificate parse(@NotNull String string) throws ParseException {
        if (validate(string)) {
            @NotNull Matcher reportUri = Pattern.compile("(?<key>report-uri=\"(?<value>[^\"]*)\")").matcher(string);
            string = string.replaceAll("(report-uri=\"[^\"]*\")", "");

            @NotNull Duration age = Duration.ofSeconds(Integer.parseInt(Pattern.compile("(max-age=(?<age>\\d+))").matcher(string).group("age")));
            @Nullable URI uri = reportUri.group("value") != null ? URI.create(reportUri.group("value")) : null;
            boolean enforce = string.contains("enforce");

            return new ExpectCertificate(age, uri, enforce);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid expect certificate", 0);
        }
    }

    public static @NotNull ExpectCertificate create(@NotNull Duration age, @NotNull URI uri, boolean enforce) {
        return new ExpectCertificate(age, uri, enforce);
    }

    // Object

    private final @NotNull Duration age;

    private final @Nullable URI uri;
    private final boolean enforce;

    private ExpectCertificate(@NotNull Duration age, @Nullable URI uri, boolean enforce) {
        this.age = age;
        this.uri = uri;
        this.enforce = enforce;
    }

    // Getters

    public @NotNull Duration getAge() {
        return age;
    }
    public @Nullable URI getUri() {
        return uri;
    }
    public boolean isEnforce() {
        return enforce;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull ExpectCertificate that = (ExpectCertificate) object;
        return enforce == that.enforce && Objects.equals(age, that.age) && Objects.equals(uri, that.uri);
    }
    @Override
    public int hashCode() {
        return Objects.hash(age, uri, enforce);
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder("max-age=").append(getAge().getSeconds());

        if (getUri() != null) {
            builder.append(", report-uri=\"").append(getUri()).append("\"");
        } if (isEnforce()) {
            builder.append(", enforce");
        }

        return builder.toString();
    }

}
