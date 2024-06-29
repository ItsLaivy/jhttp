package codes.laivy.jhttp.headers;

import codes.laivy.jhttp.authorization.Credentials;
import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.Target;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.headers.Header.Type;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.module.*;
import codes.laivy.jhttp.module.CrossOrigin.EmbedderPolicy;
import codes.laivy.jhttp.module.UserAgent.Product;
import codes.laivy.jhttp.module.attribution.Eligible;
import codes.laivy.jhttp.module.connection.Connection;
import codes.laivy.jhttp.module.connection.EffectiveConnectionType;
import codes.laivy.jhttp.module.content.AcceptRange;
import codes.laivy.jhttp.module.content.ContentDisposition;
import codes.laivy.jhttp.module.content.ContentRange;
import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import codes.laivy.jhttp.network.BitMeasure;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.url.email.Email;
import codes.laivy.jhttp.utilities.DateUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.*;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static codes.laivy.jhttp.module.CrossOrigin.OpenerPolicy;
import static codes.laivy.jhttp.module.CrossOrigin.ResourcePolicy;

@SuppressWarnings("DeprecatedIsStillUsed")
// todo: 26/06/2024 finish all headers mapping (already mapped ±80%)
public abstract class HeaderKey<T> {

    // Static initializers

    public static final @NotNull Pattern NAME_FORMAT_REGEX = Pattern.compile("^[A-Za-z][A-Za-z0-9-]*$");

    public static @NotNull HeaderKey<?> retrieve(@NotNull String name) {
        try {
            @NotNull Field field = HeaderKey.class.getDeclaredField(name.replace("-", "_").toUpperCase());
            field.setAccessible(true);

            if (field.getType() == HeaderKey.class) {
                return (HeaderKey<?>) field.get(null);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
        } catch (Throwable throwable) {
            throw new IllegalStateException("Cannot create header key '" + name + "'");
        }

        return new HeaderKey<String>(name, Target.BOTH) {
            @Override
            public @NotNull Header<String> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return Header.create(this, value);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<String> header) {
                return header.getValue();
            }
        };
    }

    // Provided

