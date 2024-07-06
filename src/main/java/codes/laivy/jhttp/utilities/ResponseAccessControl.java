package codes.laivy.jhttp.utilities;

import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.headers.HttpHeaderKey;
import codes.laivy.jhttp.headers.Wildcard;
import codes.laivy.jhttp.url.URIAuthority;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

public final class ResponseAccessControl {

    // Static initializers

    public static @NotNull Builder builder() {
        return new Builder();
    }

    // Object

    private final boolean credentials;
    private final @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> headers;
    private final @Nullable Wildcard<@NotNull Method @NotNull []> methods;
    private final @Nullable Wildcard<@Nullable URIAuthority> origin;
    private final @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> exposeHeaders;
    private final @Nullable Duration age;

    public ResponseAccessControl(boolean credentials, @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> headers, @Nullable Wildcard<@NotNull Method @NotNull []> methods, @Nullable Wildcard<@Nullable URIAuthority> origin, @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> exposeHeaders, @Nullable Duration age) {
        this.credentials = credentials;
        this.headers = headers;
        this.methods = methods;
        this.origin = origin;
        this.exposeHeaders = exposeHeaders;
        this.age = age;
    }

    // Getters

    public boolean hasCredentials() {
        return credentials;
    }
    public @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> getHeaders() {
        return headers;
    }
    public @Nullable Wildcard<@NotNull Method @NotNull []> getMethods() {
        return methods;
    }
    public @Nullable Wildcard<@Nullable URIAuthority> getOrigin() {
        return origin;
    }
    public @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> getExposeHeaders() {
        return exposeHeaders;
    }
    public @Nullable Duration getAge() {
        return age;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull ResponseAccessControl that = (ResponseAccessControl) object;
        return credentials == that.credentials && Objects.equals(getHeaders(), that.getHeaders()) && Objects.equals(getMethods(), that.getMethods()) && Objects.equals(getOrigin(), that.getOrigin()) && Objects.equals(getExposeHeaders(), that.getExposeHeaders()) && Objects.equals(getAge(), that.getAge());
    }
    @Override
    public int hashCode() {
        return Objects.hash(credentials, getHeaders(), getMethods(), getOrigin(), getExposeHeaders(), getAge());
    }

    // Classes

    public static final class Builder {

        private boolean credentials = false;
        private @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> headers;
        private @Nullable Wildcard<@NotNull Method @NotNull []> methods;
        private @Nullable Wildcard<@Nullable URIAuthority> origin;
        private @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> exposeHeaders;
        private @Nullable Duration age;

        private Builder() {
        }

        // Modules

        @Contract("_->this")
        public @NotNull Builder credentials(boolean credentials) {
            this.credentials = credentials;
            return this;
        }

        @Contract("_->this")
        public @NotNull Builder headers(@Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> headers) {
            this.headers = headers;
            return this;
        }
        @Contract("_->this")
        public @NotNull Builder headers(@NotNull HttpHeaderKey<?> @Nullable ... headers) {
            this.headers = Wildcard.create(headers);
            return this;
        }

        @Contract("_->this")
        public @NotNull Builder methods(@Nullable Wildcard<@NotNull Method @NotNull []> methods) {
            this.methods = methods;
            return this;
        }
        @Contract("_->this")
        public @NotNull Builder methods(@NotNull Method @Nullable ... methods) {
            this.methods = Wildcard.create(methods);
            return this;
        }

        @Contract("_->this")
        public @NotNull Builder origin(@Nullable Wildcard<@Nullable URIAuthority> origin) {
            this.origin = origin;
            return this;
        }
        @Contract("_->this")
        public @NotNull Builder origin(@Nullable URIAuthority origin) {
            this.origin = Wildcard.create(origin);
            return this;
        }

        @Contract("_->this")
        public @NotNull Builder exposeHeaders(@Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> exposeHeaders) {
            this.exposeHeaders = exposeHeaders;
            return this;
        }
        @Contract("_->this")
        public @NotNull Builder exposeHeaders(@NotNull HttpHeaderKey<?> @Nullable ... exposeHeaders) {
            this.exposeHeaders = Wildcard.create(exposeHeaders);
            return this;
        }

        @Contract("_->this")
        public @NotNull Builder age(@Nullable Duration age) {
            this.age = age;
            return this;
        }

        // Builder

        public @NotNull ResponseAccessControl build() {
            return new ResponseAccessControl(credentials, headers, methods, origin, exposeHeaders, age);
        }

    }

}
