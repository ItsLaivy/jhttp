package codes.laivy.jhttp.content;

import codes.laivy.jhttp.url.domain.Domain;
import codes.laivy.jhttp.utilities.DateUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Cookie {

    // Static initializers

    public static final @NotNull Pattern COOKIE_KEY_PATTERN = Pattern.compile("^[\\x21\\x23-\\x27\\x2A\\x2B\\x2D\\x2E\\x30-\\x39\\x41-\\x5A\\x5E-\\x7A\\x7C\\x7E]+$");
    public static final @NotNull Pattern COOKIE_VALUE_PATTERN = Pattern.compile("^(?:[^\\x00-\\x1F\\x22\\x2C\\x3B\\x5C\\x7F]+|\"(?:[^\\x00-\\x1F\\x22\\x2C\\x3B\\x5C\\x7F]|\")*\")$");
    public static final @NotNull Pattern COOKIE_PATTERN = Pattern.compile("^([\\x21\\x23-\\x27\\x2A\\x2B\\x2D\\x2E\\x30-\\x39\\x41-\\x5A\\x5E-\\x7A\\x7C\\x7E]+)=([^\\x00-\\x1F\\x22\\x2C\\x3B\\x5C\\x7F]+|\"(?:[^\\x00-\\x1F\\x22\\x2C\\x3B\\x5C\\x7F]|\")*\")$");

    public static boolean validate(@NotNull String string) {
        return COOKIE_PATTERN.matcher(string).matches();
    }
    public static @NotNull Cookie parse(@NotNull String string) throws ParseException {
        if (validate(string)) {
            @NotNull Matcher matcher = COOKIE_PATTERN.matcher(string);
            @NotNull String key = matcher.group(0);
            @NotNull String value = matcher.group(1);

            return new Cookie(key, value);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a cookie", 0);
        }
    }

    public static @NotNull Cookie create(@NotNull String key, @NotNull String value) {
        return new Cookie(key, value);
    }

    // Object

    private final @NotNull String key;
    private final @NotNull String value;

    private Cookie(@NotNull String key, @NotNull String value) {
        this.key = key;
        this.value = value;

        if (!key.matches(COOKIE_KEY_PATTERN.pattern())) {
            throw new IllegalArgumentException("invalid cookie key name '" + key + "'");
        } else if (!value.matches(COOKIE_VALUE_PATTERN.pattern())) {
            throw new IllegalArgumentException("invalid cookie value name '" + value +  "'");
        }
    }

    // Getters

    @Contract(pure = true)
    public final @NotNull String getKey() {
        return key;
    }
    @Contract(pure = true)
    public final @NotNull String getValue() {
        return value;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Cookie cookie = (Cookie) object;
        return Objects.equals(key, cookie.key) && Objects.equals(value, cookie.value);
    }
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public @NotNull String toString() {
        return getKey() + "=" + getValue();
    }

    // Classes

    public static final class Request extends Cookie {

        // Static initializers

        public static final @NotNull Pattern COOKIE_REQUEST_PATTERN = Pattern.compile(
                "^(?<key>[\\x21\\x23-\\x27\\x2A\\x2B\\x2D\\x2E\\x30-\\x39\\x41-\\x5A\\x5E-\\x7A\\x7C\\x7E]+)=" +
                "(?<value>[^\\x00-\\x1F\\x22\\x2C\\x3B\\x5C\\x7F]+|\"(?:[^\\x00-\\x1F\\x22\\x2C\\x3B\\x5C\\x7F]|\")*\")" +
                "(?:;\\s*(Domain=(?<domain>[\\x21\\x23-\\x27\\x2A\\x2B\\x2D\\x2E\\x30-\\x39\\x41-\\x5A\\x5E-\\x7A\\x7C\\x7E]+)|" +
                "Expires=(?<expires>(?:Mon|Tue|Wed|Thu|Fri|Sat|Sun), \\d{2} (?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \\d{4} \\d{2}:\\d{2}:\\d{2} GMT)|" +
                "HttpOnly|" +
                "Max-Age=(?<maxage>\\d+)|" +
                "Partitioned|" +
                "Path=(?<path>[^;]+)|" +
                "SameSite=(?<samesite>Strict|Lax|None)|" +
                "Secure))*$"
        );

        public static boolean validate(@NotNull String string) {
            return COOKIE_REQUEST_PATTERN.matcher(string).matches();
        }
        public static @NotNull Request parse(@NotNull String string) throws ParseException {
            @NotNull Matcher matcher = COOKIE_REQUEST_PATTERN.matcher(string);

            if (matcher.matches()) {
                @NotNull String key = matcher.group("key");
                @NotNull String value = matcher.group("value");

                @Nullable Domain domain = matcher.group("domain") != null ? Domain.parse(matcher.group("domain")) : null;
                @Nullable OffsetDateTime expires = matcher.group("expires") != null ? DateUtils.RFC822.convert(matcher.group("domain")) : null;
                @Nullable Duration maxAge = matcher.group("maxage") != null ? Duration.ofSeconds(Long.parseLong(matcher.group("maxage"))) : null;
                @Nullable Path path = matcher.group("path") != null ? new File(matcher.group("path")).toPath() : null;
                @Nullable SameSite sameSite = matcher.group("samesite") != null ? SameSite.getById(matcher.group("samesite")) : null;

                boolean httpOnly = matcher.group("httponly") != null;
                boolean partitioned = matcher.group("partitioned") != null;
                boolean secure = matcher.group("secure") != null;

                return new Request(key, value, domain, expires, maxAge, path, sameSite, httpOnly, partitioned, secure);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid cookie request", 0);
            }
        }

        public static @NotNull Builder builder(@NotNull String key, @NotNull String value) {
            return new Builder(key, value);
        }
        public static @NotNull Request create(@NotNull String key, @NotNull String value) {
            return new Request(key, value, null, null, null, null, null, false, false, false);
        }

        // Object

        private final @Nullable Domain domain;
        private final @Nullable OffsetDateTime expires;
        private final @Nullable Duration maxAge;
        private final @Nullable Path path;
        private final @Nullable SameSite sameSite;

        private final boolean httpOnly;
        private final boolean partitioned;
        private final boolean secure;

        private Request(@NotNull String key, @NotNull String value, @Nullable Domain domain, @Nullable OffsetDateTime expires, @Nullable Duration maxAge, @Nullable Path path, @Nullable SameSite sameSite, boolean httpOnly, boolean partitioned, boolean secure) {
            super(key, value);
            this.domain = domain;
            this.expires = expires;
            this.maxAge = maxAge;
            this.path = path;
            this.sameSite = sameSite;
            this.httpOnly = httpOnly;
            this.partitioned = partitioned;
            this.secure = secure;
        }

        // Getters

        public @Nullable Domain getDomain() {
            return domain;
        }
        public @Nullable OffsetDateTime getExpires() {
            return expires;
        }
        public @Nullable Duration getMaxAge() {
            return maxAge;
        }
        public @Nullable Path getPath() {
            return path;
        }
        public @Nullable SameSite getSameSite() {
            return sameSite;
        }

        public boolean isHttpOnly() {
            return httpOnly;
        }
        public boolean isPartitioned() {
            return partitioned;
        }
        public boolean isSecure() {
            return secure;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            if (!super.equals(object)) return false;
            @NotNull Request request = (Request) object;
            return httpOnly == request.httpOnly && partitioned == request.partitioned && secure == request.secure && Objects.equals(domain, request.domain) && Objects.equals(expires, request.expires) && Objects.equals(maxAge, request.maxAge) && Objects.equals(path, request.path) && sameSite == request.sameSite;
        }
        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), domain, expires, maxAge, path, sameSite, httpOnly, partitioned, secure);
        }

        @Override
        public @NotNull String toString() {
            @NotNull StringBuilder builder = new StringBuilder(getKey()).append("=").append(getValue()).append("; ");

            if (getDomain() != null) {
                @NotNull String subdomains = Arrays.stream(getDomain().getSubdomains()).map(s -> s + ".").collect(Collectors.joining());
                @NotNull String domain = subdomains + getDomain().getName();

                builder.append("Domain=").append(domain).append("; ");
            } if (getExpires() != null) {
                builder.append("Expires=").append(DateUtils.RFC822.convert(getExpires())).append("; ");
            } if (getMaxAge() != null) {
                builder.append("Max-Age=").append(getMaxAge().getSeconds()).append("; ");
            } if (getPath() != null) {
                builder.append("Path=").append(getPath()).append("; ");
            } if (getSameSite() != null) {
                builder.append("SameSite=").append(getSameSite().getId()).append("; ");
            }

            if (isHttpOnly()) {
                builder.append("HttpOnly").append("; ");
            } if (isPartitioned()) {
                builder.append("Partitioned").append("; ");
            } if (isSecure()) {
                builder.append("Secure").append("; ");
            }

            return builder.substring(0, builder.length() - 2);
        }

        // Classes

        public enum SameSite {

            STRICT("Strict"),
            LAX("Lax"),
            NONE("None"),
            ;

            private final @NotNull String id;

            SameSite(@NotNull String id) {
                this.id = id;
            }

            public @NotNull String getId() {
                return id;
            }
            public static @NotNull SameSite getById(@NotNull String id) {
                @NotNull Optional<SameSite> optional = Arrays.stream(values()).filter(sameSite -> sameSite.getId().equalsIgnoreCase(id)).findFirst();
                return optional.orElseThrow(() -> new NullPointerException("There's no SameSite cookie request enum with id '" + id + "'"));
            }

        }

        public static final class Builder {

            private final @NotNull String key;
            private final @NotNull String value;

            private @Nullable Domain domain;
            private @Nullable OffsetDateTime expires;
            private @Nullable Duration maxAge;
            private @Nullable Path path;
            private @Nullable SameSite sameSite;

            private boolean httpOnly;
            private boolean partitioned;
            private boolean secure;

            private Builder(@NotNull String key, @NotNull String value) {
                this.key = key;
                this.value = value;

                if (!key.matches(COOKIE_KEY_PATTERN.pattern())) {
                    throw new IllegalArgumentException("invalid cookie key name '" + key + "'");
                } else if (!value.matches(COOKIE_VALUE_PATTERN.pattern())) {
                    throw new IllegalArgumentException("invalid cookie value name '" + value +  "'");
                }
            }

            // Getters

            @Contract(pure = true)
            public @NotNull String getKey() {
                return key;
            }
            @Contract(pure = true)
            public @NotNull String getValue() {
                return value;
            }

            @Contract("_->this")
            public @NotNull Builder domain(@NotNull Domain domain) {
                this.domain = domain;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder expires(@NotNull OffsetDateTime expires) {
                this.expires = expires;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder maxAge(@NotNull Duration maxAge) {
                this.maxAge = maxAge;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder path(@NotNull Path path) {
                this.path = path;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder sameSite(@NotNull SameSite sameSite) {
                this.sameSite = sameSite;
                return this;
            }

            @Contract("_->this")
            public @NotNull Builder httpOnly(boolean httpOnly) {
                this.httpOnly = httpOnly;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder partitioned(boolean partitioned) {
                this.partitioned = partitioned;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder secure(boolean secure) {
                this.secure = secure;
                return this;
            }

            // Builder

            public @NotNull Request build() {
                return new Request(key, value, domain, expires, maxAge, path, sameSite, httpOnly, partitioned, secure);
            }

        }

    }

}