    public static @NotNull HeaderKey<@NotNull MediaType<?> @NotNull []> ACCEPT = new Provided.AcceptHeaderKey();
    public static @NotNull HeaderKey<@NotNull HeaderKey<?> @NotNull []> ACCEPT_CH = new Provided.AcceptCHHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<@NotNull Duration> ACCEPT_CH_LIFETIME = new Provided.AcceptCHLifetimeHeaderKey();
    public static @NotNull HeaderKey<@NotNull Weight<@NotNull Deferred<Charset>> @NotNull []> ACCEPT_CHARSET = new Provided.AcceptCharsetHeaderKey();
    public static @NotNull HeaderKey<@NotNull Wildcard<@NotNull Weight<@NotNull Deferred<Encoding>> @NotNull []>> ACCEPT_ENCODING = new Provided.AcceptEncodingHeaderKey();
    public static @NotNull HeaderKey<@NotNull Wildcard<@NotNull Weight<@NotNull Locale> @NotNull[]>> ACCEPT_LANGUAGE = new Provided.AcceptLanguageHeaderKey();
    public static @NotNull HeaderKey<@NotNull MediaType<?> @NotNull []> ACCEPT_PATCH = new Provided.AcceptPatchHeaderKey();
    public static @NotNull HeaderKey<MediaType. @NotNull Type @NotNull []> ACCEPT_POST = new Provided.AcceptPostHeaderKey();
    public static @NotNull HeaderKey<@NotNull AcceptRange> ACCEPT_RANGES = new Provided.AcceptRangesHeaderKey();
    public static @NotNull HeaderKey<@NotNull Boolean> ACCEPT_CONTROL_ALLOW_CREDENTIALS = new Provided.AcceptControlAllowCredentials();
    public static @NotNull HeaderKey<@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []>> ACCEPT_CONTROL_ALLOW_HEADERS = new Provided.AcceptControlAllowHeadersHeaderKey();
    public static @NotNull HeaderKey<@NotNull Wildcard<@NotNull Method @NotNull []>> ACCEPT_CONTROL_ALLOW_METHODS = new Provided.AccessControlAllowMethodsHeaderKey();
    public static @NotNull HeaderKey<@NotNull Wildcard<@Nullable URIAuthority>> ACCEPT_CONTROL_ALLOW_ORIGIN = new Provided.AccessControlAllowOriginHeaderKey();
    public static @NotNull HeaderKey<@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []>> ACCEPT_CONTROL_EXPOSE_HEADERS = new Provided.AcceptControlExposeHeadersHeaderKey();
    public static @NotNull HeaderKey<@NotNull Duration> ACCEPT_CONTROL_MAX_AGE = new Provided.AcceptControlMaxAgeHeaderKey();
    public static @NotNull HeaderKey<@NotNull HeaderKey<?> @NotNull []> ACCEPT_CONTROL_REQUEST_HEADERS = new Provided.AcceptControlRequestHeadersHeaderKey();
    public static @NotNull HeaderKey<@NotNull Method> ACCEPT_CONTROL_REQUEST_METHOD = new Provided.AccessControlRequestMethodHeaderKey();
    public static @NotNull HeaderKey<@NotNull Duration> AGE = new Provided.AgeHeaderKey();
    public static @NotNull HeaderKey<@NotNull Method @NotNull []> ALLOW = new Provided.AllowHeaderKey();
    public static @NotNull HeaderKey<@NotNull Optional<@NotNull AlternativeService @NotNull []>> ALT_SVC = new Provided.AltSvcHeaderKey();
    public static @NotNull HeaderKey<@NotNull URIAuthority> ALT_USED = new Provided.AltUsedHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull Eligible> ATTRIBUTION_REPORTING_ELIGIBLE = new Provided.AttributionReportingEligibleHeaderKey();
    @ApiStatus.Experimental
    // todo: attribution reporting register source object
    public static @NotNull HeaderKey<@NotNull JsonObject> ATTRIBUTION_REPORTING_REGISTER_SOURCE = new Provided.AttributionReportingRegisterSourceHeaderKey();
    @ApiStatus.Experimental
    // todo: attribution reporting register trigger object
    public static @NotNull HeaderKey<@NotNull JsonObject> ATTRIBUTION_REPORTING_REGISTER_TRIGGER = new Provided.AttributionReportingRegisterTriggerHeaderKey();
    public static @NotNull HeaderKey<@NotNull Credentials> AUTHORIZATION = new Provided.AuthorizationHeaderKey();
    public static @NotNull HeaderKey<@NotNull CacheControl> CACHE_CONTROL = new Provided.CacheControlHeaderKey();
    public static @NotNull HeaderKey<@NotNull Wildcard<@NotNull SiteData @NotNull []>> CLEAR_SITE_DATA = new Provided.ClearSiteDataHeaderKey();
    public static @NotNull HeaderKey<@NotNull Connection> CONNECTION = new Provided.ConnectionHeaderKey();
    public static @NotNull HeaderKey<@NotNull ContentDisposition> CONTENT_DISPOSITION = new Provided.ContentDispositionHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<@NotNull Float> CONTENT_DPR = new Provided.ContentDPRHeaderKey();
    public static @NotNull HeaderKey<@NotNull Deferred<Encoding> @NotNull []> CONTENT_ENCODING = new Provided.ContentEncodingHeaderKey();
    public static @NotNull HeaderKey<@NotNull Locale @NotNull []> CONTENT_LANGUAGE = new Provided.ContentLanguageHeaderKey();
    public static @NotNull HeaderKey<@NotNull BitMeasure> CONTENT_LENGTH = new Provided.ContentLengthHeaderKey();
    public static @NotNull HeaderKey<@NotNull Origin> CONTENT_LOCATION = new Provided.ContentLocationHeaderKey();
    public static @NotNull HeaderKey<@NotNull ContentRange> CONTENT_RANGE = new Provided.ContentRangeHeaderKey();
    public static @NotNull HeaderKey<@NotNull ContentSecurityPolicy> CONTENT_SECURITY_POLICY = new Provided.ContentSecurityPolicyHeaderKey();
    public static @NotNull HeaderKey<@NotNull ContentSecurityPolicy> CONTENT_SECURITY_POLICY_REPORT_ONLY = new Provided.ContentSecurityPolicyReportOnlyeHeaderKey();
    public static @NotNull HeaderKey<@NotNull MediaType<?>> CONTENT_TYPE = new Provided.ContentTypeHeaderKey();
    public static @NotNull HeaderKey<@NotNull Cookie @NotNull []> COOKIE = new Provided.CookieHeaderKey();
    public static @NotNull HeaderKey<@NotNull HeaderKey<?> @NotNull []> CRITICAL_CH = new Provided.CriticalCHHeaderKey();
    public static @NotNull HeaderKey<@NotNull EmbedderPolicy> CROSS_ORIGIN_EMBEDDER_POLICY = new Provided.CrossOriginEmbedderPolicyHeaderKey();
    public static @NotNull HeaderKey<@NotNull OpenerPolicy> CROSS_ORIGIN_OPENER_POLICY = new Provided.CrossOriginOpenerPolicyHeaderKey();
    public static @NotNull HeaderKey<@NotNull ResourcePolicy> CROSS_ORIGIN_RESOURCE_POLICY = new Provided.CrossOriginResourcePolicyHeaderKey();
    public static @NotNull HeaderKey<@NotNull OffsetDateTime> DATE = new Provided.DateHeaderKey();
    public static @NotNull HeaderKey<@NotNull BitMeasure> DEVICE_MEMORY = new Provided.DeviceMemoryHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<@NotNull Digest @NotNull []> DIGEST = new Provided.DigestHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<@NotNull Boolean> DNT = new Provided.DNTHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull BitMeasure> DOWNLINK = new Provided.DownlinkHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<@NotNull Float> DPR = new Provided.DPRHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@UnknownNullability Void> EARLY_DATA = new Provided.EarlyDataHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull EffectiveConnectionType> ECT = new Provided.ECTHeaderKey();
    public static @NotNull HeaderKey<@NotNull EntityTag> ETAG = new Provided.ETagHeaderKey();
    public static @NotNull HeaderKey<@NotNull HttpStatus> EXPECT = new Provided.ExpectHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<@NotNull ExpectCertificate> EXPECT_CT = new Provided.ExpectCTHeaderKey();
    public static @NotNull HeaderKey<@Nullable OffsetDateTime> EXPIRES = new Provided.ExpiresHeaderKey();
    public static @NotNull HeaderKey<@NotNull Forwarded> FORWARDED = new Provided.ForwardedHeaderKey();
    public static @NotNull HeaderKey<@NotNull Email> FROM = new Provided.FromHeaderKey();
    public static @NotNull HeaderKey<@NotNull Host> HOST = new Provided.HostHeaderKey();
    public static @NotNull HeaderKey<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> IF_MATCH = new Provided.IfMatchHeaderKey();
    public static @NotNull HeaderKey<@NotNull OffsetDateTime> IF_MODIFIED_SINCE = new Provided.IfModifiedSinceHeaderKey();
    public static @NotNull HeaderKey<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> IF_NONE_MATCH = new Provided.IfNoneMatchHeaderKey();
    public static @NotNull HeaderKey<@NotNull OffsetDateTime> IF_UNMODIFIED_SINCE = new Provided.IfUnmodifiedSinceHeaderKey();
    public static @NotNull HeaderKey<@NotNull KeepAlive> KEEP_ALIVE = new Provided.KeepAliveHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<@NotNull Optional<@NotNull BitMeasure>> LARGE_ALLOCATION = new Provided.LargeAllocationHeaderKey();
    public static @NotNull HeaderKey<@NotNull OffsetDateTime> LAST_MODIFIED = new Provided.LastModifiedHeaderKey();
    public static @NotNull HeaderKey<@NotNull Origin> LOCATION = new Provided.LocationHeaderKey();
    public static @NotNull HeaderKey<@NotNull Integer> MAX_FORWARDS = new Provided.MaxForwardsHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull NetworkErrorLogging> NEL = new Provided.NetworkErrorLoggingHeaderKey();
    public static @NotNull HeaderKey<@NotNull > NO_VARY_SEARCH = new Provided.();
    public static @NotNull HeaderKey<@NotNull > OBSERVE_BROWSING_TOPICS = new Provided.();
    public static @NotNull HeaderKey<@Nullable Host> ORIGIN = new Provided.OriginHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull Boolean> ORIGIN_AGENT_CLUSTER = new Provided.OriginAgentClusterHeaderKey();
    public static @NotNull HeaderKey<@NotNull > PERMISSIONS_POLICY = new Provided.();
    @Deprecated
    public static @NotNull HeaderKey<@UnknownNullability Void> PRAGMA = new Provided.PragmaHeaderKey();
    public static @NotNull HeaderKey<@NotNull Credentials> PROXY_AUTHORIZATION = new Provided.ProxyAuthorizationHeaderKey();
    public static @NotNull HeaderKey<@NotNull > RANGE = new Provided.();
    public static @NotNull HeaderKey<@NotNull Origin> REFERER = new Provided.RefererHeaderKey();
    public static @NotNull HeaderKey<@NotNull > REFERER_POLICY = new Provided.();
    public static @NotNull HeaderKey<@NotNull > REPORTING_ENDPOINTS = new Provided.();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull > REPR_DIGEST = new Provided.();
    public static @NotNull HeaderKey<@NotNull > RETRY_AFTER = new Provided.();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull Duration> RTT = new Provided.RTTHeaderKey();
    public static @NotNull HeaderKey<@NotNull Boolean> SAVE_DATA = new Provided.SaveDataHeaderKey();
    public static @NotNull HeaderKey<@NotNull > SEC_BROWSING_TOPICS = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_PREFERS_COLOR_SCHEME = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_PREFERS_REDUCED_MOTION = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_PREFERS_REDUCED_TRANSPARENCY = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_UA = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_UA_ARCH = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_UA_BITNESS = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_UA_FULL_VERSION = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_UA_FULL_VERSION_LIST = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_UA_MOBILE = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_UA_MODEL = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_UA_PLATFORM = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_CH_UA_PLATFORM_VERSION = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_FETCH_DEST = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_FETCH_MODE = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_FETCH_SITE = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_FETCH_USER = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_GPC = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_PURPOSE = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SEC_WEBSOCKET_ACCEPT = new Provided.();
    public static @NotNull HeaderKey<@NotNull Product> SERVER = new Provided.ServerHeaderKey();
    public static @NotNull HeaderKey<@NotNull > SERVER_TIMING = new Provided.();
    public static @NotNull HeaderKey<@NotNull > SERVICE_WORKER_NAVIGATION_PRELOAD = new Provided.();
    public static @NotNull HeaderKey<Cookie. @NotNull Request> SET_COOKIE = new Provided.SetCookieHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull > SET_LOGIN = new Provided.();
    public static @NotNull HeaderKey<@NotNull Origin> SOURCEMAP = new Provided.SourceMapHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull > SPECULATION_RULES = new Provided.();
    public static @NotNull HeaderKey<@NotNull > STRICT_TRANSPORT_SECURITY = new Provided.();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull > SUPPORTS_LOADING_MODE = new Provided.();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<@NotNull Weight<@NotNull Deferred<Encoding>> @NotNull []> TE = new Provided.TEHeaderKey();
    public static @NotNull HeaderKey<@NotNull > TIMING_ALLOW_ORIGIN = new Provided.();
    public static @NotNull HeaderKey<@NotNull > TK = new Provided.();
    public static @NotNull HeaderKey<@NotNull HeaderKey<?> @NotNull []> TRAILER = new Provided.TrailerHeaderKey();
    public static @NotNull HeaderKey<@NotNull Deferred<Encoding> @NotNull []> TRANSFER_ENCODING = new Provided.TransferEncodingHeaderKey();
    public static @NotNull HeaderKey<@NotNull Upgrade> UPGRADE = new Provided.UpgradeHeaderKey();
    public static @NotNull HeaderKey<@NotNull > UPGRADE_INSECURE_REQUESTS = new Provided.();
    public static @NotNull HeaderKey<@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []>> VARY = new Provided.VaryHeaderKey();
    public static @NotNull HeaderKey<@NotNull UserAgent> USER_AGENT = new Provided.UserAgentHeaderKey();
    public static @NotNull HeaderKey<@NotNull > VARY = new Provided.();
    public static @NotNull HeaderKey<@NotNull > VIA = new Provided.();
    public static @NotNull HeaderKey<@NotNull > VIEWPORT_WIDTH = new Provided.();
    public static @NotNull HeaderKey<@NotNull > WANT_CONTENT_DIGEST = new Provided.();
    public static @NotNull HeaderKey<@NotNull > WANT_DIGEST = new Provided.();
    public static @NotNull HeaderKey<@NotNull > WANT_REPR_DIGEST = new Provided.();
    public static @NotNull HeaderKey<@NotNull > WARNING = new Provided.();
    public static @NotNull HeaderKey<@NotNull > WIDTH = new Provided.();
    public static @NotNull HeaderKey<@NotNull > WWW_AUTHENTICATE = new Provided.();
    public static @NotNull HeaderKey<@NotNull > X_CONTENT_TYPE_OPTIONS = new Provided.();
    public static @NotNull HeaderKey<@NotNull > X_DNS_PREFETCH_CONTROL = new Provided.();
    public static @NotNull HeaderKey<@NotNull > X_FORWARDED_FOR = new Provided.();
    public static @NotNull HeaderKey<@NotNull > X_FORWARDED_HOST = new Provided.();
    public static @NotNull HeaderKey<@NotNull > X_FORWARDED_PROTO = new Provided.();
    public static @NotNull HeaderKey<@NotNull > X_FRAME_OPTIONS = new Provided.();
    public static @NotNull HeaderKey<@NotNull > X_XSS_PROTECTION = new Provided.();

