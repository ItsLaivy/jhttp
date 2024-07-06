package codes.laivy.jhttp.module;

import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import codes.laivy.jhttp.url.domain.Domain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Location extends ContentSecurityPolicy.Source {

    // Static initializers

    static @NotNull Location create(@Nullable Domain<?> domain, @NotNull URI uri) {
        return new Location() {
            @Override
            public @Nullable Domain<?> getDomain() {
                return domain;
            }
            @Override
            public @NotNull URI getURI() {
                return uri;
            }

            @Override
            public boolean equals(@Nullable Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                @NotNull Location that = (Location) o;
                return Objects.equals(domain, that.getDomain()) && Objects.equals(uri, that.getURI());
            }
            @Override
            public int hashCode() {
                return Objects.hash(domain, uri);
            }

            @Override
            public @NotNull String toString() {
                return Parser.serialize(this);
            }

        };
    }

    // Getters

    @Nullable Domain<?> getDomain();
    @NotNull URI getURI();

    // Classes

    final class Parser {
        private Parser() {
            throw new UnsupportedOperationException("this class cannot be instantiated");
        }

        private static final @NotNull Pattern LOCATION_PATTERN = Pattern.compile("^(?<domain>(https?://)?(localhost|(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,})(?::\\d+)?)?(/.*)??(?<path>/?\\S*)?$");

        public static @NotNull String serialize(@NotNull Location location) {
            if (location.getDomain() != null) {
                @NotNull StringBuilder builder = new StringBuilder(location.getDomain().toString());

                if (!location.getURI().toString().isEmpty() && !location.getURI().toString().startsWith("/")) {
                    builder.append("/");
                }

                builder.append(location.getURI());
                return builder.toString();
            } else {
                return location.getURI().toString();
            }
        }
        public static @NotNull Location deserialize(@NotNull String string) throws ParseException, UnknownHostException, URISyntaxException {
            @NotNull Matcher matcher = LOCATION_PATTERN.matcher(string);

            if (matcher.matches()) {
                @Nullable Domain<?> domain = matcher.group("domain") != null ? Domain.parse(matcher.group("domain")) : null;
                @NotNull URI uri = matcher.group("path") != null ? URI.create(matcher.group("path")) : URI.create("");

                return create(domain, uri);
            } else {
                throw new ParseException("cannot parse '" + string + "' into a valid origin", 0);
            }
        }

        public static boolean validate(@NotNull String string) {
            return LOCATION_PATTERN.matcher(string).matches();
        }

    }

}
