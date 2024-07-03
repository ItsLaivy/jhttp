package codes.laivy.jhttp.headers;

import codes.laivy.jhttp.authorization.Credentials;
import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.Target;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.headers.HttpHeader.Type;
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

// todo: 26/06/2024 finish all headers mapping (already mapped ±80%)
public abstract class HttpHeaderKey<T> {

    // Static initializers

    public static final @NotNull Pattern NAME_FORMAT_REGEX = Pattern.compile("^[A-Za-z][A-Za-z0-9-]*$");

    public static @NotNull HttpHeaderKey<?> retrieve(@NotNull String name) {
        try {
            @NotNull Field field = HttpHeaderKey.class.getDeclaredField(name.replace("-", "_").toUpperCase());
            field.setAccessible(true);

            if (field.getType() == HttpHeaderKey.class) {
                return (HttpHeaderKey<?>) field.get(null);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
        } catch (Throwable throwable) {
            throw new IllegalStateException("Cannot create header key '" + name + "'");
        }

        return new HttpHeaderKey<String>(name, Target.BOTH) {
            @Override
            public @NotNull HttpHeader<String> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return HttpHeader.create(this, value);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<String> header) {
                return header.getValue();
            }
        };
    }

    // Provided

    public static @NotNull HttpHeaderKey<@NotNull MediaType<?> @NotNull []> ACCEPT = new Provided.AcceptHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull HttpHeaderKey<?> @NotNull []> ACCEPT_CH = new Provided.AcceptCHHeaderKey();
    @Deprecated
    public static @NotNull HttpHeaderKey<@NotNull Duration> ACCEPT_CH_LIFETIME = new Provided.AcceptCHLifetimeHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Weight<@NotNull Deferred<Charset>> @NotNull []> ACCEPT_CHARSET = new Provided.AcceptCharsetHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Wildcard<@NotNull Weight<@NotNull Deferred<Encoding>> @NotNull []>> ACCEPT_ENCODING = new Provided.AcceptEncodingHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Wildcard<@NotNull Weight<@NotNull Locale> @NotNull[]>> ACCEPT_LANGUAGE = new Provided.AcceptLanguageHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull MediaType<?> @NotNull []> ACCEPT_PATCH = new Provided.AcceptPatchHeaderKey();
    public static @NotNull HttpHeaderKey<MediaType. @NotNull Type @NotNull []> ACCEPT_POST = new Provided.AcceptPostHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull AcceptRange> ACCEPT_RANGES = new Provided.AcceptRangesHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Boolean> ACCEPT_CONTROL_ALLOW_CREDENTIALS = new Provided.AcceptControlAllowCredentials();
    public static @NotNull HttpHeaderKey<@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []>> ACCEPT_CONTROL_ALLOW_HEADERS = new Provided.AcceptControlAllowHeadersHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Wildcard<@NotNull Method @NotNull []>> ACCEPT_CONTROL_ALLOW_METHODS = new Provided.AccessControlAllowMethodsHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Wildcard<@Nullable URIAuthority>> ACCEPT_CONTROL_ALLOW_ORIGIN = new Provided.AccessControlAllowOriginHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []>> ACCEPT_CONTROL_EXPOSE_HEADERS = new Provided.AcceptControlExposeHeadersHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Duration> ACCEPT_CONTROL_MAX_AGE = new Provided.AcceptControlMaxAgeHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull HttpHeaderKey<?> @NotNull []> ACCEPT_CONTROL_REQUEST_HEADERS = new Provided.AcceptControlRequestHeadersHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Method> ACCEPT_CONTROL_REQUEST_METHOD = new Provided.AccessControlRequestMethodHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Duration> AGE = new Provided.AgeHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Method @NotNull []> ALLOW = new Provided.AllowHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Optional<@NotNull AlternativeService @NotNull []>> ALT_SVC = new Provided.AltSvcHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull URIAuthority> ALT_USED = new Provided.AltUsedHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull Eligible> ATTRIBUTION_REPORTING_ELIGIBLE = new Provided.AttributionReportingEligibleHeaderKey();
    @ApiStatus.Experimental
    // todo: attribution reporting register source object
    public static @NotNull HttpHeaderKey<@NotNull JsonObject> ATTRIBUTION_REPORTING_REGISTER_SOURCE = new Provided.AttributionReportingRegisterSourceHeaderKey();
    @ApiStatus.Experimental
    // todo: attribution reporting register trigger object
    public static @NotNull HttpHeaderKey<@NotNull JsonObject> ATTRIBUTION_REPORTING_REGISTER_TRIGGER = new Provided.AttributionReportingRegisterTriggerHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Credentials> AUTHORIZATION = new Provided.AuthorizationHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull CacheControl> CACHE_CONTROL = new Provided.CacheControlHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Wildcard<@NotNull SiteData @NotNull []>> CLEAR_SITE_DATA = new Provided.ClearSiteDataHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Connection> CONNECTION = new Provided.ConnectionHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull ContentDisposition> CONTENT_DISPOSITION = new Provided.ContentDispositionHeaderKey();
    @Deprecated
    public static @NotNull HttpHeaderKey<@NotNull Float> CONTENT_DPR = new Provided.ContentDPRHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Deferred<Encoding> @NotNull []> CONTENT_ENCODING = new Provided.ContentEncodingHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Locale @NotNull []> CONTENT_LANGUAGE = new Provided.ContentLanguageHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull BitMeasure> CONTENT_LENGTH = new Provided.ContentLengthHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Origin> CONTENT_LOCATION = new Provided.ContentLocationHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull ContentRange> CONTENT_RANGE = new Provided.ContentRangeHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull ContentSecurityPolicy> CONTENT_SECURITY_POLICY = new Provided.ContentSecurityPolicyHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull ContentSecurityPolicy> CONTENT_SECURITY_POLICY_REPORT_ONLY = new Provided.ContentSecurityPolicyReportOnlyeHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull MediaType<?>> CONTENT_TYPE = new Provided.ContentTypeHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Cookie @NotNull []> COOKIE = new Provided.CookieHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull HttpHeaderKey<?> @NotNull []> CRITICAL_CH = new Provided.CriticalCHHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull EmbedderPolicy> CROSS_ORIGIN_EMBEDDER_POLICY = new Provided.CrossOriginEmbedderPolicyHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull OpenerPolicy> CROSS_ORIGIN_OPENER_POLICY = new Provided.CrossOriginOpenerPolicyHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull ResourcePolicy> CROSS_ORIGIN_RESOURCE_POLICY = new Provided.CrossOriginResourcePolicyHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull OffsetDateTime> DATE = new Provided.DateHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull BitMeasure> DEVICE_MEMORY = new Provided.DeviceMemoryHeaderKey();
    @Deprecated
    public static @NotNull HttpHeaderKey<@NotNull Digest @NotNull []> DIGEST = new Provided.DigestHeaderKey();
    @Deprecated
    public static @NotNull HttpHeaderKey<@NotNull Boolean> DNT = new Provided.DNTHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull BitMeasure> DOWNLINK = new Provided.DownlinkHeaderKey();
    @Deprecated
    public static @NotNull HttpHeaderKey<@NotNull Float> DPR = new Provided.DPRHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@UnknownNullability Void> EARLY_DATA = new Provided.EarlyDataHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull EffectiveConnectionType> ECT = new Provided.ECTHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull EntityTag> ETAG = new Provided.ETagHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull HttpStatus> EXPECT = new Provided.ExpectHeaderKey();
    @Deprecated
    public static @NotNull HttpHeaderKey<@NotNull ExpectCertificate> EXPECT_CT = new Provided.ExpectCTHeaderKey();
    public static @NotNull HttpHeaderKey<@Nullable OffsetDateTime> EXPIRES = new Provided.ExpiresHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Forwarded> FORWARDED = new Provided.ForwardedHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Email> FROM = new Provided.FromHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Host> HOST = new Provided.HostHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> IF_MATCH = new Provided.IfMatchHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull OffsetDateTime> IF_MODIFIED_SINCE = new Provided.IfModifiedSinceHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> IF_NONE_MATCH = new Provided.IfNoneMatchHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull OffsetDateTime> IF_UNMODIFIED_SINCE = new Provided.IfUnmodifiedSinceHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull KeepAlive> KEEP_ALIVE = new Provided.KeepAliveHeaderKey();
    @Deprecated
    public static @NotNull HttpHeaderKey<@NotNull Optional<@NotNull BitMeasure>> LARGE_ALLOCATION = new Provided.LargeAllocationHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull OffsetDateTime> LAST_MODIFIED = new Provided.LastModifiedHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Origin> LOCATION = new Provided.LocationHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Integer> MAX_FORWARDS = new Provided.MaxForwardsHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull NetworkErrorLogging> NEL = new Provided.NetworkErrorLoggingHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> NO_VARY_SEARCH = new Provided.StringHeaderKey("No-Vary-Search", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> OBSERVE_BROWSING_TOPICS = new Provided.StringHeaderKey("Observe-Browsing-Topics", Target.RESPONSE);
    public static @NotNull HttpHeaderKey<@Nullable Host> ORIGIN = new Provided.OriginHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull Boolean> ORIGIN_AGENT_CLUSTER = new Provided.OriginAgentClusterHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> PERMISSIONS_POLICY = new Provided.StringHeaderKey("Permissions-Policy", Target.RESPONSE);
    @Deprecated
    public static @NotNull HttpHeaderKey<@UnknownNullability Void> PRAGMA = new Provided.PragmaHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Credentials> PROXY_AUTHORIZATION = new Provided.ProxyAuthorizationHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> RANGE = new Provided.StringHeaderKey("Range", Target.REQUEST);
    public static @NotNull HttpHeaderKey<@NotNull Origin> REFERER = new Provided.RefererHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> REFERER_POLICY = new Provided.StringHeaderKey("Referrer-Policy", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> REPORTING_ENDPOINTS = new Provided.StringHeaderKey("Reporting-Endpoints", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> REPR_DIGEST = new Provided.StringHeaderKey("Repr-Digest", Target.BOTH);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> RETRY_AFTER = new Provided.StringHeaderKey("Retry-After", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull Duration> RTT = new Provided.RTTHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Boolean> SAVE_DATA = new Provided.SaveDataHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_BROWSING_TOPICS = new Provided.StringHeaderKey("Sec-Browsing-Topics", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_PREFERS_COLOR_SCHEME = new Provided.StringHeaderKey("Sec-CH-Prefers-Color-Scheme", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_PREFERS_REDUCED_MOTION = new Provided.StringHeaderKey("Sec-CH-Prefers-Reduced-Motion", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_PREFERS_REDUCED_TRANSPARENCY = new Provided.StringHeaderKey("Sec-CH-Prefers-Reduced-Transparency", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_UA = new Provided.StringHeaderKey("Sec-CH-UA", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_UA_ARCH = new Provided.StringHeaderKey("Sec-CH-UA-Arch", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_UA_BITNESS = new Provided.StringHeaderKey("Sec-CH-UA-Bitness", Target.REQUEST);
    @Deprecated
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_UA_FULL_VERSION = new Provided.StringHeaderKey("Sec-CH-UA-Full-Version", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_UA_FULL_VERSION_LIST = new Provided.StringHeaderKey("Sec-CH-UA-Full-Version-List", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_UA_MOBILE = new Provided.StringHeaderKey("Sec-CH-UA-Mobile", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_UA_MODEL = new Provided.StringHeaderKey("Sec-CH-UA-Model", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_UA_PLATFORM = new Provided.StringHeaderKey("Sec-CH-UA-Platform", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_CH_UA_PLATFORM_VERSION = new Provided.StringHeaderKey("Sec-CH-UA-Platform-Version", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_FETCH_DEST = new Provided.StringHeaderKey("Sec-Fetch-Dest", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_FETCH_MODE = new Provided.StringHeaderKey("Sec-Fetch-Mode", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_FETCH_SITE = new Provided.StringHeaderKey("Sec-Fetch-Site", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_FETCH_USER = new Provided.StringHeaderKey("Sec-Fetch-User", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_GPC = new Provided.StringHeaderKey("Sec-GPC", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_PURPOSE = new Provided.StringHeaderKey("Sec-Purpose", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SEC_WEBSOCKET_ACCEPT = new Provided.StringHeaderKey("Sec-WebSocket-Accept", Target.RESPONSE);
    public static @NotNull HttpHeaderKey<@NotNull Product> SERVER = new Provided.ServerHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SERVER_TIMING = new Provided.StringHeaderKey("Server-Timing", Target.RESPONSE);
    public static @NotNull HttpHeaderKey<@NotNull String> SERVICE_WORKER_NAVIGATION_PRELOAD = new Provided.StringHeaderKey("Service-Worker-Navigation-Preload", Target.REQUEST);
    public static @NotNull HttpHeaderKey<Cookie. @NotNull Request> SET_COOKIE = new Provided.SetCookieHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SET_LOGIN = new Provided.StringHeaderKey("Set-Login", Target.RESPONSE);
    public static @NotNull HttpHeaderKey<@NotNull Origin> SOURCEMAP = new Provided.SourceMapHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SPECULATION_RULES = new Provided.StringHeaderKey("Speculation-Rules", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> STRICT_TRANSPORT_SECURITY = new Provided.StringHeaderKey("Strict-Transport-Security", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> SUPPORTS_LOADING_MODE = new Provided.StringHeaderKey("Supports-Loading-Mode", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull Weight<@NotNull Deferred<Encoding>> @NotNull []> TE = new Provided.TEHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> TIMING_ALLOW_ORIGIN = new Provided.StringHeaderKey("Timing-Allow-Origin", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> TK = new Provided.StringHeaderKey("Tk", Target.RESPONSE);
    public static @NotNull HttpHeaderKey<@NotNull HttpHeaderKey<?> @NotNull []> TRAILER = new Provided.TrailerHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Deferred<Encoding> @NotNull []> TRANSFER_ENCODING = new Provided.TransferEncodingHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull Upgrade @NotNull []> UPGRADE = new Provided.UpgradeHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> UPGRADE_INSECURE_REQUESTS = new Provided.StringHeaderKey("Upgrade-Insecure-Requests", Target.REQUEST);
    public static @NotNull HttpHeaderKey<@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []>> VARY = new Provided.VaryHeaderKey();
    public static @NotNull HttpHeaderKey<@NotNull UserAgent> USER_AGENT = new Provided.UserAgentHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> VIA = new Provided.StringHeaderKey("Via", Target.BOTH);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> VIEWPORT_WIDTH = new Provided.StringHeaderKey("Viewport-Width", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> WANT_CONTENT_DIGEST = new Provided.StringHeaderKey("Want-Content-Digest", Target.BOTH);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> WANT_DIGEST = new Provided.StringHeaderKey("Want-Digest", Target.BOTH);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> WANT_REPR_DIGEST = new Provided.StringHeaderKey("Want-Repr-Digest", Target.BOTH);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> WARNING = new Provided.StringHeaderKey("Warning", Target.BOTH);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> WIDTH = new Provided.StringHeaderKey("Width", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> WWW_AUTHENTICATE = new Provided.StringHeaderKey("WWW-Authenticate", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> X_CONTENT_TYPE_OPTIONS = new Provided.StringHeaderKey("X-Content-Type-Options", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> X_DNS_PREFETCH_CONTROL = new Provided.StringHeaderKey("X-DNS-Prefetch-Control", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> X_FORWARDED_FOR = new Provided.StringHeaderKey("X-Forwarded-For", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> X_FORWARDED_HOST = new Provided.StringHeaderKey("X-Forwarded-Host", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> X_FORWARDED_PROTO = new Provided.StringHeaderKey("X-Forwarded-Proto", Target.REQUEST);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> X_FRAME_OPTIONS = new Provided.StringHeaderKey("X-Frame-Options", Target.RESPONSE);
    @ApiStatus.Experimental
    public static @NotNull HttpHeaderKey<@NotNull String> X_XSS_PROTECTION = new Provided.StringHeaderKey("X-XSS-Protection", Target.RESPONSE);

    // Object

    private final @NotNull String name;
    private final @NotNull Target target;
    private final @NotNull Type[] types;

    protected HttpHeaderKey(@NotNull String name, @NotNull Target target) {
        this.name = name;
        this.target = target;
        this.types = Arrays.stream(Type.values()).filter(type -> type.matches(this)).toArray(Type[]::new);

        if (name.isEmpty()) {
            throw new IllegalArgumentException("header name cannot be null");
        } else if (!NAME_FORMAT_REGEX.matcher(name).matches()) {
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

    public abstract @NotNull HttpHeader<T> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException;
    public abstract @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<T> header);

    public @NotNull HttpHeader<T> create(@UnknownNullability T value) {
        return new Provided.HeaderImpl<>(this, value);
    }

    // Implementations

    @Override
    public final boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof HttpHeaderKey)) return false;
        @NotNull HttpHeaderKey<?> that = (codes.laivy.jhttp.headers.HttpHeaderKey<?>) object;
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

        private static final class StringHeaderKey extends HttpHeaderKey<@NotNull String> {
            private StringHeaderKey(@NotNull String name, @NotNull Target target) {
                super(name, target);
            }

            @Override
            public @NotNull HttpHeader<@NotNull String> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(value);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<@NotNull String> header) {
                return header.getValue();
            }
        }
        private static final class DigestHeaderKey extends HttpHeaderKey<@NotNull Digest @NotNull []> {
            private DigestHeaderKey() {
                super("Digest", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<@NotNull Digest[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    @NotNull Set<Digest> digests = new HashSet<>();

                    for (@NotNull String string : value.split("\\s*,\\s*")) {
                        digests.add(Digest.Parser.deserialize(string));
                    }

                    return create(digests.toArray(new Digest[0]));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<@NotNull Digest[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Digest digest : header.getValue()) {
                    if (builder.length() > 0) builder.append(",");
                    builder.append(Digest.Parser.serialize(digest));
                }

                return builder.toString();
            }
        }
        private static final class AttributionReportingRegisterTriggerHeaderKey extends HttpHeaderKey<@NotNull JsonObject> {
            private AttributionReportingRegisterTriggerHeaderKey() {
                super("Attribution-Reporting-Register-Trigger", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<@NotNull JsonObject> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(JsonParser.parseString(value).getAsJsonObject());
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<@NotNull JsonObject> header) {
                return header.getValue().toString();
            }
        }
        private static final class AttributionReportingRegisterSourceHeaderKey extends HttpHeaderKey<@NotNull JsonObject> {
            private AttributionReportingRegisterSourceHeaderKey() {
                super("Attribution-Reporting-Register-Source", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<@NotNull JsonObject> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(JsonParser.parseString(value).getAsJsonObject());
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<@NotNull JsonObject> header) {
                return header.getValue().toString();
            }
        }
        private static final class AttributionReportingEligibleHeaderKey extends HttpHeaderKey<@NotNull Eligible> {
            private AttributionReportingEligibleHeaderKey() {
                super("Attribution-Reporting-Eligible", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<@NotNull Eligible> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Eligible.getById(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<@NotNull Eligible> header) {
                return header.getValue().getId();
            }
        }
        private static final class UpgradeHeaderKey extends HttpHeaderKey<@NotNull Upgrade @NotNull []> {
            private UpgradeHeaderKey() {
                super("Upgrade", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<@NotNull Upgrade[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    @NotNull List<Upgrade> upgrade = new LinkedList<>();

                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        upgrade.add(Upgrade.Parser.deserialize(name));
                    }

                    return create(upgrade.toArray(new Upgrade[0]));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<@NotNull Upgrade[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Upgrade upgrade : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(Upgrade.Parser.serialize(upgrade));
                }

                return builder.toString();
            }
        }
        private static final class AcceptControlAllowCredentials extends HttpHeaderKey<@NotNull Boolean> {
            private AcceptControlAllowCredentials() {
                super("Access-Control-Allow-Credentials", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Boolean> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Boolean.parseBoolean(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Boolean> header) {
                return header.getValue().toString();
            }
        }
        private static final class UserAgentHeaderKey extends HttpHeaderKey<@NotNull UserAgent> {
            private UserAgentHeaderKey() {
                super("User-Agent", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<UserAgent> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(UserAgent.Parser.deserialize(value));
                } catch (@NotNull Throwable throwable) {
                    throw new HeaderFormatException("cannot parse '" + value + "' as a valid user agent header", throwable);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<UserAgent> header) {
                return UserAgent.Parser.serialize(header.getValue());
            }
        }
        private static final class VaryHeaderKey extends HttpHeaderKey<@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []>> {
            private VaryHeaderKey() {
                super("Vary", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Wildcard<HttpHeaderKey<?>[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                } else {
                    @NotNull Set<HttpHeaderKey<?>> keys = new HashSet<>();

                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        keys.add(HttpHeaderKey.retrieve(name));
                    }

                    return create(Wildcard.create(keys.toArray(new HttpHeaderKey[0])));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Wildcard<HttpHeaderKey<?>[]>> header) {
                if (header.getValue().isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull HttpHeaderKey<?> key : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(key);
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull HttpHeader<@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []>> create(@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> value) {
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
        private static final class TrailerHeaderKey extends HttpHeaderKey<@NotNull HttpHeaderKey<?> @NotNull []> {
            private TrailerHeaderKey() {
                super("Trailer", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<HttpHeaderKey<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Set<HttpHeaderKey<?>> keys = new HashSet<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    @NotNull HttpHeaderKey<?> key = HttpHeaderKey.retrieve(name);

                    if (key.getTarget() == Target.RESPONSE) {
                        // Ignore response headers
                        continue;
                    }

                    keys.add(key);
                }

                return create(keys.toArray(new HttpHeaderKey[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<HttpHeaderKey<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull HttpHeaderKey<?> key : header.getValue()) {
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
            public @NotNull HttpHeader<HttpHeaderKey<?>[]> create(HttpHeaderKey<?> @NotNull [] value) {
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
        private static final class TEHeaderKey extends HttpHeaderKey<@NotNull Weight< @NotNull Deferred<Encoding>> @NotNull []> {
            private TEHeaderKey() {
                super("TE", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Weight<Deferred<Encoding>>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Weight<Deferred<Encoding>>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Weight<Deferred<Encoding>> pseudo : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(pseudo);
                }

                return builder.toString();
            }
        }
        private static final class SourceMapHeaderKey extends HttpHeaderKey<@NotNull Origin> {
            private SourceMapHeaderKey() {
                super("SourceMap", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Origin> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Origin.Parser.deserialize(value));
                } catch (ParseException | UnknownHostException | URISyntaxException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Origin> header) {
                return Origin.Parser.serialize(header.getValue());
            }
        }
        private static final class SaveDataHeaderKey extends HttpHeaderKey<@NotNull Boolean> {
            private SaveDataHeaderKey() {
                super("Save-Data", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Boolean> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(value.equalsIgnoreCase("on"));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Boolean> header) {
                return header.getValue() ? "on" : "off";
            }
        }
        private static final class RTTHeaderKey extends HttpHeaderKey<@NotNull Duration> {
            private RTTHeaderKey() {
                super("RTT", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Duration> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Duration.ofMillis(Long.parseLong(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Duration> header) {
                return String.valueOf(header.getValue().toMillis());
            }
        }
        private static final class RefererHeaderKey extends HttpHeaderKey<@NotNull Origin> {
            private RefererHeaderKey() {
                super("Referer", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Origin> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Origin.Parser.deserialize(value));
                } catch (ParseException | UnknownHostException | URISyntaxException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Origin> header) {
                return Origin.Parser.serialize(header.getValue());
            }
        }
        private static final class ProxyAuthorizationHeaderKey extends HttpHeaderKey<@NotNull Credentials> {
            private ProxyAuthorizationHeaderKey() {
                super("Proxy-Authorization", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Credentials> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Credentials> header) {
                return header.getValue().toString();
            }
        }
        private static final class PragmaHeaderKey extends HttpHeaderKey<@UnknownNullability Void> {
            private PragmaHeaderKey() {
                super("Pragma", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Void> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create((Void) null);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Void> header) {
                return "no-cache";
            }
        }
        private static final class OriginAgentClusterHeaderKey extends HttpHeaderKey<@NotNull Boolean> {
            private OriginAgentClusterHeaderKey() {
                super("Origin-Agent-Cluster", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Boolean> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.equalsIgnoreCase("?1")) {
                    return create(true);
                } else if (value.equalsIgnoreCase("?0")) {
                    return create(false);
                } else {
                    return create(Boolean.parseBoolean(value));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Boolean> header) {
                return header.getValue().toString();
            }
        }
        private static final class OriginHeaderKey extends HttpHeaderKey<@Nullable Host> {
            private OriginHeaderKey() {
                super("Origin", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<@Nullable Host> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equalsIgnoreCase("null")) {
                    return create(null);
                } else try {
                    return create(Host.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<@Nullable Host> header) {
                return header.getValue() != null ? header.getValue().toString() : "null";
            }
        }
        private static final class NetworkErrorLoggingHeaderKey extends HttpHeaderKey<@NotNull NetworkErrorLogging> {
            private NetworkErrorLoggingHeaderKey() {
                super("NEL", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<NetworkErrorLogging> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(NetworkErrorLogging.Parser.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<NetworkErrorLogging> header) {
                return NetworkErrorLogging.Parser.serialize(header.getValue());
            }
        }
        private static final class ExpectCTHeaderKey extends HttpHeaderKey<@NotNull ExpectCertificate> {
            private ExpectCTHeaderKey() {
                super("Expect-CT", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<ExpectCertificate> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(ExpectCertificate.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<ExpectCertificate> header) {
                return ExpectCertificate.Parser.serialize(header.getValue());
            }
        }
        private static final class CriticalCHHeaderKey extends HttpHeaderKey<@NotNull HttpHeaderKey<?> @NotNull []> {
            private CriticalCHHeaderKey() {
                super("Critical-CH", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<HttpHeaderKey<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Set<HttpHeaderKey<?>> keys = new HashSet<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    @NotNull HttpHeaderKey<?> key = HttpHeaderKey.retrieve(name);

                    if (key.getTarget() == Target.RESPONSE) {
                        // Ignore response headers
                        continue;
                    }

                    keys.add(key);
                }

                return create(keys.toArray(new HttpHeaderKey[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<HttpHeaderKey<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull HttpHeaderKey<?> key : header.getValue()) {
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
            public @NotNull HttpHeader<HttpHeaderKey<?>[]> create(HttpHeaderKey<?> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                } else if (Arrays.stream(value).anyMatch(key -> !key.isClientHint())) {
                    throw new IllegalArgumentException("the '" + getName() + "' header value only accept client hint headers as value");
                }

                return super.create(value);
            }
        }
        private static final class ContentLengthHeaderKey extends HttpHeaderKey<@NotNull BitMeasure> {
            private ContentLengthHeaderKey() {
                super("Content-Length", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<BitMeasure> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(BitMeasure.create(BitMeasure.Level.BYTES, Double.parseDouble(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<BitMeasure> header) {
                return String.valueOf(header.getValue().getBytes());
            }

            @Override
            public @NotNull HttpHeader<@NotNull BitMeasure> create(@NotNull BitMeasure value) {
                if (value.getBytes() < 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must be higher or equal to zero");
                }
                return super.create(value);
            }
        }
        private static final class ContentLanguageHeaderKey extends HttpHeaderKey<@NotNull Locale @NotNull []> {
            private ContentLanguageHeaderKey() {
                super("Content-Language", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<Locale[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull List<Locale> locales = new ArrayList<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    locales.add(new Locale(name));
                }

                return create(locales.toArray(new Locale[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Locale[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Locale locale : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(locale);
                }

                return builder.toString();
            }

            @Override
            public @NotNull HttpHeader<@NotNull Locale @NotNull []> create(@NotNull Locale @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class ContentDPRHeaderKey extends HttpHeaderKey<@NotNull Float> {
            private ContentDPRHeaderKey() {
                super("Content-DPR", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Float> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Float.parseFloat(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Float> header) {
                return String.valueOf(header.getValue());
            }

            @Override
            public @NotNull HttpHeader<@NotNull Float> create(@NotNull Float value) {
                if (value <= 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' higher than zero");
                }
                return super.create(value);
            }
        }
        private static final class AcceptCHLifetimeHeaderKey extends HttpHeaderKey<@NotNull Duration> {
            private AcceptCHLifetimeHeaderKey() {
                super("Accept-CH-Lifetime", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Duration> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Duration.ofSeconds(Integer.parseInt(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Duration> header) {
                return String.valueOf(header.getValue().getSeconds());
            }
        }
        private static final class AcceptControlMaxAgeHeaderKey extends HttpHeaderKey<@NotNull Duration> {
            private AcceptControlMaxAgeHeaderKey() {
                super("Access-Control-Max-Age", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Duration> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Duration.ofSeconds(Integer.parseInt(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Duration> header) {
                return String.valueOf(header.getValue().getSeconds());
            }

            @Override
            public @NotNull HttpHeader<@NotNull Duration> create(@NotNull Duration value) {
                if (value.isNegative()) {
                    throw new IllegalArgumentException("the header '" + getName() + "' duration value cannot be negative");
                }
                return super.create(value);
            }
        }
        private static final class AgeHeaderKey extends HttpHeaderKey<@NotNull Duration> {
            private AgeHeaderKey() {
                super("Age", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Duration> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Duration.ofSeconds(Integer.parseInt(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Duration> header) {
                return String.valueOf(header.getValue().getSeconds());
            }

            @Override
            public @NotNull HttpHeader<@NotNull Duration> create(@NotNull Duration value) {
                if (value.isNegative()) {
                    throw new IllegalArgumentException("the header '" + getName() + "' duration value cannot be negative");
                }
                return super.create(value);
            }
        }
        private static final class ServerHeaderKey extends HttpHeaderKey<@NotNull Product> {
            private ServerHeaderKey() {
                super("Server", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Product> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return HttpHeader.create(this, Product.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' as a valid product", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Product> header) {
                return header.getValue().toString();
            }
        }
        private static final class SetCookieHeaderKey extends HttpHeaderKey<Cookie. @NotNull Request> {
            private SetCookieHeaderKey() {
                super("Set-Cookie", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Cookie.Request> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Cookie.Request.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Cookie.Request> header) {
                return Cookie.Request.Parser.serialize(header.getValue());
            }
        }
        private static final class MaxForwardsHeaderKey extends HttpHeaderKey<@NotNull Integer> {
            private MaxForwardsHeaderKey() {
                super("Max-Forwards", Target.REQUEST);
            }

            @Override
            public @NotNull codes.laivy.jhttp.headers.HttpHeader<Integer> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Integer.parseInt(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Integer> header) {
                return String.valueOf(header.getValue());
            }
        }
        private static final class LocationHeaderKey extends HttpHeaderKey<@NotNull Origin> {
            private LocationHeaderKey() {
                super("Location", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Origin> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Origin.Parser.deserialize(value));
                } catch (ParseException | UnknownHostException | URISyntaxException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Origin> header) {
                return Origin.Parser.serialize(header.getValue());
            }
        }
        private static final class LastModifiedHeaderKey extends HttpHeaderKey<@NotNull OffsetDateTime> {
            private LastModifiedHeaderKey() {
                super("Last-Modified", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(DateUtils.RFC822.convert(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<OffsetDateTime> header) {
                return DateUtils.RFC822.convert(header.getValue());
            }
        }
        private static final class LargeAllocationHeaderKey extends HttpHeaderKey<@NotNull Optional<@NotNull BitMeasure>> {
            private LargeAllocationHeaderKey() {
                super("Large-Allocation", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Optional<BitMeasure>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Optional<BitMeasure>> header) {
                @NotNull Optional<BitMeasure> optional = header.getValue();
                return optional.map(bitMeasure -> String.valueOf((int) bitMeasure.getMegabytes())).orElse("0");
            }
        }
        private static final class KeepAliveHeaderKey extends HttpHeaderKey<@NotNull KeepAlive> {
            private KeepAliveHeaderKey() {
                super("Keep-Alive", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<KeepAlive> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(KeepAlive.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<KeepAlive> header) {
                return KeepAlive.Parser.serialize(header.getValue());
            }
        }
        private static final class IfUnmodifiedSinceHeaderKey extends HttpHeaderKey<@NotNull OffsetDateTime> {
            private IfUnmodifiedSinceHeaderKey() {
                super("If-Unmodified-Since", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(DateUtils.RFC822.convert(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<OffsetDateTime> header) {
                return DateUtils.RFC822.convert(header.getValue());
            }
        }
        private static final class IfNoneMatchHeaderKey extends HttpHeaderKey<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> {
            private IfNoneMatchHeaderKey() {
                super("If-None-Match", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Wildcard<EntityTag[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Wildcard<EntityTag[]>> header) {
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
            public @NotNull HttpHeader<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> create(@NotNull Wildcard<@NotNull EntityTag @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class IfModifiedSinceHeaderKey extends HttpHeaderKey<@NotNull OffsetDateTime> {
            private IfModifiedSinceHeaderKey() {
                super("If-Modified-Since", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(DateUtils.RFC822.convert(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<OffsetDateTime> header) {
                return DateUtils.RFC822.convert(header.getValue());
            }
        }
        private static final class IfMatchHeaderKey extends HttpHeaderKey<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> {
            private IfMatchHeaderKey() {
                super("If-Match", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Wildcard<EntityTag[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Wildcard<EntityTag[]>> header) {
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
            public @NotNull HttpHeader<@NotNull Wildcard<@NotNull EntityTag @NotNull []>> create(@NotNull Wildcard<@NotNull EntityTag @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class HostHeaderKey extends HttpHeaderKey<@NotNull Host> {
            private HostHeaderKey() {
                super("Host", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Host> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Host.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Host> header) {
                return header.getValue().toString();
            }
        }
        private static final class FromHeaderKey extends HttpHeaderKey<@NotNull Email> {
            private FromHeaderKey() {
                super("From", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Email> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Email.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Email> header) {
                return Email.Parser.serialize(header.getValue());
            }
        }
        private static final class ForwardedHeaderKey extends HttpHeaderKey<@NotNull Forwarded> {
            private ForwardedHeaderKey() {
                super("Forwarded", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Forwarded> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Forwarded.Parser.deserialize(value));
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Forwarded> header) {
                return Forwarded.Parser.serialize(header.getValue());
            }
        }
        private static final class ExpiresHeaderKey extends HttpHeaderKey<@Nullable OffsetDateTime> {
            private ExpiresHeaderKey() {
                super("Expires", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<OffsetDateTime> header) {
                if (header.getValue() == null || header.getValue().isBefore(OffsetDateTime.now())) {
                    return "0";
                } else {
                    return DateUtils.RFC822.convert(header.getValue());
                }
            }
        }
        private static final class ExpectHeaderKey extends HttpHeaderKey<@NotNull HttpStatus> {
            private ExpectHeaderKey() {
                super("Expect", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<HttpStatus> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    int code = Integer.parseInt(value.split("-", 2)[0]);
                    return create(HttpStatus.getByCode(code));
                } catch (@NotNull NumberFormatException ignore) {
                    throw new HeaderFormatException("cannot parse '" + value.split("-", 2)[0] + "' as a valid http status code");
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<HttpStatus> header) {
                @NotNull HttpStatus status = header.getValue();
                return status.getCode() + "-" + status.getMessage().replace(" ", "_").toLowerCase();
            }
        }
        private static final class ETagHeaderKey extends HttpHeaderKey<@NotNull EntityTag> {
            private ETagHeaderKey() {
                super("ETag", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<EntityTag> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(EntityTag.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<EntityTag> header) {
                return EntityTag.Parser.serialize(header.getValue());
            }
        }
        private static final class ECTHeaderKey extends HttpHeaderKey<@NotNull EffectiveConnectionType> {
            private ECTHeaderKey() {
                super("ECT", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<EffectiveConnectionType> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(EffectiveConnectionType.getById(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<EffectiveConnectionType> header) {
                return header.getValue().getId();
            }
        }
        private static final class EarlyDataHeaderKey extends HttpHeaderKey<@UnknownNullability Void> {
            private EarlyDataHeaderKey() {
                super("Early-Data", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Void> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(null);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Void> header) {
                return "1";
            }
        }
        private static final class DPRHeaderKey extends HttpHeaderKey<@NotNull Float> {
            private DPRHeaderKey() {
                super("DPR", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Float> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Float.parseFloat(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Float> header) {
                return header.getValue().toString();
            }

            @Override
            public @NotNull HttpHeader<@NotNull Float> create(@NotNull Float value) {
                if (value <= 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' higher than zero");
                }
                return super.create(value);
            }
        }
        private static final class DownlinkHeaderKey extends HttpHeaderKey<BitMeasure> {
            private DownlinkHeaderKey() {
                super("Downlink", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<BitMeasure> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(BitMeasure.create((long) (Double.parseDouble(value) * 1_000_000D)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<BitMeasure> header) {
                return String.valueOf(header.getValue().getBits(BitMeasure.Level.MEGABITS));
            }
        }
        private static final class DNTHeaderKey extends HttpHeaderKey<@NotNull Boolean> {
            private DNTHeaderKey() {
                super("DNT", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Boolean> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(value.trim().equals("1"));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Boolean> header) {
                return header.getValue() ? "1" : "0";
            }
        }
        private static final class DeviceMemoryHeaderKey extends HttpHeaderKey<@NotNull BitMeasure> {
            private DeviceMemoryHeaderKey() {
                super("Device-Memory", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<BitMeasure> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                float size = Float.parseFloat(value);

                if (size != 0.25f && size != 0.5f && size != 1f && size != 2f && size != 4f && size != 8f) {
                    throw new HeaderFormatException("the Device-Memory header only accept: 0.25, 0.5, 1, 2, 4 or 8");
                }

                return create(BitMeasure.create(BitMeasure.Level.GIGABYTES, size));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<BitMeasure> header) {
                double giga = header.getValue().getGigabytes();
                float closestValue = Collections.min(Arrays.asList(0.25f, 0.5f, 1f, 2f, 4f, 8f), Comparator.comparingDouble(a -> Math.abs(a - giga)));

                return String.valueOf(closestValue);
            }

            @Override
            public @NotNull HttpHeader<@NotNull BitMeasure> create(@NotNull BitMeasure value) {
                if (value.getBytes() < 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' bytes must be positive");
                }
                return super.create(value);
            }
        }
        private static final class DateHeaderKey extends HttpHeaderKey<@NotNull OffsetDateTime> {
            private DateHeaderKey() {
                super("Date", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(DateUtils.RFC822.convert(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<OffsetDateTime> header) {
                return DateUtils.RFC822.convert(header.getValue());
            }
        }
        private static final class CrossOriginResourcePolicyHeaderKey extends HttpHeaderKey<ResourcePolicy> {
            private CrossOriginResourcePolicyHeaderKey() {
                super("Cross-Origin-Resource-Policy", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<ResourcePolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(ResourcePolicy.getById(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<ResourcePolicy> header) {
                return header.getValue().getId();
            }
        }
        private static final class CrossOriginOpenerPolicyHeaderKey extends HttpHeaderKey<OpenerPolicy> {
            private CrossOriginOpenerPolicyHeaderKey() {
                super("Cross-Origin-Opener-Policy", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<OpenerPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(OpenerPolicy.getById(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<OpenerPolicy> header) {
                return header.getValue().getId();
            }
        }
        private static final class CrossOriginEmbedderPolicyHeaderKey extends HttpHeaderKey<EmbedderPolicy> {
            private CrossOriginEmbedderPolicyHeaderKey() {
                super("Cross-Origin-Embedder-Policy", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<EmbedderPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(EmbedderPolicy.getById(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<EmbedderPolicy> header) {
                return header.getValue().getId();
            }
        }
        private static final class CookieHeaderKey extends HttpHeaderKey<@NotNull Cookie @NotNull []> {
            private CookieHeaderKey() {
                super("Cookie", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Cookie[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Cookie[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Cookie cookie : header.getValue()) {
                    if (builder.length() > 0) builder.append("; ");
                    builder.append(Cookie.Parser.serialize(cookie));
                }

                return builder.toString();
            }

            @Override
            public @NotNull HttpHeader<@NotNull Cookie @NotNull []> create(@NotNull Cookie @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class ContentSecurityPolicyReportOnlyeHeaderKey extends HttpHeaderKey<@NotNull ContentSecurityPolicy> {
            private ContentSecurityPolicyReportOnlyeHeaderKey() {
                super("Content-Security-Policy-Report-Only", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<ContentSecurityPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(ContentSecurityPolicy.parse(value));
                } catch (@NotNull Throwable e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid content security policy", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<ContentSecurityPolicy> header) {
                return header.getValue().toString();
            }
        }
        private static final class ContentSecurityPolicyHeaderKey extends HttpHeaderKey<@NotNull ContentSecurityPolicy> {
            private ContentSecurityPolicyHeaderKey() {
                super("Content-Security-Policy", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<ContentSecurityPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(ContentSecurityPolicy.parse(value));
                } catch (@NotNull Throwable e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid content security policy", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<ContentSecurityPolicy> header) {
                return header.getValue().toString();
            }
        }
        private static final class ContentRangeHeaderKey extends HttpHeaderKey<@NotNull ContentRange> {
            private ContentRangeHeaderKey() {
                super("Content-Range", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<ContentRange> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(ContentRange.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid content range", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<ContentRange> header) {
                return ContentRange.Parser.serialize(header.getValue());
            }
        }
        private static final class ContentLocationHeaderKey extends HttpHeaderKey<@NotNull Origin> {
            private ContentLocationHeaderKey() {
                super("Content-Location", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Origin> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Origin.Parser.deserialize(value));
                } catch (ParseException | UnknownHostException | URISyntaxException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a location", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Origin> header) {
                return Origin.Parser.serialize(header.getValue());
            }
        }
        private static final class ContentEncodingHeaderKey extends HttpHeaderKey<@NotNull Deferred<Encoding> @NotNull []> {
            private ContentEncodingHeaderKey() {
                super("Content-Encoding", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<Deferred<Encoding>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                //noinspection unchecked
                @NotNull Deferred<Encoding>[] encodings = Arrays.stream(value.split("\\s*,\\s*")).map(Deferred::encoding).toArray(Deferred[]::new);

                return create(encodings);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Deferred<Encoding>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Deferred<Encoding> deferred : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(deferred);
                }

                return builder.toString();
            }

            @Override
            public @NotNull HttpHeader<@NotNull Deferred<Encoding> @NotNull []> create(@NotNull Deferred<Encoding> @NotNull [] value) {
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
        private static final class ContentDispositionHeaderKey extends HttpHeaderKey<@NotNull ContentDisposition> {
            private ContentDispositionHeaderKey() {
                super("Content-Disposition", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<ContentDisposition> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(ContentDisposition.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse content disposition '" + value + "'", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<ContentDisposition> header) {
                return header.getValue().toString();
            }
        }
        private static final class ConnectionHeaderKey extends HttpHeaderKey<@NotNull Connection> {
            private ConnectionHeaderKey() {
                super("Connection", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<Connection> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(Connection.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid connection", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Connection> header) {
                return Connection.Parser.serialize(header.getValue());
            }
        }
        private static final class ClearSiteDataHeaderKey extends HttpHeaderKey<@NotNull Wildcard<@NotNull SiteData @NotNull []>> {
            private ClearSiteDataHeaderKey() {
                super("Clear-Site-Data", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Wildcard<SiteData[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Wildcard<SiteData[]>> header) {
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
            public @NotNull HttpHeader<@NotNull Wildcard<@NotNull SiteData @NotNull []>> create(@NotNull Wildcard<@NotNull SiteData @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class CacheControlHeaderKey extends HttpHeaderKey<@NotNull CacheControl> {
            private CacheControlHeaderKey() {
                super("Cache-Control", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<CacheControl> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(CacheControl.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse cache control '" + value + "'", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<CacheControl> header) {
                return header.getValue().toString();
            }
        }
        private static final class AuthorizationHeaderKey extends HttpHeaderKey<@NotNull Credentials> {
            private AuthorizationHeaderKey() {
                super("Authorization", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Credentials> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Credentials> header) {
                return header.getValue().toString();
            }
        }
        private static final class AltUsedHeaderKey extends HttpHeaderKey<@NotNull URIAuthority> {
            private AltUsedHeaderKey() {
                super("Alt-Used", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<URIAuthority> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(URIAuthority.parse(value));
                } catch (@NotNull URISyntaxException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid uri authority in header '" + getName() + "'", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<URIAuthority> header) {
                @NotNull URIAuthority authority = header.getValue();
                return authority.getHostName() + ":" + authority.getPort();
            }

            @Override
            public @NotNull HttpHeader<@NotNull URIAuthority> create(@NotNull URIAuthority value) {
                return super.create(value);
            }
        }
        private static final class AltSvcHeaderKey extends HttpHeaderKey<@NotNull Optional<@NotNull AlternativeService @NotNull []>> {
            private AltSvcHeaderKey() {
                super("Alt-Svc", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Optional<AlternativeService[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Optional<AlternativeService[]>> header) {
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
            public @NotNull HttpHeader<@NotNull Optional<@NotNull AlternativeService @NotNull []>> create(@NotNull Optional<@NotNull AlternativeService @NotNull []> value) {
                if (value.isPresent() && value.get().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AllowHeaderKey extends HttpHeaderKey<@NotNull Method @NotNull []> {
            private AllowHeaderKey() {
                super("Allow", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Method[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Method[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Method method : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(method.name().toLowerCase());
                }

                return builder.toString();
            }

            @Override
            public @NotNull HttpHeader<@NotNull Method @NotNull []> create(@NotNull Method @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AccessControlRequestMethodHeaderKey extends HttpHeaderKey<@NotNull Method> {
            private AccessControlRequestMethodHeaderKey() {
                super("Access-Control-Request-Method", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Method> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Method.valueOf(value.toUpperCase()));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Method> header) {
                return header.getValue().name();
            }
        }
        private static final class AcceptControlRequestHeadersHeaderKey extends HttpHeaderKey<@NotNull HttpHeaderKey<?> @NotNull []> {
            private AcceptControlRequestHeadersHeaderKey() {
                super("Access-Control-Request-Headers", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<HttpHeaderKey<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
                @NotNull Matcher matcher = pattern.matcher(value);

                @NotNull Set<HttpHeaderKey<?>> headers = new HashSet<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    headers.add(HttpHeaderKey.retrieve(name));
                }

                return create(headers.toArray(new HttpHeaderKey[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<HttpHeaderKey<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull HttpHeaderKey<?> h : header.getValue()) {
                    if (builder.length() > 0) builder.append(",");
                    builder.append(h.toString().toLowerCase());
                }

                return builder.toString();
            }

            @Override
            public @NotNull HttpHeader<@NotNull HttpHeaderKey<?> @NotNull []> create(@NotNull HttpHeaderKey<?> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AcceptControlExposeHeadersHeaderKey extends HttpHeaderKey<@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []>> {
            private AcceptControlExposeHeadersHeaderKey() {
                super("Access-Control-Expose-Headers", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Wildcard<HttpHeaderKey<?> []>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                } else {
                    @NotNull List<HttpHeaderKey<?>> headers = new ArrayList<>();

                    int row = 0;
                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        headers.add(HttpHeaderKey.retrieve(name));
                    }

                    return create(Wildcard.create(headers.toArray(new HttpHeaderKey[0])));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Wildcard<HttpHeaderKey<?> []>> header) {
                if (header.getValue().isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull HttpHeaderKey<?> key : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(key);
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull HttpHeader<@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []>> create(@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AccessControlAllowOriginHeaderKey extends HttpHeaderKey<@NotNull Wildcard<@Nullable URIAuthority>> {
            private AccessControlAllowOriginHeaderKey() {
                super("Access-Control-Allow-Origin", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<@NotNull Wildcard<@Nullable URIAuthority>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<@NotNull Wildcard<@Nullable URIAuthority>> header) {
                return header.getValue().toString();
            }
        }
        private static final class AccessControlAllowMethodsHeaderKey extends HttpHeaderKey<@NotNull Wildcard<@NotNull Method @NotNull []>> {
            private AccessControlAllowMethodsHeaderKey() {
                super("Access-Control-Allow-Methods", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Wildcard<Method[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Wildcard<Method[]>> header) {
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
            public @NotNull HttpHeader<@NotNull Wildcard<@NotNull Method @NotNull []>> create(@NotNull Wildcard<@NotNull Method @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AcceptControlAllowHeadersHeaderKey extends HttpHeaderKey<@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []>> {
            private AcceptControlAllowHeadersHeaderKey() {
                super("Access-Control-Allow-Headers", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<Wildcard<HttpHeaderKey<?>[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                if (value.trim().equals("*")) {
                    return create(Wildcard.create());
                } else {
                    @NotNull List<HttpHeaderKey<?>> headers = new ArrayList<>();

                    for (@NotNull String name : value.split("\\s*,\\s*")) {
                        headers.add(HttpHeaderKey.retrieve(name));
                    }

                    return create(Wildcard.create(headers.toArray(new HttpHeaderKey[0])));
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Wildcard<HttpHeaderKey<?>[]>> header) {
                if (header.getValue().isWildcard()) {
                    return "*";
                } else {
                    @NotNull StringBuilder builder = new StringBuilder();

                    for (@NotNull HttpHeaderKey<?> key : header.getValue().getValue()) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(key);
                    }

                    return builder.toString();
                }
            }

            @Override
            public @NotNull HttpHeader<@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []>> create(@NotNull Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> value) {
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
        private static final class AcceptRangesHeaderKey extends HttpHeaderKey<@NotNull AcceptRange> {
            private AcceptRangesHeaderKey() {
                super("Accept-Ranges", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<AcceptRange> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(AcceptRange.valueOf(value.toUpperCase()));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<AcceptRange> header) {
                return header.getValue().name().toLowerCase();
            }
        }
        private static final class AcceptPostHeaderKey extends HttpHeaderKey<MediaType. @NotNull Type @NotNull []> {
            private AcceptPostHeaderKey() {
                super("Accept-Post", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<MediaType.Type[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull List<MediaType.Type> types = new ArrayList<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    types.add(MediaType.Type.parse(name));
                }

                return create(types.toArray(new MediaType.Type[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<MediaType.Type[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull MediaType.Type type : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(type);
                }

                return builder.toString();
            }

            @Override
            public @NotNull HttpHeader<MediaType.@NotNull Type @NotNull []> create(MediaType.@NotNull Type @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AcceptPatchHeaderKey extends HttpHeaderKey<@NotNull MediaType<?> @NotNull []> {
            private AcceptPatchHeaderKey() {
                super("Accept-Patch", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<MediaType<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<MediaType<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull MediaType<?> type : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(type);
                }

                return builder.toString();
            }

            @Override
            public @NotNull HttpHeader<@NotNull MediaType<?> @NotNull []> create(@NotNull MediaType<?> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }

        private static final class AcceptLanguageHeaderKey extends HttpHeaderKey<@NotNull Wildcard<@NotNull Weight<@NotNull Locale> @NotNull []>> {
            private AcceptLanguageHeaderKey() {
                super("Accept-Language", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Wildcard<Weight<Locale>[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Wildcard<Weight<Locale>[]>> header) {
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
            public @NotNull HttpHeader<@NotNull Wildcard<@NotNull Weight<@NotNull Locale> @NotNull []>> create(@NotNull Wildcard<@NotNull Weight<@NotNull Locale> @NotNull []> value) {
                if (!value.isWildcard() && value.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AcceptEncodingHeaderKey extends HttpHeaderKey<@NotNull Wildcard<@NotNull Weight<@NotNull Deferred<Encoding>> @NotNull []>> {
            private AcceptEncodingHeaderKey() {
                super("Accept-Encoding", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Wildcard<Weight<Deferred<Encoding>>[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Wildcard<Weight<Deferred<Encoding>>[]>> header) {
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
            public @NotNull HttpHeader<@NotNull Wildcard<Weight<Deferred<Encoding>> @NotNull []>> create(@NotNull Wildcard<Weight<Deferred<Encoding>> @NotNull []> wildcard) {
                if (!wildcard.isWildcard() && wildcard.getValue().length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(wildcard);
            }
        }
        private static final class AcceptCharsetHeaderKey extends HttpHeaderKey<@NotNull Weight<@NotNull Deferred<Charset>> @NotNull []> {
            private AcceptCharsetHeaderKey() {
                super("Accept-Charset", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<Weight<Deferred<Charset>>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Weight<Deferred<Charset>>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Weight<Deferred<Charset>> pseudo : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(pseudo);
                }

                return builder.toString();
            }

            @Override
            public @NotNull HttpHeader<Weight<Deferred<Charset>>[]> create(Weight<Deferred<Charset>> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class AcceptCHHeaderKey extends HttpHeaderKey<@NotNull HttpHeaderKey<?> @NotNull []> {
            private AcceptCHHeaderKey() {
                super("Accept-CH", Target.RESPONSE);
            }

            @Override
            public @NotNull HttpHeader<HttpHeaderKey<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull List<HttpHeaderKey<?>> keys = new ArrayList<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    @NotNull HttpHeaderKey<?> key = HttpHeaderKey.retrieve(name);

                    if (key.getTarget() == Target.RESPONSE) {
                        // Ignore response headers
                        continue;
                    }

                    keys.add(key);
                }

                return create(keys.toArray(new HttpHeaderKey[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<HttpHeaderKey<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull HttpHeaderKey<?> key : header.getValue()) {
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
            public @NotNull HttpHeader<HttpHeaderKey<?>[]> create(HttpHeaderKey<?> @NotNull [] value) {
                if (Arrays.stream(value).anyMatch(key -> !key.isClientHint())) {
                    throw new IllegalArgumentException("the '" + getName() + "' header value only accept client hint headers as value");
                } else if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }

                return super.create(value);
            }
        }
        private static final class AcceptHeaderKey extends HttpHeaderKey<@NotNull MediaType<?> @NotNull []> {
            private AcceptHeaderKey() {
                super("Accept", Target.REQUEST);
            }

            @Override
            public @NotNull HttpHeader<MediaType<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<MediaType<?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull MediaType<?> type : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(type);
                }

                return builder.toString();
            }

            @Override
            public @NotNull HttpHeader<@NotNull MediaType<?> @NotNull []> create(@NotNull MediaType<?> @NotNull [] value) {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The header '" + getName() + "' value must not be empty");
                }
                return super.create(value);
            }
        }
        private static final class ContentTypeHeaderKey extends HttpHeaderKey<@NotNull MediaType<?>> {
            private ContentTypeHeaderKey() {
                super("Content-Type", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<MediaType<?>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(MediaType.Parser.deserialize(value));
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<MediaType<?>> header) {
                return MediaType.Parser.serialize(header.getValue());
            }
        }
        private static final class TransferEncodingHeaderKey extends HttpHeaderKey<@NotNull Deferred<Encoding> @NotNull []> {
            private TransferEncodingHeaderKey() {
                super("Transfer-Encoding", Target.BOTH);
            }

            @Override
            public @NotNull HttpHeader<Deferred<Encoding>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                //noinspection unchecked
                @NotNull Deferred<Encoding>[] encodings = Arrays.stream(value.split("\\s*,\\s*")).map(Deferred::encoding).toArray(Deferred[]::new);

                return create(encodings);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull HttpHeader<Deferred<Encoding>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Deferred<Encoding> encoding : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(encoding);
                }

                return builder.toString();
            }
        }

        private static class HeaderImpl<T> implements HttpHeader<T> {

            private final @NotNull HttpHeaderKey<T> key;
            private final @UnknownNullability T value;

            private HeaderImpl(@NotNull HttpHeaderKey<T> key, @UnknownNullability T value) {
                this.key = key;
                this.value = value;
            }

            // Getters

            @Override
            public @NotNull HttpHeaderKey<T> getKey() {
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
            public @NotNull HeaderImpl<T> clone() {
                try {
                    //noinspection unchecked
                    return (HeaderImpl<T>) super.clone();
                } catch (@NotNull CloneNotSupportedException e) {
                    throw new RuntimeException("cannot clone header '" + getKey() + "'", e);
                }
            }

            @Override
            public @NotNull String toString() {
                return getName() + "=" + getValue();
            }

        }

    }

}