    // Object

    private final @NotNull String name;
    private final @NotNull Target target;
    private final @NotNull Type[] types;

    protected HeaderKey(@NotNull String name, @NotNull Target target) {
        this.name = name;
        this.target = target;
        this.types = Arrays.stream(Type.values()).filter(type -> type.matches(this)).toArray(Type[]::new);

        if (!NAME_FORMAT_REGEX.matcher(name).matches()) {
            throw new IllegalArgumentException("this header key name '" + name + "' have illegal characters");
        }
    }

    @Contract(pure = true)
    public final @NotNull String getName() {
        return this.name;
    }
    @Contract(pure = true)
    public final @NotNull Target getTarget() {
        return this.target;
    }
    @Contract(pure = true)
    public final @NotNull Type[] getTypes() {
        return types;
    }
    public final boolean hasType(@NotNull Type type) {
        return type.matches(this);
    }

    @Contract(pure = true)
    public final boolean isHopByHop() {
        return Type.HOP_BY_HOP.matches(this);
    }
    @Contract(pure = true)
    public final boolean isEndToEnd() {
        return !isHopByHop();
    }

    @Contract(pure = true)
    public final boolean isClientHint() {
        return Type.CLIENT_HINT.matches(this);
    }
    @Contract(pure = true)
    public final boolean isConditional() {
        return Type.CONDITIONAL.matches(this);
    }

    // Modules

    public abstract @NotNull Header<T> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException;
    public abstract @NotNull String write(@NotNull HttpVersion version, @NotNull Header<T> header);

    public @NotNull Header<T> create(@UnknownNullability T value) {
        return new Provided.HeaderImpl<>(this, value);
    }

    // Implementations

