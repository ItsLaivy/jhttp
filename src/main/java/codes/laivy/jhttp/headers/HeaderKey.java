package codes.laivy.jhttp.headers;

import codes.laivy.jhttp.authorization.Credentials;
import codes.laivy.jhttp.content.*;
import codes.laivy.jhttp.exception.parser.FilesystemProtocolException;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.url.csp.ContentSecurityPolicy;
import codes.laivy.jhttp.url.domain.Domain;
import codes.laivy.jhttp.url.domain.Domain.Host;
import codes.laivy.jhttp.utilities.*;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.utilities.header.Weight;
import codes.laivy.jhttp.utilities.header.Wildcard;
import codes.laivy.jhttp.utilities.pseudo.PseudoString;
import codes.laivy.jhttp.utilities.pseudo.provided.PseudoCharset;
import codes.laivy.jhttp.utilities.pseudo.provided.PseudoEncoding;
import org.jetbrains.annotations.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HeaderKey<T> {

    // Static initializers

    public static final @NotNull Pattern NAME_FORMAT_REGEX = Pattern.compile("^[A-Za-z][A-Za-z0-9-]*$");

    // todo: remove
    @ApiStatus.ScheduledForRemoval
    @Deprecated
    public static @NotNull HeaderKey<?> create(@NotNull String name) {
        try {
            @NotNull Field field = HeaderKey.class.getDeclaredField(name.replace("-", "_").toLowerCase());
            return (HeaderKey<?>) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
        } catch (Throwable throwable) {
            throw new IllegalStateException("Cannot create header key '" + name + "'");
        }

        return new StringHeaderKey(name, Target.BOTH);
    }

    // Provided

    public static @NotNull HeaderKey<MediaType[]> ACCEPT = new AcceptHeaderKey();
    public static @NotNull HeaderKey<HeaderKey<?>[]> ACCEPT_CH = new AcceptCHHeaderKey();
    @Deprecated
    // todo: duration
    public static @NotNull HeaderKey<Duration> ACCEPT_CH_LIFETIME = new IntegerHeaderKey("Accept-CH-Lifetime", Target.RESPONSE);
    public static @NotNull HeaderKey<Weight<PseudoCharset>[]> ACCEPT_CHARSET = new AcceptCharsetHeaderKey();
    public static @NotNull HeaderKey<Weight<PseudoEncoding>[]> ACCEPT_ENCODING = new AcceptEncodingHeaderKey();
    public static @NotNull HeaderKey<Weight<Locale>[]> ACCEPT_LANGUAGE = new AcceptLanguageHeaderKey();
    public static @NotNull HeaderKey<MediaType[]> ACCEPT_PATCH = new AcceptPatchHeaderKey();
    public static @NotNull HeaderKey<MediaType.Type[]> ACCEPT_POST = new AcceptPostHeaderKey();
    public static @NotNull HeaderKey<AcceptRange> ACCEPT_RANGES = new AcceptRangesHeaderKey();
    public static @NotNull HeaderKey<Boolean> ACCEPT_CONTROL_ALLOW_CREDENTIALS = new BooleanHeaderKey("Access-Control-Allow-Credentials", Target.RESPONSE);
    public static @NotNull HeaderKey<Wildcard<PseudoString<HeaderKey<?>>>[]> ACCEPT_CONTROL_ALLOW_HEADERS = new AcceptControlAllowHeadersHeaderKey();
    public static @NotNull HeaderKey<Wildcard<Method>[]> ACCEPT_CONTROL_ALLOW_METHODS = new AccessControlAllowMethodsHeaderKey();
    public static @NotNull HeaderKey<Wildcard<@Nullable URIAuthority>> ACCEPT_CONTROL_ALLOW_ORIGIN = new AccessControlAllowOriginHeaderKey();
    public static @NotNull HeaderKey<Wildcard<PseudoString<HeaderKey<?>>>[]> ACCEPT_CONTROL_EXPOSE_HEADERS = new AcceptControlExposeHeadersHeaderKey();
    // todo: duration
    public static @NotNull HeaderKey<Duration> ACCEPT_CONTROL_MAX_AGE = new IntegerHeaderKey("Access-Control-Max-Age", Target.RESPONSE);
    public static @NotNull HeaderKey<Wildcard<PseudoString<HeaderKey<?>>>[]> ACCEPT_CONTROL_REQUEST_HEADERS = new AcceptControlRequestHeadersHeaderKey();
    public static @NotNull HeaderKey<Method[]> ACCEPT_CONTROL_REQUEST_METHOD = new AccessControlRequestMethodHeaderKey();
    // todo: duration
    public static @NotNull HeaderKey<Duration> AGE = new IntegerHeaderKey("Age", Target.RESPONSE);
    public static @NotNull HeaderKey<Method[]> ALLOW = new AllowHeaderKey();
    public static @NotNull HeaderKey<Optional<AlternativeService[]>> ALT_SVC = new AltSvcHeaderKey();
    public static @NotNull HeaderKey<URIAuthority> ALT_USED = new AltUsedHeaderKey();
    public static @NotNull HeaderKey<Credentials> AUTHORIZATION = new AuthorizationHeaderKey();
    public static @NotNull HeaderKey<CacheControl> CACHE_CONTROL = new CacheControlHeaderKey();
    public static @NotNull HeaderKey<Wildcard<SiteData>[]> CLEAR_SITE_DATA = new ClearSiteDataHeaderKey();
    public static @NotNull HeaderKey<Connection> CONNECTION = new ConnectionHeaderKey();
    public static @NotNull HeaderKey<ContentDisposition> CONTENT_DISPOSITION = new ContentDispositionHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<?> CONTENT_DPR = new StringHeaderKey("Content-DPR");
    public static @NotNull HeaderKey<PseudoEncoding[]> CONTENT_ENCODING = new ContentEncodingHeaderKey();
    public static @NotNull HeaderKey<?> CONTENT_LANGUAGE = new StringHeaderKey("Content-Language");
    public static @NotNull HeaderKey<Integer> CONTENT_LENGTH = new IntegerHeaderKey("Content-Length", Target.BOTH);
    public static @NotNull HeaderKey<ContentLocation> CONTENT_LOCATION = new ContentLocationHeaderKey();
    public static @NotNull HeaderKey<ContentRange> CONTENT_RANGE = new ContentRangeHeaderKey();
    public static @NotNull HeaderKey<ContentSecurityPolicy> CONTENT_SECURITY_POLICY = new ContentSecurityPolicyHeaderKey();
    public static @NotNull HeaderKey<ContentSecurityPolicy> CONTENT_SECURITY_POLICY_REPORT_ONLY = new ContentSecurityPolicyReportOnlyeHeaderKey();
    public static @NotNull HeaderKey<MediaType> CONTENT_TYPE = new ContentTypeHeaderKey();
    public static @NotNull HeaderKey<Cookie[]> COOKIE = new CookieHeaderKey();
    public static @NotNull HeaderKey<?> CRITICAL_CH = new StringHeaderKey("Critical-CH");
    public static @NotNull HeaderKey<CrossOrigin.EmbedderPolicy> CROSS_ORIGIN_EMBEDDER_POLICY = new CrossOriginEmbedderPolicyHeaderKey();
    public static @NotNull HeaderKey<CrossOrigin.OpenerPolicy> CROSS_ORIGIN_OPENER_POLICY = new CrossOriginOpenerPolicyHeaderKey();
    public static @NotNull HeaderKey<CrossOrigin.ResourcePolicy> CROSS_ORIGIN_RESOURCE_POLICY = new CrossOriginResourcePolicyHeaderKey();
    public static @NotNull HeaderKey<OffsetDateTime> DATE = new DateHeaderKey();
    public static @NotNull HeaderKey<Float> DEVICE_MEMORY = new DeviceMemoryHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<?> DIGEST = new StringHeaderKey("Digest");
    @Deprecated
    public static @NotNull HeaderKey<Boolean> DNT = new DNTHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<NetworkSpeed> DOWNLINK = new DownlinkHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<Float> DPR = new DPRHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<Void> EARLY_DATA = new EarlyDataHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<EffectiveConnectionType> ECT = new ECTHeaderKey();
    public static @NotNull HeaderKey<EntityTag> ETAG = new ETagHeaderKey();
    public static @NotNull HeaderKey<HttpStatus> EXPECT = new ExpectHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<?> EXPECT_CT = new StringHeaderKey("Expect-CT");
    public static @NotNull HeaderKey<OffsetDateTime> EXPIRES = new ExpiresHeaderKey();
    public static @NotNull HeaderKey<Forwarded> FORWARDED = new ForwardedHeaderKey();
    public static @NotNull HeaderKey<Email> FROM = new FromHeaderKey();
    public static @NotNull HeaderKey<Host> HOST = new HostHeaderKey();
    public static @NotNull HeaderKey<Wildcard<EntityTag[]>> IF_MATCH = new IfMatchHeaderKey();
    public static @NotNull HeaderKey<?> IF_MODIFIED_SINCE = new StringHeaderKey("If-Modified-Since");
    public static @NotNull HeaderKey<?> IF_NONE_MATCH = new StringHeaderKey("If-None-Match");
    public static @NotNull HeaderKey<?> IF_RANGE = new StringHeaderKey("If-Range");
    public static @NotNull HeaderKey<?> IF_UNMODIFIED_SINCE = new StringHeaderKey("If-Unmodified-Since");
    public static @NotNull HeaderKey<?> KEEP_ALIVE = new StringHeaderKey("Keep-Alive");
    @Deprecated
    public static @NotNull HeaderKey<?> LARGE_ALLOCATION = new StringHeaderKey("Large-Allocation");
    public static @NotNull HeaderKey<?> LAST_MODIFIED = new StringHeaderKey("Last-Modified");
    public static @NotNull HeaderKey<?> LINK = new StringHeaderKey("Link");
    public static @NotNull HeaderKey<?> LOCATION = new StringHeaderKey("Location");
    public static @NotNull HeaderKey<?> MAX_FORWARDS = new StringHeaderKey("Max-Forwards");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> NEL = new StringHeaderKey("NEL");
    public static @NotNull HeaderKey<?> OBSERVE_BROWSING_TOPICS = new StringHeaderKey("Observe-Browsing-Topics");
    public static @NotNull HeaderKey<?> ORIGIN = new StringHeaderKey("Origin");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> ORIGIN_AGENT_CLUSTER = new StringHeaderKey("Origin-Agent-Cluster");
    public static @NotNull HeaderKey<?> PERMISSIONS_POLICY = new StringHeaderKey("Permissions-Policy");
    @Deprecated
    public static @NotNull HeaderKey<?> PRAGMA = new StringHeaderKey("Pragma");
    public static @NotNull HeaderKey<?> PROXY_AUTHENTICATE = new StringHeaderKey("Proxy-Authenticate");
    public static @NotNull HeaderKey<?> PROXY_AUTHORIZATION = new StringHeaderKey("Proxy-Authorization");
    public static @NotNull HeaderKey<?> RANGE = new StringHeaderKey("Range");
    public static @NotNull HeaderKey<?> REFERER = new StringHeaderKey("Referer");
    public static @NotNull HeaderKey<?> REFERRER_POLICY = new StringHeaderKey("Referrer-Policy");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> RTT = new StringHeaderKey("RTT");
    public static @NotNull HeaderKey<?> SAVE_DATA = new StringHeaderKey("Save-Data");
    public static @NotNull HeaderKey<?> SEC_BROWSING_TOPICS = new StringHeaderKey("Sec-Browsing-Topics");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_PREFERS_COLOR_SCHEME = new StringHeaderKey("Sec-CH-Prefers-Color-Scheme");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_PREFERS_REDUCED_MOTION = new StringHeaderKey("Sec-CH-Prefers-Reduced-Motion");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_PREFERS_REDUCED_TRANSPARENCY = new StringHeaderKey("Sec-CH-Prefers-Reduced-Transparency");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_UA = new StringHeaderKey("Sec-CH-UA");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_UA_ARCH = new StringHeaderKey("Sec-CH-UA-Arch");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_UA_BITNESS = new StringHeaderKey("Sec-CH-UA-Bitness");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_UA_FULL_VERSION = new StringHeaderKey("Sec-CH-UA-Full-Version");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_UA_FULL_VERSION_LIST = new StringHeaderKey("Sec-CH-UA-Full-Version-List");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_UA_MOBILE = new StringHeaderKey("Sec-CH-UA-Mobile");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_UA_MODEL = new StringHeaderKey("Sec-CH-UA-Model");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_UA_PLATFORM = new StringHeaderKey("Sec-CH-UA-Platform");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_CH_UA_PLATFORM_VERSION = new StringHeaderKey("Sec-CH-UA-Platform-Version");
    public static @NotNull HeaderKey<?> SEC_FETCH_DEST = new StringHeaderKey("Sec-Fetch-Dest");
    public static @NotNull HeaderKey<?> SEC_FETCH_MODE = new StringHeaderKey("Sec-Fetch-Mode");
    public static @NotNull HeaderKey<?> SEC_FETCH_SITE = new StringHeaderKey("Sec-Fetch-Site");
    public static @NotNull HeaderKey<?> SEC_FETCH_USER = new StringHeaderKey("Sec-Fetch-User");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SEC_GPC = new StringHeaderKey("Sec-GPC");
    public static @NotNull HeaderKey<?> SEC_PURPOSE = new StringHeaderKey("Sec-Purpose");
    public static @NotNull HeaderKey<?> SEC_WEBSOCKET_ACCEPT = new StringHeaderKey("Sec-WebSocket-Accept");
    public static @NotNull HeaderKey<?> SERVER = new StringHeaderKey("Server");
    public static @NotNull HeaderKey<?> SERVER_TIMING = new StringHeaderKey("Server-Timing");
    public static @NotNull HeaderKey<?> SERVICE_WORKER_NAVIGATION_PRELOAD = new StringHeaderKey("Service-Worker-Navigation-Preload");
    public static @NotNull HeaderKey<?> SET_COOKIE = new StringHeaderKey("Set-Cookie");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> SET_LOGIN = new StringHeaderKey("Set-Login");
    public static @NotNull HeaderKey<?> SOURCEMAP = new StringHeaderKey("SourceMap");
    public static @NotNull HeaderKey<?> STRICT_TRANSPORT_SECURITY = new StringHeaderKey("Strict-Transport-Security");
    public static @NotNull HeaderKey<?> SUPPORTS_LOADING_MODE = new StringHeaderKey("Supports-Loading-Mode");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> TE = new StringHeaderKey("TE");
    public static @NotNull HeaderKey<?> TIMING_ALLOW_ORIGIN = new StringHeaderKey("Timing-Allow-Origin");
    @Deprecated
    public static @NotNull HeaderKey<?> TK = new StringHeaderKey("Tk");
    public static @NotNull HeaderKey<?> TRAILER = new StringHeaderKey("Trailer");
    public static @NotNull HeaderKey<PseudoEncoding[]> TRANSFER_ENCODING = new TransferEncodingHeaderKey();
    public static @NotNull HeaderKey<?> ANONYMOUS_HEADER = new StringHeaderKey("X-Anonymous", Pattern.compile("^(?i)(true|false)$"));

    /**
     * @see <a href="https://regexr.com/7sg4c">RegExr Tests</a>
     * @apiNote Last change: 23/02/2024 | 19:23 (GMT-3)
     */
    public static @NotNull HeaderKey<?> UPGRADE = new StringHeaderKey("Upgrade", Pattern.compile("^[a-zA-Z-_]+(?:/[a-zA-Z0-9-_.@]+)?(?:,\\s?[a-zA-Z-_]+(?:/[a-zA-Z0-9-_.@]+)?)*$"));
    public static @NotNull HeaderKey<?> UPGRADE_INSECURE_REQUESTS = new StringHeaderKey("Upgrade-Insecure-Requests");
    public static @NotNull HeaderKey<?> USER_AGENT = new StringHeaderKey("User-Agent");
    public static @NotNull HeaderKey<?> VARY = new StringHeaderKey("Vary");
    public static @NotNull HeaderKey<?> VIA = new StringHeaderKey("Via");
    @Deprecated
    public static @NotNull HeaderKey<?> VIEWPORT_WIDTH = new StringHeaderKey("Viewport-Width");
    @Deprecated
    public static @NotNull HeaderKey<?> WANT_DIGEST = new StringHeaderKey("Want-Digest");
    @Deprecated
    public static @NotNull HeaderKey<?> WARNING = new StringHeaderKey("Warning");
    @Deprecated
    public static @NotNull HeaderKey<?> WIDTH = new StringHeaderKey("Width");
    public static @NotNull HeaderKey<?> WWW_AUTHENTICATE = new StringHeaderKey("WWW-Authenticate");
    public static @NotNull HeaderKey<?> PROXY_CONNECTION = new StringHeaderKey("Proxy-Connection", Pattern.compile("^(?i)(keep-alive|close)(,\\s?[a-zA-Z0-9!#$%&'*+.^_`|~-]+)*$"));

    // Object

    private final @NotNull String name;
    private final @NotNull Target target;

    private HeaderKey(@NotNull String name, @NotNull Target target) {
        this.name = name;
        this.target = target;
    }

    @Contract(pure = true)
    public final @NotNull String getName() {
        return this.name;
    }

    public final @NotNull Target getTarget() {
        return this.target;
    }

    // Modules

    public abstract @NotNull Header<T> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException;
    public abstract @NotNull String write(@NotNull Header<T> header);

    public @NotNull Header<T> create(@UnknownNullability T value) {
        return Header.create(this, value);
    }

    // Implementations

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof HeaderKey)) return false;
        HeaderKey<?> that = (HeaderKey<?>) object;
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

    private static final class IntegerHeaderKey extends HeaderKey<Integer> {
        private IntegerHeaderKey(@NotNull String name, @NotNull Target target) {
            super(name, target);
        }

        @Override
        public @NotNull Header<Integer> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(Integer.parseInt(value));
        }
        @Override
        public @NotNull String write(@NotNull Header<Integer> header) {
            return header.getValue().toString();
        }
    }
    private static final class BooleanHeaderKey extends HeaderKey<Boolean> {
        private BooleanHeaderKey(@NotNull String name, @NotNull Target target) {
            super(name, target);
        }

        @Override
        public @NotNull Header<Boolean> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(Boolean.parseBoolean(value));
        }

        @Override
        public @NotNull String write(@NotNull Header<Boolean> header) {
            return header.getValue().toString();
        }
    }

    private static final class IfMatchHeaderKey extends HeaderKey<Wildcard<EntityTag[]>> {
        private IfMatchHeaderKey() {
            super("If-Match", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Wildcard<EntityTag[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            if (value.trim().equals("*")) {
                return create(Wildcard.create());
            } else {
                @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
                @NotNull Matcher matcher = pattern.matcher(value);
                @NotNull Set<EntityTag> tag = new LinkedHashSet<>();

                while (matcher.find()) {
                    try {
                        tag.add(EntityTag.parse(matcher.group()));
                    } catch (ParseException e) {
                        throw new HeaderFormatException(e);
                    }
                }

                return create(Wildcard.create(tag.toArray(new EntityTag[0])));
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<Wildcard<EntityTag[]>> header) {
            @NotNull Wildcard<EntityTag[]> wildcard = header.getValue();

            if (wildcard.isWildcard()) {
                return "*";
            } else {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull EntityTag tag : header.getValue().getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(tag);
                }

                return builder.toString();
            }
        }
    }
    private static final class HostHeaderKey extends HeaderKey<Host> {
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
        public @NotNull String write(@NotNull Header<Host> header) {
            return header.getValue().toString();
        }
    }
    private static final class FromHeaderKey extends HeaderKey<Email> {
        private FromHeaderKey() {
            super("From", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Email> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            try {
                return create(Email.parse(value));
            } catch (ParseException e) {
                throw new HeaderFormatException(e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<Email> header) {
            return header.getValue().toString();
        }
    }
    private static final class ForwardedHeaderKey extends HeaderKey<Forwarded> {
        private ForwardedHeaderKey() {
            super("Forwarded", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Forwarded> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            try {
                return create(Forwarded.parse(value));
            } catch (@NotNull ParseException e) {
                throw new HeaderFormatException(e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<Forwarded> header) {
            return header.getValue().toString();
        }
    }
    private static final class ExpiresHeaderKey extends HeaderKey<OffsetDateTime> {
        private ExpiresHeaderKey() {
            super("Expires", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            if (value.trim().equals("0")) {
                return create(OffsetDateTime.MIN);
            } else {
                return create(DateUtils.RFC822.convert(value));
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<OffsetDateTime> header) {
            if (header.getValue().isBefore(OffsetDateTime.now())) {
                return "0";
            } else {
                return DateUtils.RFC822.convert(header.getValue());
            }
        }
    }
    private static final class ExpectHeaderKey extends HeaderKey<HttpStatus> {
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
        public @NotNull String write(@NotNull Header<HttpStatus> header) {
            @NotNull HttpStatus status = header.getValue();
            return status.getCode() + "-" + status.getMessage().replace(" ", "_").toLowerCase();
        }
    }
    private static final class ETagHeaderKey extends HeaderKey<EntityTag> {
        private ETagHeaderKey() {
            super("ETag", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<EntityTag> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            try {
                return create(EntityTag.parse(value));
            } catch (ParseException e) {
                throw new HeaderFormatException(e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<EntityTag> header) {
            return header.getValue().toString();
        }
    }
    private static final class ECTHeaderKey extends HeaderKey<EffectiveConnectionType> {
        private ECTHeaderKey() {
            super("ECT", Target.REQUEST);
        }

        @Override
        public @NotNull Header<EffectiveConnectionType> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(EffectiveConnectionType.getById(value));
        }
        @Override
        public @NotNull String write(@NotNull Header<EffectiveConnectionType> header) {
            return header.getValue().getId();
        }
    }
    private static final class EarlyDataHeaderKey extends HeaderKey<Void> {
        private EarlyDataHeaderKey() {
            super("Early-Data", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Void> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create((Void) null);
        }
        @Override
        public @NotNull String write(@NotNull Header<Void> header) {
            return "1";
        }
    }
    private static final class DPRHeaderKey extends HeaderKey<Float> {
        private DPRHeaderKey() {
            super("DPR", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Float> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(Float.parseFloat(value));
        }
        @Override
        public @NotNull String write(@NotNull Header<Float> header) {
            return header.getValue().toString();
        }
    }
    private static final class DownlinkHeaderKey extends HeaderKey<NetworkSpeed> {
        private DownlinkHeaderKey() {
            super("Downlink", Target.REQUEST);
        }

        @Override
        public @NotNull Header<NetworkSpeed> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(NetworkSpeed.create((long) (Double.parseDouble(value) * 1_000_000D)));
        }
        @Override
        public @NotNull String write(@NotNull Header<NetworkSpeed> header) {
            return String.valueOf(header.getValue().getBits(NetworkSpeed.Category.MEGABITS));
        }
    }
    private static final class DNTHeaderKey extends HeaderKey<Boolean> {
        private DNTHeaderKey() {
            super("DNT", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Boolean> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(value.trim().equals("1"));
        }
        @Override
        public @NotNull String write(@NotNull Header<Boolean> header) {
            return header.getValue() ? "1" : "0";
        }
    }
    private static final class DeviceMemoryHeaderKey extends HeaderKey<Float> {
        private DeviceMemoryHeaderKey() {
            super("Device-Memory", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Float> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(Float.parseFloat(value));
        }
        @Override
        public @NotNull String write(@NotNull Header<Float> header) {
            return header.getValue().toString();
        }
    }
    private static final class DateHeaderKey extends HeaderKey<OffsetDateTime> {
        private DateHeaderKey() {
            super("Date", Target.BOTH);
        }

        @Override
        public @NotNull Header<OffsetDateTime> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(DateUtils.RFC822.convert(value));
        }
        @Override
        public @NotNull String write(@NotNull Header<OffsetDateTime> header) {
            return DateUtils.RFC822.convert(header.getValue());
        }
    }
    private static final class CrossOriginResourcePolicyHeaderKey extends HeaderKey<CrossOrigin.ResourcePolicy> {
        private CrossOriginResourcePolicyHeaderKey() {
            super("Cross-Origin-Resource-Policy", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<CrossOrigin.ResourcePolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(CrossOrigin.ResourcePolicy.getById(value));
        }
        @Override
        public @NotNull String write(@NotNull Header<CrossOrigin.ResourcePolicy> header) {
            return header.getValue().getId();
        }
    }
    private static final class CrossOriginOpenerPolicyHeaderKey extends HeaderKey<CrossOrigin.OpenerPolicy> {
        private CrossOriginOpenerPolicyHeaderKey() {
            super("Cross-Origin-Opener-Policy", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<CrossOrigin.OpenerPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(CrossOrigin.OpenerPolicy.getById(value));
        }
        @Override
        public @NotNull String write(@NotNull Header<CrossOrigin.OpenerPolicy> header) {
            return header.getValue().getId();
        }
    }
    private static final class CrossOriginEmbedderPolicyHeaderKey extends HeaderKey<CrossOrigin.EmbedderPolicy> {
        private CrossOriginEmbedderPolicyHeaderKey() {
            super("Cross-Origin-Embedder-Policy", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<CrossOrigin.EmbedderPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(CrossOrigin.EmbedderPolicy.getById(value));
        }
        @Override
        public @NotNull String write(@NotNull Header<CrossOrigin.EmbedderPolicy> header) {
            return header.getValue().getId();
        }
    }
    private static final class CookieHeaderKey extends HeaderKey<Cookie[]> {
        private CookieHeaderKey() {
            super("Cookie", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Cookie[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("\\s*;\\s*");
            @NotNull Matcher matcher = pattern.matcher(value);
            @NotNull List<Cookie> cookies = new LinkedList<>();

            while (matcher.find()) {
                try {
                    @NotNull Cookie cookie = Cookie.parse(matcher.group());
                    cookies.add(cookie);
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }

            return create(cookies.toArray(new Cookie[0]));
        }

        @Override
        public @NotNull String write(@NotNull Header<Cookie[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Cookie cookie : header.getValue()) {
                if (builder.length() > 0) builder.append("; ");
                builder.append(cookie.getKey()).append("=").append(cookie.getValue());
            }
            
            return builder.toString();
        }
    }
    private static final class ContentSecurityPolicyReportOnlyeHeaderKey extends HeaderKey<ContentSecurityPolicy> {
        private ContentSecurityPolicyReportOnlyeHeaderKey() {
            super("Content-Security-Policy-Report-Only", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<ContentSecurityPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            try {
                return create(ContentSecurityPolicy.parse(value));
            } catch (ParseException | UnsupportedEncodingException | FilesystemProtocolException e) {
                throw new HeaderFormatException("cannot parse '" + value + "' into a valid content security policy", e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<ContentSecurityPolicy> header) {
            return header.getValue().toString();
        }
    }
    private static final class ContentSecurityPolicyHeaderKey extends HeaderKey<ContentSecurityPolicy> {
        private ContentSecurityPolicyHeaderKey() {
            super("Content-Security-Policy", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<ContentSecurityPolicy> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            try {
                return create(ContentSecurityPolicy.parse(value));
            } catch (ParseException | UnsupportedEncodingException | FilesystemProtocolException e) {
                throw new HeaderFormatException("cannot parse '" + value + "' into a valid content security policy", e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<ContentSecurityPolicy> header) {
            return header.getValue().toString();
        }
    }
    private static final class ContentRangeHeaderKey extends HeaderKey<ContentRange> {
        private ContentRangeHeaderKey() {
            super("Content-Range", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<ContentRange> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            try {
                return create(ContentRange.parse(value));
            } catch (ParseException e) {
                throw new HeaderFormatException("cannot parse '" + value + "' into a valid content range", e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<ContentRange> header) {
            return header.getValue().toString();
        }
    }
    private static final class ContentLocationHeaderKey extends HeaderKey<ContentLocation> {
        private ContentLocationHeaderKey() {
            super("Content-Location", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<ContentLocation> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            try {
                return create(ContentLocation.parse(value));
            } catch (ParseException | UnknownHostException | URISyntaxException e) {
                throw new HeaderFormatException("cannot parse '" + value + "' into a location", e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<ContentLocation> header) {
            return header.getValue().toString();
        }
    }
    private static final class ContentEncodingHeaderKey extends HeaderKey<PseudoEncoding[]> {
        private ContentEncodingHeaderKey() {
            super("Content-Encoding", Target.BOTH);
        }

        @Override
        public @NotNull Header<PseudoEncoding[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Matcher matcher = Pattern.compile("\\s*,\\s*").matcher(value);
            @NotNull List<PseudoEncoding> encodings = new ArrayList<>();

            while (matcher.find()) {
                @NotNull String name = matcher.group(matcher.group());
                encodings.add(PseudoEncoding.create(name));
            }

            return create(encodings.toArray(new PseudoEncoding[0]));
        }
        @Override
        public @NotNull String write(@NotNull Header<PseudoEncoding[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull PseudoEncoding encoding : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(encoding.raw());
            }

            return builder.toString();
        }
    }
    private static final class ContentDispositionHeaderKey extends HeaderKey<ContentDisposition> {
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
        public @NotNull String write(@NotNull Header<ContentDisposition> header) {
            return header.getValue().toString();
        }
    }
    private static final class ConnectionHeaderKey extends HeaderKey<Connection> {
        private ConnectionHeaderKey() {
            super("Connection", Target.BOTH);
        }

        @Override
        public @NotNull Header<Connection> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            try {
                return create(Connection.parse(value));
            } catch (ParseException e) {
                throw new HeaderFormatException("cannot parse '" + value + "' into a valid connection", e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<Connection> header) {
            return header.getValue().toString();
        }
    }
    private static final class ClearSiteDataHeaderKey extends HeaderKey<Wildcard<SiteData>[]> {
        private ClearSiteDataHeaderKey() {
            super("Clear-Site-Data", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<Wildcard<SiteData>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("\"(.*?)\"");
            @NotNull Matcher matcher = pattern.matcher(value);
            @NotNull Set<Wildcard<SiteData>> data = new LinkedHashSet<>();

            while (matcher.find()) {
                @NotNull String name = matcher.group(1);

                if (name.trim().equals("*")) {
                    data.add(Wildcard.create());
                } else {
                    @NotNull Optional<SiteData> optional = Arrays.stream(SiteData.values()).filter(s -> s.getId().equalsIgnoreCase(name)).findFirst();

                    if (optional.isPresent()) data.add(Wildcard.create(optional.get()));
                    else throw new NullPointerException("there's no site data named '" + name + "'");
                }
            }

            //noinspection unchecked
            return create(data.toArray(new Wildcard[0]));
        }
        @Override
        public @NotNull String write(@NotNull Header<Wildcard<SiteData>[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Wildcard<SiteData> data : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(data);
            }

            return builder.toString();
        }
    }
    private static final class CacheControlHeaderKey extends HeaderKey<CacheControl> {
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
        public @NotNull String write(@NotNull Header<CacheControl> header) {
            return header.getValue().toString();
        }
    }
    private static final class AuthorizationHeaderKey extends HeaderKey<Credentials> {
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
        public @NotNull String write(@NotNull Header<Credentials> header) {
            return header.getValue().toString();
        }
    }
    private static final class AltUsedHeaderKey extends HeaderKey<URIAuthority> {
        private AltUsedHeaderKey() {
            super("Alt-Used", Target.REQUEST);
        }

        @Override
        public @NotNull Header<URIAuthority> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            try {
                return create(URIAuthority.parse(value));
            } catch (URISyntaxException | UnknownHostException e) {
                throw new HeaderFormatException("cannot parse '" + value + "' into a valid uri authority in header '" + getName() + "'", e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<URIAuthority> header) {
            @NotNull URIAuthority authority = header.getValue();
            return authority.getHostName() + ":" + authority.getPort();
        }
    }
    private static final class AltSvcHeaderKey extends HeaderKey<Optional<AlternativeService[]>> {
        private AltSvcHeaderKey() {
            super("Alt-Svc", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<Optional<AlternativeService[]>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            if (value.trim().equalsIgnoreCase("clear")) {
                return create(Optional.empty());
            }

            @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
            @NotNull Matcher matcher = pattern.matcher(value);
            @NotNull AlternativeService[] services = new AlternativeService[matcher.groupCount()];

            // todo: row never updated
            int row = 0;
            while (matcher.find()) {
                @NotNull String group = matcher.group();

                try {
                    services[row] = AlternativeService.parse(group);
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException("cannot parse alternative service '" + group + "'", e);
                }
            }

            return create(Optional.of(services));
        }
        @Override
        public @NotNull String write(@NotNull Header<Optional<AlternativeService[]>> header) {
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
    }
    private static final class AllowHeaderKey extends HeaderKey<Method[]> {
        private AllowHeaderKey() {
            super("Allow", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<Method[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
            @NotNull Matcher matcher = pattern.matcher(value);
            @NotNull Method[] methods = new Method[matcher.groupCount()];

            int row = 0;
            while (matcher.find()) {
                @NotNull String group = matcher.group();

                try {
                    methods[row] = Method.valueOf(group.toUpperCase());
                    row++;
                } catch (@NotNull IllegalArgumentException ignore) {
                    throw new HeaderFormatException("cannot parse method '" + group + "' from header '" + getName() + "'");
                }
            }

            return create(methods);
        }
        @Override
        public @NotNull String write(@NotNull Header<Method[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Method method : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(method.name().toLowerCase());
            }

            return builder.toString();
        }
    }
    private static final class AccessControlRequestMethodHeaderKey extends HeaderKey<Method[]> {
        private AccessControlRequestMethodHeaderKey() {
            super("Access-Control-Request-Method", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Method[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
            @NotNull Matcher matcher = pattern.matcher(value);
            @NotNull Method[] methods = new Method[matcher.groupCount()];

            int row = 0;
            while (matcher.find()) {
                @NotNull String group = matcher.group();

                try {
                    methods[row] = Method.valueOf(group.toUpperCase());
                    row++;
                } catch (@NotNull IllegalArgumentException ignore) {
                    throw new HeaderFormatException("cannot parse method '" + group + "' from header '" + getName() + "'");
                }
            }

            return create(methods);
        }
        @Override
        public @NotNull String write(@NotNull Header<Method[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Method method : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(method.name().toLowerCase());
            }

            return builder.toString();
        }
    }
    private static final class AcceptControlRequestHeadersHeaderKey extends HeaderKey<Wildcard<PseudoString<HeaderKey<?>>>[]> {
        private AcceptControlRequestHeadersHeaderKey() {
            super("Access-Control-Request-Headers", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Wildcard<PseudoString<HeaderKey<?>>>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
            @NotNull Matcher matcher = pattern.matcher(value);

            //noinspection unchecked
            @NotNull Wildcard<PseudoString<HeaderKey<?>>>[] headers = new Wildcard[matcher.groupCount()];

            int row = 0;
            while (matcher.find()) {
                @NotNull String group = matcher.group();
                headers[row] = (group.trim().equals("*") ?
                        Wildcard.create() :
                        Wildcard.create(PseudoString.create(group, () -> true, () -> HeaderKey.create(group)))
                );
                row++;
            }

            return create(headers);
        }
        @Override
        public @NotNull String write(@NotNull Header<Wildcard<PseudoString<HeaderKey<?>>>[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Wildcard<PseudoString<HeaderKey<?>>> h : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(h.toString().toLowerCase());
            }

            return builder.toString();
        }
    }
    private static final class AcceptControlExposeHeadersHeaderKey extends HeaderKey<Wildcard<PseudoString<HeaderKey<?>>>[]> {
        private AcceptControlExposeHeadersHeaderKey() {
            super("Access-Control-Expose-Headers", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<Wildcard<PseudoString<HeaderKey<?>>>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
            @NotNull Matcher matcher = pattern.matcher(value);

            //noinspection unchecked
            @NotNull Wildcard<PseudoString<HeaderKey<?>>>[] headers = new Wildcard[matcher.groupCount()];

            int row = 0;
            while (matcher.find()) {
                @NotNull String group = matcher.group();
                headers[row] = (group.trim().equals("*") ?
                        Wildcard.create() :
                        Wildcard.create(PseudoString.create(group, () -> true, () -> HeaderKey.create(group)))
                );
                row++;
            }

            return create(headers);
        }
        @Override
        public @NotNull String write(@NotNull Header<Wildcard<PseudoString<HeaderKey<?>>>[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Wildcard<PseudoString<HeaderKey<?>>> h : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(h);
            }

            return builder.toString();
        }
    }
    private static final class AccessControlAllowOriginHeaderKey extends HeaderKey<Wildcard<@Nullable URIAuthority>> {
        private AccessControlAllowOriginHeaderKey() {
            super("Access-Control-Allow-Origin", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<Wildcard<@Nullable URIAuthority>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            value = value.trim();

            if (value.equals("*")) {
                return create(Wildcard.create());
            } else if (value.equalsIgnoreCase("null")) {
                return create(Wildcard.create(null));
            } else try {
                return create(Wildcard.create(URIAuthority.parse(value)));
            } catch (@NotNull UnknownHostException | @NotNull URISyntaxException e) {
                throw new HeaderFormatException("cannot parse '" + value + "' into a valid uri authority of '" + getName() + "' header", e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<Wildcard<@Nullable URIAuthority>> header) {
            return header.getValue().toString();
        }
    }
    private static final class AccessControlAllowMethodsHeaderKey extends HeaderKey<Wildcard<Method>[]> {
        private AccessControlAllowMethodsHeaderKey() {
            super("Access-Control-Allow-Methods", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<Wildcard<Method>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
            @NotNull Matcher matcher = pattern.matcher(value);

            //noinspection unchecked
            @NotNull Wildcard<Method>[] methods = new Wildcard[matcher.groupCount()];

            int row = 0;
            while (matcher.find()) {
                @NotNull String group = matcher.group();

                try {
                    methods[row] = group.trim().equals("*") ? Wildcard.create() : Wildcard.create(Method.valueOf(group.toUpperCase()));
                    row++;
                } catch (@NotNull IllegalArgumentException ignore) {
                    throw new HeaderFormatException("cannot parse method '" + group + "' from header '" + getName() + "'");
                }
            }

            return create(methods);
        }
        @Override
        public @NotNull String write(@NotNull Header<Wildcard<Method>[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Wildcard<Method> method : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(method);
            }

            return builder.toString();
        }
    }
    private static final class AcceptControlAllowHeadersHeaderKey extends HeaderKey<Wildcard<PseudoString<HeaderKey<?>>>[]> {
        private AcceptControlAllowHeadersHeaderKey() {
            super("Access-Control-Allow-Headers", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<Wildcard<PseudoString<HeaderKey<?>>>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
            @NotNull Matcher matcher = pattern.matcher(value);

            //noinspection unchecked
            @NotNull Wildcard<PseudoString<HeaderKey<?>>>[] headers = new Wildcard[matcher.groupCount()];

            int row = 0;
            while (matcher.find()) {
                @NotNull String group = matcher.group();
                headers[row] = (group.trim().equals("*") ?
                        Wildcard.create() :
                        Wildcard.create(PseudoString.create(group, () -> true, () -> HeaderKey.create(group)))
                );
                row++;
            }

            return create(headers);
        }
        @Override
        public @NotNull String write(@NotNull Header<Wildcard<PseudoString<HeaderKey<?>>>[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Wildcard<PseudoString<HeaderKey<?>>> h : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(h);
            }

            return builder.toString();
        }
    }
    private static final class AcceptRangesHeaderKey extends HeaderKey<AcceptRange> {
        private AcceptRangesHeaderKey() {
            super("Accept-Ranges", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<AcceptRange> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            return create(AcceptRange.valueOf(value.toUpperCase()));
        }
        @Override
        public @NotNull String write(@NotNull Header<AcceptRange> header) {
            return header.getValue().name().toLowerCase();
        }
    }
    private static final class AcceptPostHeaderKey extends HeaderKey<MediaType.Type[]> {
        private AcceptPostHeaderKey() {
            super("Accept-Post", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<MediaType.Type[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
            @NotNull Matcher matcher = pattern.matcher(value);
            @NotNull MediaType.Type[] types = new MediaType.Type[matcher.groupCount()];

            int row = 0;
            while (matcher.find()) {
                types[row] = MediaType.Type.parse(matcher.group());
                row++;
            }

            return create(types);
        }
        @Override
        public @NotNull String write(@NotNull Header<MediaType.Type[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull MediaType.Type type : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(type);
            }

            return builder.toString();
        }
    }
    private static final class AcceptPatchHeaderKey extends HeaderKey<MediaType[]> {
        private AcceptPatchHeaderKey() {
            super("Accept-Patch", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<MediaType[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
            @NotNull Matcher matcher = pattern.matcher(value);
            @NotNull MediaType[] types = new MediaType[matcher.groupCount()];

            int row = 0;
            while (matcher.find()) {
                try {
                    types[row] = MediaType.parse(matcher.group());
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException("cannot parse media type '" + matcher.group() + "'", e);
                }

                row++;
            }

            return create(types);
        }
        @Override
        public @NotNull String write(@NotNull Header<MediaType[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull MediaType type : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(type);
            }

            return builder.toString();
        }
    }

    // todo: accept language pseudo locale
    private static final class AcceptLanguageHeaderKey extends HeaderKey<Weight<Locale>[]> {
        private AcceptLanguageHeaderKey() {
            super("Accept-Language", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Weight<Locale>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("(?<locale>[^,;\\s]+)(?:\\s*;\\s*q\\s*=\\s*(?<weight>\\d(?:[.,]\\d)?))?");
            @NotNull Matcher matcher = pattern.matcher(value);

            //noinspection unchecked
            @NotNull Weight<Locale>[] pairs = new Weight[matcher.groupCount()];

            for (int group = 0; group < matcher.groupCount(); group++) {
                if (!matcher.find()) break;

                @NotNull String locale = matcher.group("locale");
                @Nullable Float weight = matcher.group("weight") != null ? Float.parseFloat(matcher.group("weight").replace(",", ".")) : null;

                pairs[group] = Weight.create(weight, Locale.forLanguageTag(locale));
            }

            return create(pairs);
        }
        @Override
        public @NotNull String write(@NotNull Header<Weight<Locale>[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Weight<Locale> pseudo : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(pseudo);
            }

            return builder.toString();
        }
    }
    private static final class AcceptEncodingHeaderKey extends HeaderKey<Weight<PseudoEncoding>[]> {
        private AcceptEncodingHeaderKey() {
            super("Accept-Encoding", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Weight<PseudoEncoding>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("(?<encoding>[^,;\\s]+)(?:\\s*;\\s*q\\s*=\\s*(?<weight>\\d(?:[.,]\\d)?))?");
            @NotNull Matcher matcher = pattern.matcher(value);

            //noinspection unchecked
            @NotNull Weight<PseudoEncoding>[] pairs = new Weight[matcher.groupCount()];

            for (int group = 0; group < matcher.groupCount(); group++) {
                if (!matcher.find()) break;

                @NotNull String encoding = matcher.group("encoding");
                @Nullable Float weight = matcher.group("weight") != null ? Float.parseFloat(matcher.group("weight").replace(",", ".")) : null;

                pairs[group] = Weight.create(weight, PseudoEncoding.create(encoding));
            }

            return create(pairs);
        }
        @Override
        public @NotNull String write(@NotNull Header<Weight<PseudoEncoding>[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Weight<PseudoEncoding> pseudo : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(pseudo);
            }

            return builder.toString();
        }
    }
    private static final class AcceptCharsetHeaderKey extends HeaderKey<Weight<PseudoCharset>[]> {
        private AcceptCharsetHeaderKey() {
            super("Accept-Charset", Target.REQUEST);
        }

        @Override
        public @NotNull Header<Weight<PseudoCharset>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Pattern pattern = Pattern.compile("(?<charset>[^,;\\s]+)(?:\\s*;\\s*q\\s*=\\s*(?<weight>\\d(?:[.,]\\d)?))?");
            @NotNull Matcher matcher = pattern.matcher(value);

            //noinspection unchecked
            @NotNull Weight<PseudoCharset>[] pairs = new Weight[matcher.groupCount()];

            for (int group = 0; group < matcher.groupCount(); group++) {
                if (!matcher.find()) break;

                @NotNull String charset = matcher.group("charset");
                @Nullable Float weight = matcher.group("weight") != null ? Float.parseFloat(matcher.group("weight").replace(",", ".")) : null;

                pairs[group] = Weight.create(weight, PseudoCharset.create(charset));
            }

            return create(pairs);
        }

        @Override
        public @NotNull String write(@NotNull Header<Weight<PseudoCharset>[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Weight<PseudoCharset> pseudo : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(pseudo);
            }

            return builder.toString();
        }
    }
    private static final class AcceptCHHeaderKey extends HeaderKey<HeaderKey<?>[]> {
        private AcceptCHHeaderKey() {
            super("Accept-CH", Target.RESPONSE);
        }

        @Override
        public @NotNull Header<HeaderKey<?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Matcher matcher = Pattern.compile("\\s*,\\s*").matcher(value);
            @NotNull HeaderKey<?>[] keys = new HeaderKey[matcher.groupCount()];

            for (int group = 0; group < matcher.groupCount(); group++) {
                @NotNull String name = matcher.group(group);
                @NotNull HeaderKey<?> key = HeaderKey.create(name);

                if (key.getTarget() == Target.RESPONSE) {
                    // Ignore response headers
                    continue;
                }

                keys[group] = key;
            }

            return create(keys);
        }
        @Override
        public @NotNull String write(@NotNull Header<HeaderKey<?>[]> header) {
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
    }
    private static final class AcceptHeaderKey extends HeaderKey<MediaType[]> {
        private AcceptHeaderKey() {
            super("Accept", Target.REQUEST);
        }

        @Override
        public @NotNull Header<MediaType[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Matcher matcher = Pattern.compile("\\s*,\\s*").matcher(value);
            @NotNull List<MediaType> types = new LinkedList<>();

            try {
                for (int group = 0; group < matcher.groupCount(); group++) {
                    @NotNull String name = matcher.group(group);
                    types.add(MediaType.parse(name));
                }
            } catch (@NotNull ParseException e) {
                throw new HeaderFormatException("cannot parse Accept header's content types", e);
            }

            return create(types.toArray(new MediaType[0]));
        }
        @Override
        public @NotNull String write(@NotNull Header<MediaType[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull MediaType type : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(type);
            }

            return builder.toString();
        }
    }
    private static final class ContentTypeHeaderKey extends HeaderKey<MediaType> {
        private ContentTypeHeaderKey() {
            super("Content-Type", Target.BOTH);
        }

        @Override
        public @NotNull Header<MediaType> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            try {
                return create(MediaType.parse(value));
            } catch (@NotNull ParseException e) {
                throw new HeaderFormatException("cannot parse content type '" + value + "'", e);
            }
        }
        @Override
        public @NotNull String write(@NotNull Header<MediaType> header) {
            return header.getValue().toString();
        }
    }
    private static final class TransferEncodingHeaderKey extends HeaderKey<PseudoEncoding[]> {
        private TransferEncodingHeaderKey() {
            super("Transfer-Encoding", Target.BOTH);
        }

        @Override
        public @NotNull Header<PseudoEncoding[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Matcher matcher = Pattern.compile("\\s*,\\s*").matcher(value);
            @NotNull List<PseudoEncoding> encodings = new ArrayList<>();

            while (matcher.find()) {
                @NotNull String name = matcher.group(matcher.group());
                encodings.add(PseudoEncoding.create(name));
            }

            return create(encodings.toArray(new PseudoEncoding[0]));
        }
        @Override
        public @NotNull String write(@NotNull Header<PseudoEncoding[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull PseudoEncoding encoding : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(encoding.raw());
            }

            return builder.toString();
        }
    }

}