    @Override
    public final boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof HeaderKey)) return false;
        @NotNull HeaderKey<?> that = (HeaderKey<?>) object;
        return getName().equalsIgnoreCase(that.getName());
    }
    @Override
    public final int hashCode() {
        return Objects.hash(getName().toLowerCase());
    }
    @Override
    public final @NotNull String toString() {
        return getName();
    }

    // Classes

    private static final class Provided {
        private Provided() {
            throw new UnsupportedOperationException();
        }

        private static final class DigestHeaderKey extends HeaderKey<@NotNull Digest @NotNull []> {
            private DigestHeaderKey() {
                super("Digest", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<@NotNull Digest[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    @NotNull String[] split = value.split("\\s*,\\s*");
                    @NotNull Set<Digest> digests = new HashSet<>();

                    for (@NotNull String string : split) {
                        digests.add(Digest.Parser.deserialize(string));
                    }

                    return create(digests.toArray(new Digest[0]));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<@NotNull Digest[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Digest digest : header.getValue()) {
                    if (builder.length() > 0) builder.append(",");
                    builder.append(Digest.Parser.serialize(digest));
                }

                return builder.toString();
            }
        }
        private static final class AttributionReportingRegisterTriggerHeaderKey extends HeaderKey<@NotNull JsonObject> {
            private AttributionReportingRegisterTriggerHeaderKey() {
                super("Attribution-Reporting-Register-Trigger", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<@NotNull JsonObject> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(JsonParser.parseString(value).getAsJsonObject());
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<@NotNull JsonObject> header) {
                return header.getValue().toString();
            }
        }
        private static final class AttributionReportingRegisterSourceHeaderKey extends HeaderKey<@NotNull JsonObject> {
            private AttributionReportingRegisterSourceHeaderKey() {
                super("Attribution-Reporting-Register-Source", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<@NotNull JsonObject> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(JsonParser.parseString(value).getAsJsonObject());
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<@NotNull JsonObject> header) {
                return header.getValue().toString();
            }
        }
        private static final class AttributionReportingEligibleHeaderKey extends HeaderKey<@NotNull Eligible> {
            private AttributionReportingEligibleHeaderKey() {
                super("Attribution-Reporting-Eligible", Target.REQUEST);
            }

            @Override
            public @NotNull Header<@NotNull Eligible> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Eligible.getById(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<@NotNull Eligible> header) {
                return header.getValue().getId();
            }
        }
        private static final class UpgradeHeaderKey extends HeaderKey<@NotNull Upgrade> {
            private UpgradeHeaderKey() {
                super("Upgrade", Target.BOTH);
            }

            @Override
            public @NotNull Header<@NotNull Upgrade> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Upgrade.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<@NotNull Upgrade> header) {
                return Upgrade.Parser.serialize(header.getValue());
            }
        }
        private static final class AcceptControlAllowCredentials extends HeaderKey<@NotNull Boolean> {
            private AcceptControlAllowCredentials() {
                super("Access-Control-Allow-Credentials", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Boolean> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Boolean.parseBoolean(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Boolean> header) {
                return header.getValue().toString();
            }
        }
        private static final class UserAgentHeaderKey extends HeaderKey<@NotNull UserAgent> {
            private UserAgentHeaderKey() {
                super("User-Agent", Target.REQUEST);
            }

            @Override
            public @NotNull Header<UserAgent> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(UserAgent.Parser.deserialize(value));
                } catch (@NotNull Throwable throwable) {
                    throw new HeaderFormatException("cannot parse '" + value + "' as a valid user agent header", throwable);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<UserAgent> header) {
                return UserAgent.Parser.serialize(header.getValue());
            }
        }
        private static final class VaryHeaderKey extends HeaderKey<@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []>> {
            private VaryHeaderKey() {
                super("Vary", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Wildcard<HeaderKey<?>[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                } else {
                    @NotNull Set<HeaderKey<?>> keys = new HashSet<>();

                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        keys.add(HeaderKey.retrieve(name));
                    }

                    return create(Wildcard.create(keys.toArray(new HeaderKey[0])));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<HeaderKey<?>[]>> header) {
                if (header.getValue().isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull HeaderKey<?> key : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(key);
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull Header<@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []>> create(@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []> value) {
                if (!value.isWildcard()) {
                    if (value.getValue().length == 0) {
                        throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                    } else if (Arrays.stream(value.getValue()).anyMatch(key -> !key.getTarget().isRequests())) {
                        throw new IllegalArgumentException("the '" + getName() + "' header value only accept request headers as value");
                    }
                }
                return super.create(value);
            }
        }
        private static final class TrailerHeaderKey extends HeaderKey<@NotNull HeaderKey<?> @NotNull []> {
            private TrailerHeaderKey() {
                super("Trailer", Target.BOTH);
            }

            @Override
            public @NotNull Header<HeaderKey<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Set<HeaderKey<?>> keys = new HashSet<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    @NotNull HeaderKey<?> key = HeaderKey.retrieve(name);

                    if (key.getTarget() == Target.RESPONSE) {
                        // Ignore response headers
                        continue;
                    }

                    keys.add(key);
                }

                return create(keys.toArray(new HeaderKey[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<HeaderKey<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull HeaderKey<?> key : header.getValue()) {
                    if (key.getTarget() == Target.RESPONSE) {
                        // Ignore response headers
                        continue;
                    }

                    if (builder.length() > 0) builder.append(", ");
                    builder.append(key.getName());
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<HeaderKey<?>[]> create(HeaderKey<?> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                } else if (!Arrays.stream(value).allMatch(
                        key -> key.hasType(Type.CONTENT) ||
                                key.hasType(Type.ROOTING) ||
                                key.hasType(Type.CONTROL) ||
                                key.hasType(Type.CONDITIONAL) ||
                                key.hasType(Type.AUTHENTICATION) ||
                                key.getName().equalsIgnoreCase("Trailer")
                )) {
                    throw new IllegalArgumentException("the '" + getName() + "' header value only accept content, rooting, control, conditional, authentication types or itself (Trailer)");
                }

                return super.create(value);
            }
        }
        private static final class TEHeaderKey extends HeaderKey<@NotNull Weight< @NotNull Deferred<Encoding>> @NotNull []> {
            private TEHeaderKey() {
                super("TE", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Weight<Deferred<Encoding>>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Pattern pattern = Pattern.compile("(?<encoding>[^,;\\s]+)(?:\\s*;\\s*q\\s*=\\s*(?<weight>\\d(?:[.,]\\d)?))?");
                @NotNull Matcher matcher = pattern.matcher(value);

                @NotNull Set<Weight<Deferred<Encoding>>> pairs = new HashSet<>();

                while (matcher.find()) {
                    @NotNull String name = matcher.group("encoding");
                    @Nullable Float weight = matcher.group("weight") != null ? Float.parseFloat(matcher.group("weight").replace(",", ".")) : null;

                    pairs.add(Weight.create(weight, Deferred.encoding(name)));
                }

                //noinspection unchecked
                return create(pairs.toArray(new Weight[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Weight<Deferred<Encoding>>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Weight<Deferred<Encoding>> pseudo : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(pseudo);
                }

                return builder.toString();
            }
        }
        private static final class SourceMapHeaderKey extends HeaderKey<@NotNull Origin> {
            private SourceMapHeaderKey() {
                super("SourceMap", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Origin> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Origin.Parser.deserialize(value));
                } catch (ParseException | UnknownHostException | URISyntaxException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Origin> header) {
                return Origin.Parser.serialize(header.getValue());
            }
        }
        private static final class SaveDataHeaderKey extends HeaderKey<@NotNull Boolean> {
            private SaveDataHeaderKey() {
                super("Save-Data", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Boolean> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(value.equalsIgnoreCase("on"));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Boolean> header) {
                return header.getValue() ? "on" : "off";
            }
        }
        private static final class RTTHeaderKey extends HeaderKey<@NotNull Duration> {
            private RTTHeaderKey() {
                super("RTT", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Duration> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Duration.ofMillis(Long.parseLong(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Duration> header) {
                return String.valueOf(header.getValue().toMillis());
            }
        }
        private static final class RefererHeaderKey extends HeaderKey<@NotNull Origin> {
            private RefererHeaderKey() {
                super("Referer", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Origin> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Origin.Parser.deserialize(value));
                } catch (ParseException | UnknownHostException | URISyntaxException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Origin> header) {
                return Origin.Parser.serialize(header.getValue());
            }
        }
        private static final class ProxyAuthorizationHeaderKey extends HeaderKey<@NotNull Credentials> {
            private ProxyAuthorizationHeaderKey() {
                super("Proxy-Authorization", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Credentials> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull String[] split = value.split(" ", 2);
                @NotNull Credentials credentials;

                if (split.length != 2) {
                    throw new HeaderFormatException("credentials missing values");
                }

                try {
                    if (split[0].equalsIgnoreCase("Bearer")) {
                        credentials = new Credentials.Bearer(split[1].toLowerCase());
                    } else if (split[0].equalsIgnoreCase("Basic")) {
                        credentials = Credentials.Basic.parse(split[1]);
                    } else {
                        credentials = Credentials.Unknown.create(value.toCharArray());
                    }
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException("cannot parse '" + split[0] + "' credential '" + value + "'");
                }

                return create(credentials);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Credentials> header) {
                return header.getValue().toString();
            }
        }
        private static final class PragmaHeaderKey extends HeaderKey<@UnknownNullability Void> {
            private PragmaHeaderKey() {
                super("Pragma", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Void> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create((Void) null);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Void> header) {
                return "no-cache";
            }
        }
        private static final class OriginAgentClusterHeaderKey extends HeaderKey<@NotNull Boolean> {
            private OriginAgentClusterHeaderKey() {
                super("Origin-Agent-Cluster", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Boolean> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.equalsIgnoreCase("?1")) {
                    return create(true);
                } else if (value.equalsIgnoreCase("?0")) {
                    return create(false);
                } else {
                    return create(Boolean.parseBoolean(value));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Boolean> header) {
                return header.getValue().toString();
            }
        }
        private static final class OriginHeaderKey extends HeaderKey<@Nullable Host> {
            private OriginHeaderKey() {
                super("Origin", Target.REQUEST);
            }

            @Override
            public @NotNull Header<@Nullable Host> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equalsIgnoreCase("null")) {
                    return create(null);
                } else try {
                    return create(Host.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<@Nullable Host> header) {
                return header.getValue() != null ? header.getValue().toString() : "null";
            }
        }
        private static final class NetworkErrorLoggingHeaderKey extends HeaderKey<@NotNull NetworkErrorLogging> {
            private NetworkErrorLoggingHeaderKey() {
                super("NEL", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<NetworkErrorLogging> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(NetworkErrorLogging.Parser.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<NetworkErrorLogging> header) {
                return NetworkErrorLogging.Parser.serialize(header.getValue());
            }
        }
        private static final class ExpectCTHeaderKey extends HeaderKey<@NotNull ExpectCertificate> {
            private ExpectCTHeaderKey() {
                super("Expect-CT", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<ExpectCertificate> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(ExpectCertificate.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<ExpectCertificate> header) {
                return ExpectCertificate.Parser.serialize(header.getValue());
            }
        }
        private static final class CriticalCHHeaderKey extends HeaderKey<@NotNull HeaderKey<?> @NotNull []> {
            private CriticalCHHeaderKey() {
                super("Critical-CH", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<HeaderKey<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Set<HeaderKey<?>> keys = new HashSet<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    @NotNull HeaderKey<?> key = HeaderKey.retrieve(name);

                    if (key.getTarget() == Target.RESPONSE) {
                        // Ignore response headers
                        continue;
                    }

                    keys.add(key);
                }

                return create(keys.toArray(new HeaderKey[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<HeaderKey<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull HeaderKey<?> key : header.getValue()) {
                    if (key.getTarget() == Target.RESPONSE) {
                        // Ignore response headers
                        continue;
                    }

                    if (builder.length() > 0) builder.append(", ");
                    builder.append(key.getName());
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<HeaderKey<?>[]> create(HeaderKey<?> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                } else if (Arrays.stream(value).anyMatch(key -> !key.isClientHint())) {
                    throw new IllegalArgumentException("the '" + getName() + "' header value only accept client hint headers as value");
                }

                return super.create(value);
            }
        }
        private static final class ContentLengthHeaderKey extends HeaderKey<@NotNull BitMeasure> {
            private ContentLengthHeaderKey() {
                super("Content-Length", Target.BOTH);
            }

            @Override
            public @NotNull Header<BitMeasure> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(BitMeasure.create(BitMeasure.Level.BYTES, Double.parseDouble(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<BitMeasure> header) {
                return String.valueOf(header.getValue().getBytes());
            }

            @Override
            public @NotNull Header<@NotNull BitMeasure> create(@NotNull BitMeasure value) {
                if (value.getBytes() < 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must be higher or equal to zero");
                }
                return super.create(value);
            }
        }
        private static final class ContentLanguageHeaderKey extends HeaderKey<@NotNull Locale @NotNull []> {
            private ContentLanguageHeaderKey() {
                super("Content-Language", Target.BOTH);
            }

            @Override
            public @NotNull Header<Locale[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull List<Locale> locales = new ArrayList<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    locales.add(new Locale(name));
                }

                return create(locales.toArray(new Locale[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Locale[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Locale locale : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(locale);
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<@NotNull Locale @NotNull []> create(@NotNull Locale @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class ContentDPRHeaderKey extends HeaderKey<@NotNull Float> {
            private ContentDPRHeaderKey() {
                super("Content-DPR", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Float> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Float.parseFloat(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Float> header) {
                return String.valueOf(header.getValue());
            }

            @Override
            public @NotNull Header<@NotNull Float> create(@NotNull Float value) {
                if (value <= 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' higher than zero");
                }
                return super.create(value);
            }
        }
        private static final class AcceptCHLifetimeHeaderKey extends HeaderKey<@NotNull Duration> {
            private AcceptCHLifetimeHeaderKey() {
                super("Accept-CH-Lifetime", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Duration> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Duration.ofSeconds(Integer.parseInt(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Duration> header) {
                return String.valueOf(header.getValue().getSeconds());
            }
        }
        private static final class AcceptControlMaxAgeHeaderKey extends HeaderKey<@NotNull Duration> {
            private AcceptControlMaxAgeHeaderKey() {
                super("Access-Control-Max-Age", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Duration> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Duration.ofSeconds(Integer.parseInt(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Duration> header) {
                return String.valueOf(header.getValue().getSeconds());
            }

            @Override
            public @NotNull Header<@NotNull Duration> create(@NotNull Duration value) {
                if (value.isNegative()) {
                    throw new IllegalArgumentException("the header '" + getName() + "' duration value cannot be negative");
                }
                return super.create(value);
            }
        }
        private static final class AgeHeaderKey extends HeaderKey<@NotNull Duration> {
            private AgeHeaderKey() {
                super("Age", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Duration> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Duration.ofSeconds(Integer.parseInt(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Duration> header) {
                return String.valueOf(header.getValue().getSeconds());
            }

            @Override
            public @NotNull Header<@NotNull Duration> create(@NotNull Duration value) {
                if (value.isNegative()) {
                    throw new IllegalArgumentException("the header '" + getName() + "' duration value cannot be negative");
                }
                return super.create(value);
            }
        }
        private static final class ServerHeaderKey extends HeaderKey<@NotNull Product> {
            private ServerHeaderKey() {
                super("Server", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Product> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return Header.create(this, Product.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' as a valid product", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Product> header) {
                return header.getValue().toString();
            }
        }
        private static final class SetCookieHeaderKey extends HeaderKey<Cookie. @NotNull Request> {
            private SetCookieHeaderKey() {
                super("Set-Cookie", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Cookie.Request> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Cookie.Request.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Cookie.Request> header) {
                return Cookie.Request.Parser.serialize(header.getValue());
            }
        }
        private static final class MaxForwardsHeaderKey extends HeaderKey<@NotNull Integer> {
            private MaxForwardsHeaderKey() {
                super("Max-Forwards", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Integer> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Integer.parseInt(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Integer> header) {
                return String.valueOf(header.getValue());
            }
        }
        private static final class LocationHeaderKey extends HeaderKey<@NotNull Origin> {
            private LocationHeaderKey() {
                super("Location", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Origin> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Origin.Parser.deserialize(value));
                } catch (ParseException | UnknownHostException | URISyntaxException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Origin> header) {
                return Origin.Parser.serialize(header.getValue());
            }
        }
        private static final class LastModifiedHeaderKey extends HeaderKey<@NotNull OffsetDateTime> {
            private LastModifiedHeaderKey() {
                super("Last-Modified", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(DateUtils.RFC822.convert(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<OffsetDateTime> header) {
                return DateUtils.RFC822.convert(header.getValue());
            }
        }
        private static final class LargeAllocationHeaderKey extends HeaderKey<@NotNull Optional<@NotNull BitMeasure>> {
            private LargeAllocationHeaderKey() {
                super("Large-Allocation", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Optional<BitMeasure>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("0")) {
                    return create(Optional.empty());
                } else try {
                    int megabytes = Integer.parseInt(value.replace(",", "."));
                    return create(Optional.of(BitMeasure.create(BitMeasure.Level.MEGABYTES, megabytes)));
                } catch (@NotNull NumberFormatException ignore) {
                    throw new HeaderFormatException("cannot parse '" + value + "' as a valid megabytes number");
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Optional<BitMeasure>> header) {
                @NotNull Optional<BitMeasure> optional = header.getValue();
                return optional.map(bitMeasure -> String.valueOf((int) bitMeasure.getMegabytes())).orElse("0");
            }
        }
        private static final class KeepAliveHeaderKey extends HeaderKey<@NotNull KeepAlive> {
            private KeepAliveHeaderKey() {
                super("Keep-Alive", Target.BOTH);
            }

            @Override
            public @NotNull Header<KeepAlive> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(KeepAlive.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<KeepAlive> header) {
                return KeepAlive.Parser.serialize(header.getValue());
            }
        }
        private static final class IfUnmodifiedSinceHeaderKey extends HeaderKey<@NotNull OffsetDateTime> {
            private IfUnmodifiedSinceHeaderKey() {
                super("If-Unmodified-Since", Target.REQUEST);
            }

            @Override
            public @NotNull Header<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(DateUtils.RFC822.convert(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<OffsetDateTime> header) {
                return DateUtils.RFC822.convert(header.getValue());
            }
        }
        private static final class IfNoneMatchHeaderKey extends HeaderKey<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> {
            private IfNoneMatchHeaderKey() {
                super("If-None-Match", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Wildcard<EntityTag[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                } else {
                    @NotNull Set<EntityTag> tag = new LinkedHashSet<>();

                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        try {
                            tag.add(EntityTag.Parser.deserialize(name));
                        } catch (ParseException e) {
                            throw new HeaderFormatException(e);
                        }
                    }

                    return create(Wildcard.create(tag.toArray(new EntityTag[0])));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<EntityTag[]>> header) {
                @NotNull Wildcard<EntityTag[]> wildcard = header.getValue();

                if (wildcard.isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull EntityTag tag : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(EntityTag.Parser.serialize(tag));
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull Header<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> create(@NotNull Wildcard<@NotNull EntityTag @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class IfModifiedSinceHeaderKey extends HeaderKey<@NotNull OffsetDateTime> {
            private IfModifiedSinceHeaderKey() {
                super("If-Modified-Since", Target.REQUEST);
            }

            @Override
            public @NotNull Header<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(DateUtils.RFC822.convert(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<OffsetDateTime> header) {
                return DateUtils.RFC822.convert(header.getValue());
            }
        }
        private static final class IfMatchHeaderKey extends HeaderKey<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> {
            private IfMatchHeaderKey() {
                super("If-Match", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Wildcard<EntityTag[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                } else {
                    @NotNull Set<EntityTag> tag = new LinkedHashSet<>();

                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        try {
                            tag.add(EntityTag.Parser.deserialize(name));
                        } catch (ParseException e) {
                            throw new HeaderFormatException(e);
                        }
                    }

                    return create(Wildcard.create(tag.toArray(new EntityTag[0])));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<EntityTag[]>> header) {
                @NotNull Wildcard<EntityTag[]> wildcard = header.getValue();

                if (wildcard.isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull EntityTag tag : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(EntityTag.Parser.serialize(tag));
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull Header<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> create(@NotNull Wildcard<@NotNull EntityTag @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class HostHeaderKey extends HeaderKey<@NotNull Host> {
            private HostHeaderKey() {
                super("Host", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Host> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Host.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Host> header) {
                return header.getValue().toString();
            }
        }
        private static final class FromHeaderKey extends HeaderKey<@NotNull Email> {
            private FromHeaderKey() {
                super("From", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Email> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Email.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Email> header) {
                return Email.Parser.serialize(header.getValue());
            }
        }
        private static final class ForwardedHeaderKey extends HeaderKey<@NotNull Forwarded> {
            private ForwardedHeaderKey() {
                super("Forwarded", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Forwarded> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Forwarded.Parser.deserialize(value));
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Forwarded> header) {
                return Forwarded.Parser.serialize(header.getValue());
            }
        }
        private static final class ExpiresHeaderKey extends HeaderKey<@Nullable OffsetDateTime> {
            private ExpiresHeaderKey() {
                super("Expires", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("0")) {
                    return create(null);
                } else {
                    @NotNull OffsetDateTime date = DateUtils.RFC822.convert(value);

                    if (date.isBefore(OffsetDateTime.now())) {
                        return create(null);
                    }

                    return create(date);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<OffsetDateTime> header) {
                if (header.getValue() == null || header.getValue().isBefore(OffsetDateTime.now())) {
                    return "0";
                } else {
                    return DateUtils.RFC822.convert(header.getValue());
                }
            }
        }
        private static final class ExpectHeaderKey extends HeaderKey<@NotNull HttpStatus> {
            private ExpectHeaderKey() {
                super("Expect", Target.REQUEST);
            }

            @Override
            public @NotNull Header<HttpStatus> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    int code = Integer.parseInt(value.split("-", 2)[0]);
                    return create(HttpStatus.getByCode(code));
                } catch (@NotNull NumberFormatException ignore) {
                    throw new HeaderFormatException("cannot parse '" + value.split("-", 2)[0] + "' as a valid http status code");
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<HttpStatus> header) {
                @NotNull HttpStatus status = header.getValue();
                return status.getCode() + "-" + status.getMessage().replace(" ", "_").toLowerCase();
            }
        }
        private static final class ETagHeaderKey extends HeaderKey<@NotNull EntityTag> {
            private ETagHeaderKey() {
                super("ETag", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<EntityTag> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(EntityTag.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<EntityTag> header) {
                return EntityTag.Parser.serialize(header.getValue());
            }
        }
        private static final class ECTHeaderKey extends HeaderKey<@NotNull EffectiveConnectionType> {
            private ECTHeaderKey() {
                super("ECT", Target.REQUEST);
            }

            @Override
            public @NotNull Header<EffectiveConnectionType> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(EffectiveConnectionType.getById(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<EffectiveConnectionType> header) {
                return header.getValue().getId();
            }
        }
        private static final class EarlyDataHeaderKey extends HeaderKey<@UnknownNullability Void> {
            private EarlyDataHeaderKey() {
                super("Early-Data", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Void> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(null);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Void> header) {
                return "1";
            }
        }
        private static final class DPRHeaderKey extends HeaderKey<@NotNull Float> {
            private DPRHeaderKey() {
                super("DPR", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Float> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Float.parseFloat(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Float> header) {
                return header.getValue().toString();
            }

            @Override
            public @NotNull Header<@NotNull Float> create(@NotNull Float value) {
                if (value <= 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' higher than zero");
                }
                return super.create(value);
            }
        }
        private static final class DownlinkHeaderKey extends HeaderKey<BitMeasure> {
            private DownlinkHeaderKey() {
                super("Downlink", Target.REQUEST);
            }

            @Override
            public @NotNull Header<BitMeasure> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(BitMeasure.create((long) (Double.parseDouble(value) * 1_000_000D)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<BitMeasure> header) {
                return String.valueOf(header.getValue().getBits(BitMeasure.Level.MEGABITS));
            }
        }
        private static final class DNTHeaderKey extends HeaderKey<@NotNull Boolean> {
            private DNTHeaderKey() {
                super("DNT", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Boolean> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(value.trim().equals("1"));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Boolean> header) {
                return header.getValue() ? "1" : "0";
            }
        }
        private static final class DeviceMemoryHeaderKey extends HeaderKey<@NotNull BitMeasure> {
            private DeviceMemoryHeaderKey() {
                super("Device-Memory", Target.REQUEST);
            }

            @Override
            public @NotNull Header<BitMeasure> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                float size = Float.parseFloat(value);

                if (size != 0.25f && size != 0.5f && size != 1f && size != 2f && size != 4f && size != 8f) {
                    throw new HeaderFormatException("the Device-Memory header only accept: 0.25, 0.5, 1, 2, 4 or 8");
                }

                return create(BitMeasure.create(BitMeasure.Level.GIGABYTES, size));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<BitMeasure> header) {
                double giga = header.getValue().getGigabytes();
                float closestValue = Collections.min(Arrays.asList(0.25f, 0.5f, 1f, 2f, 4f, 8f), Comparator.comparingDouble(a -> Math.abs(a - giga)));

                return String.valueOf(closestValue);
            }

            @Override
            public @NotNull Header<@NotNull BitMeasure> create(@NotNull BitMeasure value) {
                if (value.getBytes() < 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' bytes must be positive");
                }
                return super.create(value);
            }
        }
        private static final class DateHeaderKey extends HeaderKey<@NotNull OffsetDateTime> {
            private DateHeaderKey() {
                super("Date", Target.BOTH);
            }

            @Override
            public @NotNull Header<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(DateUtils.RFC822.convert(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<OffsetDateTime> header) {
                return DateUtils.RFC822.convert(header.getValue());
            }
        }
        private static final class CrossOriginResourcePolicyHeaderKey extends HeaderKey<ResourcePolicy> {
            private CrossOriginResourcePolicyHeaderKey() {
                super("Cross-Origin-Resource-Policy", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<ResourcePolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(ResourcePolicy.getById(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<ResourcePolicy> header) {
                return header.getValue().getId();
            }
        }
        private static final class CrossOriginOpenerPolicyHeaderKey extends HeaderKey<OpenerPolicy> {
            private CrossOriginOpenerPolicyHeaderKey() {
                super("Cross-Origin-Opener-Policy", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<OpenerPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(OpenerPolicy.getById(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<OpenerPolicy> header) {
                return header.getValue().getId();
            }
        }
        private static final class CrossOriginEmbedderPolicyHeaderKey extends HeaderKey<EmbedderPolicy> {
            private CrossOriginEmbedderPolicyHeaderKey() {
                super("Cross-Origin-Embedder-Policy", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<EmbedderPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(EmbedderPolicy.getById(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<EmbedderPolicy> header) {
                return header.getValue().getId();
            }
        }
        private static final class CookieHeaderKey extends HeaderKey<@NotNull Cookie @NotNull []> {
            private CookieHeaderKey() {
                super("Cookie", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Cookie[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Pattern pattern = Pattern.compile("\\s*;\\s*");
                @NotNull Matcher matcher = pattern.matcher(value);
                @NotNull List<Cookie> cookies = new LinkedList<>();

                for (@NotNull String cookie : value.split("\\s*;\\s*")) {
                    try {
                        cookies.add(Cookie.Parser.deserialize(cookie));
                    } catch (ParseException e) {
                        throw new HeaderFormatException(e);
                    }
                }

                return create(cookies.toArray(new Cookie[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Cookie[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Cookie cookie : header.getValue()) {
                    if (builder.length() > 0) builder.append("; ");
                    builder.append(Cookie.Parser.serialize(cookie));
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<@NotNull Cookie @NotNull []> create(@NotNull Cookie @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class ContentSecurityPolicyReportOnlyeHeaderKey extends HeaderKey<@NotNull ContentSecurityPolicy> {
            private ContentSecurityPolicyReportOnlyeHeaderKey() {
                super("Content-Security-Policy-Report-Only", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<ContentSecurityPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(ContentSecurityPolicy.parse(value));
                } catch (@NotNull Throwable e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid content security policy", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<ContentSecurityPolicy> header) {
                return header.getValue().toString();
            }
        }
        private static final class ContentSecurityPolicyHeaderKey extends HeaderKey<@NotNull ContentSecurityPolicy> {
            private ContentSecurityPolicyHeaderKey() {
                super("Content-Security-Policy", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<ContentSecurityPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(ContentSecurityPolicy.parse(value));
                } catch (@NotNull Throwable e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid content security policy", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<ContentSecurityPolicy> header) {
                return header.getValue().toString();
            }
        }
        private static final class ContentRangeHeaderKey extends HeaderKey<@NotNull ContentRange> {
            private ContentRangeHeaderKey() {
                super("Content-Range", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<ContentRange> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(ContentRange.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid content range", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<ContentRange> header) {
                return ContentRange.Parser.serialize(header.getValue());
            }
        }
        private static final class ContentLocationHeaderKey extends HeaderKey<@NotNull Origin> {
            private ContentLocationHeaderKey() {
                super("Content-Location", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Origin> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Origin.Parser.deserialize(value));
                } catch (ParseException | UnknownHostException | URISyntaxException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a location", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Origin> header) {
                return Origin.Parser.serialize(header.getValue());
            }
        }
        private static final class ContentEncodingHeaderKey extends HeaderKey<@NotNull Deferred<Encoding> @NotNull []> {
            private ContentEncodingHeaderKey() {
                super("Content-Encoding", Target.BOTH);
            }

            @Override
            public @NotNull Header<Deferred<Encoding>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                //noinspection unchecked
                @NotNull Deferred<Encoding>[] encodings = Arrays.stream(value.split("\\s*,\\s*")).map(Deferred::encoding).toArray(Deferred[]::new);

                return create(encodings);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Deferred<Encoding>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Deferred<Encoding> deferred : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(deferred);
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<@NotNull Deferred<Encoding> @NotNull []> create(@NotNull Deferred<Encoding> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                } else if (!Arrays.stream(value).map(Deferred::toString).allMatch(
                        raw -> raw.equalsIgnoreCase("gzip") || raw.equalsIgnoreCase("X-gzip") ||
                                raw.equalsIgnoreCase("compress") ||
                                raw.equalsIgnoreCase("deflate") ||
                                raw.equalsIgnoreCase("br") ||
                                raw.equalsIgnoreCase("zstd"))) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value only accepts 'gzip'/'x-gzip', 'compress', 'deflate', 'br' or 'zstd' encodings");
                }
                return super.create(value);
            }
        }
        private static final class ContentDispositionHeaderKey extends HeaderKey<@NotNull ContentDisposition> {
            private ContentDispositionHeaderKey() {
                super("Content-Disposition", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<ContentDisposition> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(ContentDisposition.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse content disposition '" + value + "'", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<ContentDisposition> header) {
                return header.getValue().toString();
            }
        }
        private static final class ConnectionHeaderKey extends HeaderKey<@NotNull Connection> {
            private ConnectionHeaderKey() {
                super("Connection", Target.BOTH);
            }

            @Override
            public @NotNull Header<Connection> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Connection.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid connection", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Connection> header) {
                return Connection.Parser.serialize(header.getValue());
            }
        }
        private static final class ClearSiteDataHeaderKey extends HeaderKey<@NotNull Wildcard<@NotNull SiteData @NotNull []>> {
            private ClearSiteDataHeaderKey() {
                super("Clear-Site-Data", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Wildcard<SiteData[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("\"*\"") || value.trim().equals("*")) {
                    return create(Wildcard.create());
                }

                @NotNull Pattern pattern = Pattern.compile("\"(?<id>.*?)\"");
                @NotNull Matcher matcher = pattern.matcher(value);
                @NotNull Set<SiteData> data = new LinkedHashSet<>();

                while (matcher.find()) {
                    data.add(SiteData.getById(matcher.group("id")));
                }

                return create(Wildcard.create(data.toArray(new SiteData[0])));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<SiteData[]>> header) {
                if (header.getValue().isWildcard()) {
                    return "\"*\"";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull SiteData data : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append("\"").append(data.getId()).append("\"");
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull Header<@NotNull Wildcard<@NotNull SiteData @NotNull []>> create(@NotNull Wildcard<@NotNull SiteData @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class CacheControlHeaderKey extends HeaderKey<@NotNull CacheControl> {
            private CacheControlHeaderKey() {
                super("Cache-Control", Target.BOTH);
            }

            @Override
            public @NotNull Header<CacheControl> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(CacheControl.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse cache control '" + value + "'", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<CacheControl> header) {
                return header.getValue().toString();
            }
        }
        private static final class AuthorizationHeaderKey extends HeaderKey<@NotNull Credentials> {
            private AuthorizationHeaderKey() {
                super("Authorization", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Credentials> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull String[] split = value.split(" ", 2);
                @NotNull Credentials credentials;

                try {
                    if (split[0].equalsIgnoreCase("Bearer")) {
                        credentials = new Credentials.Bearer(split[1].toLowerCase());
                    } else if (split[0].equalsIgnoreCase("Basic")) {
                        credentials = Credentials.Basic.parse(split[1]);
                    } else {
                        credentials = Credentials.Unknown.create(value.toCharArray());
                    }
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException("cannot parse credentials '" + value + "'", e);
                }

                return create(credentials);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Credentials> header) {
                return header.getValue().toString();
            }
        }
        private static final class AltUsedHeaderKey extends HeaderKey<@NotNull URIAuthority> {
            private AltUsedHeaderKey() {
                super("Alt-Used", Target.REQUEST);
            }

            @Override
            public @NotNull Header<URIAuthority> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(URIAuthority.parse(value));
                } catch (@NotNull URISyntaxException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid uri authority in header '" + getName() + "'", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<URIAuthority> header) {
                @NotNull URIAuthority authority = header.getValue();
                return authority.getHostName() + ":" + authority.getPort();
            }

            @Override
            public @NotNull Header<@NotNull URIAuthority> create(@NotNull URIAuthority value) {
                return super.create(value);
            }
        }
        private static final class AltSvcHeaderKey extends HeaderKey<@NotNull Optional<@NotNull AlternativeService @NotNull []>> {
            private AltSvcHeaderKey() {
                super("Alt-Svc", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Optional<AlternativeService[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equalsIgnoreCase("clear")) {
                    return create(Optional.empty());
                }

                @NotNull Set<AlternativeService> services = new HashSet<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    try {
                        services.add(AlternativeService.parse(name));
                    } catch (@NotNull ParseException | @NotNull UnknownHostException | @NotNull URISyntaxException e) {
                        throw new HeaderFormatException("cannot parse alternative service '" + name + "'", e);
                    }
                }

                return create(Optional.of(services.toArray(new AlternativeService[0])));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Optional<AlternativeService[]>> header) {
                if (header.getValue().isPresent()) {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull AlternativeService service : header.getValue().get()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(service);
                    }

                    return builder.toString();
                } else {
                    return "clear";
                }
            }

            @Override
            public @NotNull Header<@NotNull Optional<@NotNull AlternativeService @NotNull []>> create(@NotNull Optional<@NotNull AlternativeService @NotNull []> value) {
                if (value.isPresent() && value.get().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AllowHeaderKey extends HeaderKey<@NotNull Method @NotNull []> {
            private AllowHeaderKey() {
                super("Allow", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Method[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Set<Method> methods = new HashSet<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    try {
                        methods.add(Method.valueOf(name.toUpperCase()));
                    } catch (@NotNull IllegalArgumentException ignore) {
                        throw new HeaderFormatException("cannot parse method '" + name + "' from header '" + getName() + "'");
                    }
                }

                return create(methods.toArray(new Method[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Method[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Method method : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(method.name().toLowerCase());
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<@NotNull Method @NotNull []> create(@NotNull Method @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AccessControlRequestMethodHeaderKey extends HeaderKey<@NotNull Method> {
            private AccessControlRequestMethodHeaderKey() {
                super("Access-Control-Request-Method", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Method> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Method.valueOf(value.toUpperCase()));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Method> header) {
                return header.getValue().name();
            }
        }
        private static final class AcceptControlRequestHeadersHeaderKey extends HeaderKey<@NotNull HeaderKey<?> @NotNull []> {
            private AcceptControlRequestHeadersHeaderKey() {
                super("Access-Control-Request-Headers", Target.REQUEST);
            }

            @Override
            public @NotNull Header<HeaderKey<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
                @NotNull Matcher matcher = pattern.matcher(value);

                @NotNull Set<HeaderKey<?>> headers = new HashSet<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    headers.add(HeaderKey.retrieve(name));
                }

                return create(headers.toArray(new HeaderKey[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<HeaderKey<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull HeaderKey<?> h : header.getValue()) {
                    if (builder.length() > 0) builder.append(",");
                    builder.append(h.toString().toLowerCase());
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<@NotNull HeaderKey<?> @NotNull []> create(@NotNull HeaderKey<?> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AcceptControlExposeHeadersHeaderKey extends HeaderKey<@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []>> {
            private AcceptControlExposeHeadersHeaderKey() {
                super("Access-Control-Expose-Headers", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Wildcard<HeaderKey<?> []>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                } else {
                    @NotNull List<HeaderKey<?>> headers = new ArrayList<>();

                    int row = 0;
                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        headers.add(HeaderKey.retrieve(name));
                    }

                    return create(Wildcard.create(headers.toArray(new HeaderKey[0])));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<HeaderKey<?> []>> header) {
                if (header.getValue().isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull HeaderKey<?> key : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(key);
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull Header<@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []>> create(@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AccessControlAllowOriginHeaderKey extends HeaderKey<@NotNull Wildcard<@Nullable URIAuthority>> {
            private AccessControlAllowOriginHeaderKey() {
                super("Access-Control-Allow-Origin", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<@NotNull Wildcard<@Nullable URIAuthority>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                value = value.trim();

                if (value.equals("*")) {
                    return create(Wildcard.create());
                } else if (value.equalsIgnoreCase("null")) {
                    return create(Wildcard.create(null));
                } else try {
                    return create(Wildcard.create(URIAuthority.parse(value)));
                } catch (@NotNull URISyntaxException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid uri authority of '" + getName() + "' header", e);
                }
            }

            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<@NotNull Wildcard<@Nullable URIAuthority>> header) {
                return header.getValue().toString();
            }
        }
        private static final class AccessControlAllowMethodsHeaderKey extends HeaderKey<@NotNull Wildcard<@NotNull Method @NotNull []>> {
            private AccessControlAllowMethodsHeaderKey() {
                super("Access-Control-Allow-Methods", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Wildcard<Method[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                } else {
                    @NotNull List<Method> methods = new ArrayList<>();

                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        try {
                            methods.add(Method.valueOf(name.toUpperCase()));
                        } catch (@NotNull IllegalArgumentException ignore) {
                            throw new HeaderFormatException("cannot parse '" + name + "' as a valid request method");
                        }
                    }

                    return create(Wildcard.create(methods.toArray(new Method[0])));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<Method[]>> header) {
                if (header.getValue().isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull Method method : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(method.name());
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull Header<@NotNull Wildcard<@NotNull Method @NotNull []>> create(@NotNull Wildcard<@NotNull Method @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AcceptControlAllowHeadersHeaderKey extends HeaderKey<@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []>> {
            private AcceptControlAllowHeadersHeaderKey() {
                super("Access-Control-Allow-Headers", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Wildcard<HeaderKey<?>[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                } else {
                    @NotNull List<HeaderKey<?>> headers = new ArrayList<>();

                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        headers.add(HeaderKey.retrieve(name));
                    }

                    return create(Wildcard.create(headers.toArray(new HeaderKey[0])));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<HeaderKey<?>[]>> header) {
                if (header.getValue().isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull HeaderKey<?> key : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(key);
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull Header<@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []>> create(@NotNull Wildcard<@NotNull HeaderKey<?> @NotNull []> value) {
                if (!value.isWildcard()) {
                    if (value.getValue().length == 0) {
                        throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                    } else if (Arrays.stream(value.getValue()).anyMatch(header -> !header.getTarget().isRequests())) {
                        throw new IllegalArgumentException("The header '" + getName() + "' value must contain only request headers");
                    }
                }

                return super.create(value);
            }
        }
        private static final class AcceptRangesHeaderKey extends HeaderKey<@NotNull AcceptRange> {
            private AcceptRangesHeaderKey() {
                super("Accept-Ranges", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<AcceptRange> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(AcceptRange.valueOf(value.toUpperCase()));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<AcceptRange> header) {
                return header.getValue().name().toLowerCase();
            }
        }
        private static final class AcceptPostHeaderKey extends HeaderKey<MediaType. @NotNull Type @NotNull []> {
            private AcceptPostHeaderKey() {
                super("Accept-Post", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<MediaType.Type[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull List<MediaType.Type> types = new ArrayList<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    types.add(MediaType.Type.parse(name));
                }

                return create(types.toArray(new MediaType.Type[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<MediaType.Type[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull MediaType.Type type : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(type);
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<MediaType.@NotNull Type @NotNull []> create(MediaType.@NotNull Type @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AcceptPatchHeaderKey extends HeaderKey<@NotNull MediaType<?> @NotNull []> {
            private AcceptPatchHeaderKey() {
                super("Accept-Patch", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<MediaType<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull List<MediaType<?>> types = new ArrayList<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    try {
                        types.add(MediaType.Parser.deserialize(name));
                    } catch (@NotNull ParseException e) {
                        throw new HeaderFormatException("cannot parse media type '" + name + "'", e);
                    }
                }

                return create(types.toArray(new MediaType[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<MediaType<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull MediaType<?> type : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(type);
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<@NotNull MediaType<?> @NotNull []> create(@NotNull MediaType<?> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }

        private static final class AcceptLanguageHeaderKey extends HeaderKey<@NotNull Wildcard<@NotNull Weight<@NotNull Locale> @NotNull []>> {
            private AcceptLanguageHeaderKey() {
                super("Accept-Language", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Wildcard<Weight<Locale>[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                } else {
                    @NotNull Pattern pattern = Pattern.compile("(?<locale>[^,;\\s]+)(?:\\s*;\\s*q\\s*=\\s*(?<weight>\\d(?:[.,]\\d)?))?");
                    @NotNull Matcher matcher = pattern.matcher(value);

                    @NotNull Set<Weight<Locale>> pairs = new HashSet<>();

                    while (matcher.find()) {
                        @NotNull String locale = matcher.group("locale");
                        @Nullable Float weight = matcher.group("weight") != null ? Float.parseFloat(matcher.group("weight").replace(",", ".")) : null;

                        pairs.add(Weight.create(weight, new Locale(locale)));
                    }

                    //noinspection unchecked
                    return create(Wildcard.create(pairs.toArray(new Weight[0])));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<Weight<Locale>[]>> header) {
                if (header.getValue().isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull Weight<Locale> pseudo : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(pseudo);
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull Header<@NotNull Wildcard<@NotNull Weight<@NotNull Locale> @NotNull []>> create(@NotNull Wildcard<@NotNull Weight<@NotNull Locale> @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AcceptEncodingHeaderKey extends HeaderKey<@NotNull Wildcard<@NotNull Weight<@NotNull Deferred<Encoding>> @NotNull []>> {
            private AcceptEncodingHeaderKey() {
                super("Accept-Encoding", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Wildcard<Weight<Deferred<Encoding>>[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                }

                @NotNull Pattern pattern = Pattern.compile("(?<encoding>[^,;\\s]+)(?:\\s*;\\s*q\\s*=\\s*(?<weight>\\d(?:[.,]\\d)?))?");
                @NotNull Matcher matcher = pattern.matcher(value);

                @NotNull Set<Weight<Deferred<Encoding>>> pairs = new HashSet<>();

                while (matcher.find()) {
                    @NotNull String name = matcher.group("encoding");
                    @Nullable Float weight = matcher.group("weight") != null ? Float.parseFloat(matcher.group("weight").replace(",", ".")) : null;

                    pairs.add(Weight.create(weight, Deferred.encoding(name)));
                }

                //noinspection unchecked
                return create(Wildcard.create(pairs.toArray(new Weight[0])));
            }

            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<Weight<Deferred<Encoding>>[]>> header) {
                if (header.getValue().isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull Weight<Deferred<Encoding>> pseudo : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(pseudo);
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull Header<@NotNull Wildcard<Weight<Deferred<Encoding>> @NotNull []>> create(@NotNull Wildcard<Weight<Deferred<Encoding>> @NotNull []> wildcard) {
                if (!wildcard.isWildcard() && wildcard.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(wildcard);
            }
        }
        private static final class AcceptCharsetHeaderKey extends HeaderKey<@NotNull Weight<@NotNull Deferred<Charset>> @NotNull []> {
            private AcceptCharsetHeaderKey() {
                super("Accept-Charset", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Weight<Deferred<Charset>>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Pattern pattern = Pattern.compile("(?<charset>[^,;\\s]+)(?:\\s*;\\s*q\\s*=\\s*(?<weight>\\d(?:[.,]\\d)?))?");
                @NotNull Matcher matcher = pattern.matcher(value);

                @NotNull Set<Weight<Deferred<Charset>>> pairs = new HashSet<>();

                while (matcher.find()) {
                    @NotNull String charset = matcher.group("charset");
                    @Nullable Float weight = matcher.group("weight") != null ? Float.parseFloat(matcher.group("weight").replace(",", ".")) : null;

                    pairs.add(Weight.create(weight, Deferred.charset(charset)));
                }

                //noinspection unchecked
                return create(pairs.toArray(new Weight[0]));
            }

            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Weight<Deferred<Charset>>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Weight<Deferred<Charset>> pseudo : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(pseudo);
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<Weight<Deferred<Charset>>[]> create(Weight<Deferred<Charset>> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AcceptCHHeaderKey extends HeaderKey<@NotNull HeaderKey<?> @NotNull []> {
            private AcceptCHHeaderKey() {
                super("Accept-CH", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<HeaderKey<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull List<HeaderKey<?>> keys = new ArrayList<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    @NotNull HeaderKey<?> key = HeaderKey.retrieve(name);

                    if (key.getTarget() == Target.RESPONSE) {
                        // Ignore response headers
                        continue;
                    }

                    keys.add(key);
                }

                return create(keys.toArray(new HeaderKey[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<HeaderKey<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull HeaderKey<?> key : header.getValue()) {
                    if (key.getTarget() == Target.RESPONSE) {
                        // Ignore response headers
                        continue;
                    }

                    if (builder.length() > 0) builder.append(", ");
                    builder.append(key.getName());
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<HeaderKey<?>[]> create(HeaderKey<?> @NotNull [] value) {
                if (Arrays.stream(value).anyMatch(key -> !key.isClientHint())) {
                    throw new IllegalArgumentException("the '" + getName() + "' header value only accept client hint headers as value");
                } else if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }

                return super.create(value);
            }
        }
        private static final class AcceptHeaderKey extends HeaderKey<@NotNull MediaType<?> @NotNull []> {
            private AcceptHeaderKey() {
                super("Accept", Target.REQUEST);
            }

            @Override
            public @NotNull Header<MediaType<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Matcher matcher = Pattern.compile("\\s*,\\s*").matcher(value);
                @NotNull List<MediaType<?>> types = new LinkedList<>();

                try {
                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        types.add(MediaType.Parser.deserialize(name));
                    }
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException("cannot parse Accept header's content types", e);
                }

                return create(types.toArray(new MediaType[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<MediaType<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull MediaType<?> type : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(type);
                }

                return builder.toString();
            }

            @Override
            public @NotNull Header<@NotNull MediaType<?> @NotNull []> create(@NotNull MediaType<?> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class ContentTypeHeaderKey extends HeaderKey<@NotNull MediaType<?>> {
            private ContentTypeHeaderKey() {
                super("Content-Type", Target.BOTH);
            }

            @Override
            public @NotNull Header<MediaType<?>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(MediaType.Parser.deserialize(value));
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<MediaType<?>> header) {
                return MediaType.Parser.serialize(header.getValue());
            }
        }
        private static final class TransferEncodingHeaderKey extends HeaderKey<@NotNull Deferred<Encoding> @NotNull []> {
            private TransferEncodingHeaderKey() {
                super("Transfer-Encoding", Target.BOTH);
            }

            @Override
            public @NotNull Header<Deferred<Encoding>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                //noinspection unchecked
                @NotNull Deferred<Encoding>[] encodings = Arrays.stream(value.split("\\s*,\\s*")).map(Deferred::encoding).toArray(Deferred[]::new);

                return create(encodings);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Deferred<Encoding>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Deferred<Encoding> encoding : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(encoding);
                }

                return builder.toString();
            }
        }

        private static class HeaderImpl<T> implements Header<T> {

            private final @NotNull HeaderKey<T> key;
            private final @UnknownNullability T value;

            private HeaderImpl(@NotNull HeaderKey<T> key, @UnknownNullability T value) {
                this.key = key;
                this.value = value;
            }

            // Getters

            @Override
            public @NotNull HeaderKey<T> getKey() {
                return key;
            }
            @Override
            public @UnknownNullability T getValue() {
                return value;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull HeaderImpl<?> header = (HeaderImpl<?>) object;
                return Objects.equals(key, header.key);
            }
            @Override
            public int hashCode() {
                return Objects.hashCode(key);
            }

            @Override
            public @NotNull String toString() {
                return getName() + "=" + getValue();
            }

        }

    }

}
