package codes.laivy.jhttp.headers;

import codes.laivy.jhttp.authorization.Credentials;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.Target;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.parser.FilesystemProtocolException;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.module.*;
import codes.laivy.jhttp.module.connection.Connection;
import codes.laivy.jhttp.module.connection.EffectiveConnectionType;
import codes.laivy.jhttp.module.content.AcceptRange;
import codes.laivy.jhttp.module.content.ContentDisposition;
import codes.laivy.jhttp.module.content.ContentRange;
import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import codes.laivy.jhttp.network.BitMeasure;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.pseudo.PseudoString;
import codes.laivy.jhttp.pseudo.provided.PseudoCharset;
import codes.laivy.jhttp.pseudo.provided.PseudoEncoding;
import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.url.email.Email;
import codes.laivy.jhttp.utilities.DateUtils;
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

    public static @NotNull HeaderKey<?> create(@NotNull String name) {
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

        return new Provided.StringHeaderKey(name);
    }

    // Provided

    public static @NotNull HeaderKey<MediaType<?, ?>[]> ACCEPT = new Provided.AcceptHeaderKey();
    public static @NotNull HeaderKey<HeaderKey<?>[]> ACCEPT_CH = new Provided.AcceptCHHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<Duration> ACCEPT_CH_LIFETIME = new Provided.AcceptCHLifetimeHeaderKey();
    public static @NotNull HeaderKey<Weight<PseudoCharset>[]> ACCEPT_CHARSET = new Provided.AcceptCharsetHeaderKey();
    public static @NotNull HeaderKey<Weight<PseudoEncoding>[]> ACCEPT_ENCODING = new Provided.AcceptEncodingHeaderKey();
    public static @NotNull HeaderKey<Weight<Locale>[]> ACCEPT_LANGUAGE = new Provided.AcceptLanguageHeaderKey();
    public static @NotNull HeaderKey<MediaType<?, ?>[]> ACCEPT_PATCH = new Provided.AcceptPatchHeaderKey();
    public static @NotNull HeaderKey<MediaType.Type[]> ACCEPT_POST = new Provided.AcceptPostHeaderKey();
    public static @NotNull HeaderKey<AcceptRange> ACCEPT_RANGES = new Provided.AcceptRangesHeaderKey();
    public static @NotNull HeaderKey<Boolean> ACCEPT_CONTROL_ALLOW_CREDENTIALS = new Provided.BooleanHeaderKey("Access-Control-Allow-Credentials", Target.RESPONSE);
    public static @NotNull HeaderKey<Wildcard<PseudoString<HeaderKey<?>>>[]> ACCEPT_CONTROL_ALLOW_HEADERS = new Provided.AcceptControlAllowHeadersHeaderKey();
    public static @NotNull HeaderKey<Wildcard<Method>[]> ACCEPT_CONTROL_ALLOW_METHODS = new Provided.AccessControlAllowMethodsHeaderKey();
    public static @NotNull HeaderKey<Wildcard<@Nullable URIAuthority>> ACCEPT_CONTROL_ALLOW_ORIGIN = new Provided.AccessControlAllowOriginHeaderKey();
    public static @NotNull HeaderKey<Wildcard<PseudoString<HeaderKey<?>>>[]> ACCEPT_CONTROL_EXPOSE_HEADERS = new Provided.AcceptControlExposeHeadersHeaderKey();
    public static @NotNull HeaderKey<Duration> ACCEPT_CONTROL_MAX_AGE = new Provided.AcceptControlMaxAgeHeaderKey();
    public static @NotNull HeaderKey<Wildcard<PseudoString<HeaderKey<?>>>[]> ACCEPT_CONTROL_REQUEST_HEADERS = new Provided.AcceptControlRequestHeadersHeaderKey();
    public static @NotNull HeaderKey<Method[]> ACCEPT_CONTROL_REQUEST_METHOD = new Provided.AccessControlRequestMethodHeaderKey();
    public static @NotNull HeaderKey<Duration> AGE = new Provided.AgeHeaderKey();
    public static @NotNull HeaderKey<Method[]> ALLOW = new Provided.AllowHeaderKey();
    public static @NotNull HeaderKey<Optional<AlternativeService[]>> ALT_SVC = new Provided.AltSvcHeaderKey();
    public static @NotNull HeaderKey<URIAuthority> ALT_USED = new Provided.AltUsedHeaderKey();
    public static @NotNull HeaderKey<Credentials> AUTHORIZATION = new Provided.AuthorizationHeaderKey();
    public static @NotNull HeaderKey<CacheControl> CACHE_CONTROL = new Provided.CacheControlHeaderKey();
    public static @NotNull HeaderKey<Wildcard<SiteData>[]> CLEAR_SITE_DATA = new Provided.ClearSiteDataHeaderKey();
    public static @NotNull HeaderKey<Connection> CONNECTION = new Provided.ConnectionHeaderKey();
    public static @NotNull HeaderKey<ContentDisposition> CONTENT_DISPOSITION = new Provided.ContentDispositionHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<Float> CONTENT_DPR = new Provided.ContentDPRHeaderKey();
    // todo: there's a directives list allowed for this header
    public static @NotNull HeaderKey<PseudoEncoding[]> CONTENT_ENCODING = new Provided.ContentEncodingHeaderKey();
    public static @NotNull HeaderKey<Locale[]> CONTENT_LANGUAGE = new Provided.ContentLanguageHeaderKey();
    public static @NotNull HeaderKey<BitMeasure> CONTENT_LENGTH = new Provided.ContentLengthHeaderKey();
    public static @NotNull HeaderKey<Origin> CONTENT_LOCATION = new Provided.ContentLocationHeaderKey();
    public static @NotNull HeaderKey<ContentRange> CONTENT_RANGE = new Provided.ContentRangeHeaderKey();
    public static @NotNull HeaderKey<ContentSecurityPolicy> CONTENT_SECURITY_POLICY = new Provided.ContentSecurityPolicyHeaderKey();
    public static @NotNull HeaderKey<ContentSecurityPolicy> CONTENT_SECURITY_POLICY_REPORT_ONLY = new Provided.ContentSecurityPolicyReportOnlyeHeaderKey();
    public static @NotNull HeaderKey<MediaType<?, ?>> CONTENT_TYPE = new Provided.ContentTypeHeaderKey();
    public static @NotNull HeaderKey<Cookie[]> COOKIE = new Provided.CookieHeaderKey();
    public static @NotNull HeaderKey<HeaderKey<?>[]> CRITICAL_CH = new Provided.CriticalCHHeaderKey();
    public static @NotNull HeaderKey<CrossOrigin.EmbedderPolicy> CROSS_ORIGIN_EMBEDDER_POLICY = new Provided.CrossOriginEmbedderPolicyHeaderKey();
    public static @NotNull HeaderKey<CrossOrigin.OpenerPolicy> CROSS_ORIGIN_OPENER_POLICY = new Provided.CrossOriginOpenerPolicyHeaderKey();
    public static @NotNull HeaderKey<CrossOrigin.ResourcePolicy> CROSS_ORIGIN_RESOURCE_POLICY = new Provided.CrossOriginResourcePolicyHeaderKey();
    public static @NotNull HeaderKey<OffsetDateTime> DATE = new Provided.DateHeaderKey();
    public static @NotNull HeaderKey<Float> DEVICE_MEMORY = new Provided.DeviceMemoryHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<Boolean> DNT = new Provided.DNTHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<BitMeasure> DOWNLINK = new Provided.DownlinkHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<Float> DPR = new Provided.DPRHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<Void> EARLY_DATA = new Provided.EarlyDataHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<EffectiveConnectionType> ECT = new Provided.ECTHeaderKey();
    public static @NotNull HeaderKey<EntityTag> ETAG = new Provided.ETagHeaderKey();
    public static @NotNull HeaderKey<HttpStatus> EXPECT = new Provided.ExpectHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<ExpectCertificate> EXPECT_CT = new Provided.ExpectCTHeaderKey();
    public static @NotNull HeaderKey<OffsetDateTime> EXPIRES = new Provided.ExpiresHeaderKey();
    public static @NotNull HeaderKey<Forwarded> FORWARDED = new Provided.ForwardedHeaderKey();
    public static @NotNull HeaderKey<Email> FROM = new Provided.FromHeaderKey();
    public static @NotNull HeaderKey<Host> HOST = new Provided.HostHeaderKey();
    public static @NotNull HeaderKey<Wildcard<EntityTag[]>> IF_MATCH = new Provided.IfMatchHeaderKey();
    public static @NotNull HeaderKey<OffsetDateTime> IF_MODIFIED_SINCE = new Provided.IfModifiedSinceHeaderKey();
    public static @NotNull HeaderKey<Wildcard<EntityTag[]>> IF_NONE_MATCH = new Provided.IfNoneMatchHeaderKey();
    public static @NotNull HeaderKey<OffsetDateTime> IF_UNMODIFIED_SINCE = new Provided.IfUnmodifiedSinceHeaderKey();
    public static @NotNull HeaderKey<KeepAlive> KEEP_ALIVE = new Provided.KeepAliveHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<Optional<BitMeasure>> LARGE_ALLOCATION = new Provided.LargeAllocationHeaderKey();
    public static @NotNull HeaderKey<OffsetDateTime> LAST_MODIFIED = new Provided.LastModifiedHeaderKey();
    public static @NotNull HeaderKey<Origin> LOCATION = new Provided.LocationHeaderKey();
    public static @NotNull HeaderKey<Integer> MAX_FORWARDS = new Provided.MaxForwardsHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<NetworkErrorLogging> NEL = new Provided.NetworkErrorLoggingHeaderKey();
    public static @NotNull HeaderKey<@Nullable Host> ORIGIN = new Provided.OriginHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<Boolean> ORIGIN_AGENT_CLUSTER = new Provided.OriginAgentClusterHeaderKey();
    @Deprecated
    public static @NotNull HeaderKey<Void> PRAGMA = new Provided.PragmaHeaderKey();
    public static @NotNull HeaderKey<Credentials> PROXY_AUTHORIZATION = new Provided.ProxyAuthorizationHeaderKey();
    public static @NotNull HeaderKey<Origin> REFERER = new Provided.RefererHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<Duration> RTT = new Provided.RTTHeaderKey();
    public static @NotNull HeaderKey<Boolean> SAVE_DATA = new Provided.SaveDataHeaderKey();
    public static @NotNull HeaderKey<String> SERVER = new Provided.ServerHeaderKey();
    public static @NotNull HeaderKey<Cookie.Request> SET_COOKIE = new Provided.SetCookieHeaderKey();
    public static @NotNull HeaderKey<Origin> SOURCEMAP = new Provided.SourceMapHeaderKey();
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<Weight<PseudoEncoding>[]> TE = new Provided.TEHeaderKey();
    public static @NotNull HeaderKey<HeaderKey<?>[]> TRAILER = new Provided.TrailerHeaderKey();
    public static @NotNull HeaderKey<PseudoEncoding[]> TRANSFER_ENCODING = new Provided.TransferEncodingHeaderKey();
    public static @NotNull HeaderKey<Wildcard<HeaderKey<?>>[]> VARY = new Provided.VaryHeaderKey();
    // Object

    private final @NotNull String name;
    private final @NotNull Target target;

    protected HeaderKey(@NotNull String name, @NotNull Target target) {
        this.name = name;
        this.target = target;

        if (!NAME_FORMAT_REGEX.matcher(name).matches()) {
            throw new IllegalArgumentException("this header key name '" + name + "' have illegal characters");
        }
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
    public abstract @NotNull String write(@NotNull HttpVersion version, @NotNull Header<T> header);

    public @NotNull Header<T> create(@UnknownNullability T value) {
        return Header.create(this, value);
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

        private static final class StringHeaderKey extends HeaderKey<String> {
            private StringHeaderKey(@NotNull String name) {
                super(name, Target.BOTH);
            }

            @Override
            public @NotNull Header<String> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return Header.create(this, value);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<String> header) {
                return header.getValue();
            }
        }
        private static final class IntegerHeaderKey extends HeaderKey<Integer> {
            private IntegerHeaderKey(@NotNull String name, @NotNull Target target) {
                super(name, target);
            }

            @Override
            public @NotNull Header<Integer> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Integer.parseInt(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Integer> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Boolean> header) {
                return header.getValue().toString();
            }
        }

        private static final class VaryHeaderKey extends HeaderKey<Wildcard<HeaderKey<?>>[]> {
            private VaryHeaderKey() {
                super("Vary", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<Wildcard<HeaderKey<?>>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Matcher matcher = Pattern.compile("\\s*,\\s*").matcher(value);

                //noinspection unchecked
                @NotNull Wildcard<HeaderKey<?>>[] keys = new Wildcard[matcher.groupCount()];

                for (int group = 0; group < matcher.groupCount(); group++) {
                    @NotNull String name = matcher.group(group);
                    @NotNull Wildcard<HeaderKey<?>> key;

                    if (name.trim().equals("*")) key = Wildcard.create();
                    else key = Wildcard.create(HeaderKey.create(name));

                    keys[group] = key;
                }

                return create(keys);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<HeaderKey<?>>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Wildcard<HeaderKey<?>> key : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");

                    if (key.isWildcard()) builder.append("*");
                    else builder.append(key);
                }

                return builder.toString();
            }
        }
        private static final class TrailerHeaderKey extends HeaderKey<HeaderKey<?>[]> {
            private TrailerHeaderKey() {
                super("Trailer", Target.BOTH);
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
        }
        private static final class TEHeaderKey extends HeaderKey<Weight<PseudoEncoding>[]> {
            private TEHeaderKey() {
                super("TE", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Weight<PseudoEncoding>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Pattern pattern = Pattern.compile("(?<encoding>[^,;\\s]+)(?:\\s*;\\s*q\\s*=\\s*(?<weight>\\d(?:[.,]\\d)?))?");
                @NotNull Matcher matcher = pattern.matcher(value);

                //noinspection unchecked
                @NotNull Weight<PseudoEncoding>[] pairs = new Weight[matcher.groupCount()];

                for (int group = 0; group < matcher.groupCount(); group++) {
                    if (!matcher.find()) break;

                    @NotNull String string = matcher.group("encoding");
                    @Nullable Float weight = matcher.group("weight") != null ? Float.parseFloat(matcher.group("weight").replace(",", ".")) : null;

                    @NotNull Optional<Encoding> optional = Encoding.retrieve(string);
                    pairs[group] = optional.map(encoding -> Weight.create(weight, PseudoEncoding.createAvailable(encoding))).orElseGet(() -> Weight.create(weight, PseudoEncoding.createUnavailable(string)));
                }

                return create(pairs);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Weight<PseudoEncoding>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull Weight<PseudoEncoding> pseudo : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(pseudo);
                }

                return builder.toString();
            }
        }
        private static final class SourceMapHeaderKey extends HeaderKey<Origin> {
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
        private static final class SaveDataHeaderKey extends HeaderKey<Boolean> {
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
        private static final class RTTHeaderKey extends HeaderKey<Duration> {
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
        private static final class RefererHeaderKey extends HeaderKey<Origin> {
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
        private static final class ProxyAuthorizationHeaderKey extends HeaderKey<Credentials> {
            private ProxyAuthorizationHeaderKey() {
                super("Proxy-Authorization", Target.REQUEST);
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
        private static final class PragmaHeaderKey extends HeaderKey<Void> {
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
        private static final class OriginAgentClusterHeaderKey extends HeaderKey<Boolean> {
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
                    return create((Host) null);
                } else try {
                    return create(Host.parse(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<@Nullable Host> header) {
                return header.getValue().toString();
            }
        }
        private static final class NetworkErrorLoggingHeaderKey extends HeaderKey<NetworkErrorLogging> {
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
        private static final class ExpectCTHeaderKey extends HeaderKey<ExpectCertificate> {
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
                return header.getValue().toString();
            }
        }
        private static final class CriticalCHHeaderKey extends HeaderKey<HeaderKey<?>[]> {
            private CriticalCHHeaderKey() {
                super("Critical-CH", Target.RESPONSE);
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
        }
        private static final class ContentLengthHeaderKey extends HeaderKey<BitMeasure> {
            private ContentLengthHeaderKey() {
                super("Content-Language", Target.BOTH);
            }

            @Override
            public @NotNull Header<BitMeasure> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(BitMeasure.create(BitMeasure.Level.BYTES, Double.parseDouble(value)));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<BitMeasure> header) {
                return String.valueOf(header.getValue().getBytes());
            }
        }
        private static final class ContentLanguageHeaderKey extends HeaderKey<Locale[]> {
            private ContentLanguageHeaderKey() {
                super("Content-Language", Target.BOTH);
            }

            @Override
            public @NotNull Header<Locale[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Matcher matcher = Pattern.compile("\\s*,\\s*").matcher(value);
                @NotNull List<Locale> locales = new ArrayList<>();

                while (matcher.find()) {
                    @NotNull String name = matcher.group(matcher.group());
                    locales.add(Locale.forLanguageTag(name));
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
        }
        private static final class ContentDPRHeaderKey extends HeaderKey<Float> {
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
        }
        private static final class AcceptCHLifetimeHeaderKey extends HeaderKey<Duration> {
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
        private static final class AcceptControlMaxAgeHeaderKey extends HeaderKey<Duration> {
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
        }
        private static final class AgeHeaderKey extends HeaderKey<Duration> {
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
        }
        private static final class ServerHeaderKey extends HeaderKey<String> {
            private ServerHeaderKey() {
                super("Server", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<String> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return Header.create(this, value);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<String> header) {
                return header.getValue();
            }
        }
        private static final class SetCookieHeaderKey extends HeaderKey<Cookie.Request> {
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
                return header.getValue().toString();
            }
        }
        private static final class MaxForwardsHeaderKey extends HeaderKey<Integer> {
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
        private static final class LocationHeaderKey extends HeaderKey<Origin> {
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
        private static final class LastModifiedHeaderKey extends HeaderKey<OffsetDateTime> {
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
        private static final class LargeAllocationHeaderKey extends HeaderKey<Optional<BitMeasure>> {
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
        private static final class KeepAliveHeaderKey extends HeaderKey<KeepAlive> {
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
                return header.getValue().toString();
            }
        }
        private static final class IfUnmodifiedSinceHeaderKey extends HeaderKey<OffsetDateTime> {
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
        private static final class IfNoneMatchHeaderKey extends HeaderKey<Wildcard<EntityTag[]>> {
            private IfNoneMatchHeaderKey() {
                super("If-None-Match", Target.REQUEST);
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
                            tag.add(EntityTag.Parser.deserialize(matcher.group()));
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
                        builder.append(tag);
                    }

                    return builder.toString();
                }
            }
        }
        private static final class IfModifiedSinceHeaderKey extends HeaderKey<OffsetDateTime> {
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
                            tag.add(EntityTag.Parser.deserialize(matcher.group()));
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Host> header) {
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
                    return create(Email.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Email> header) {
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
                    return create(Forwarded.Parser.deserialize(value));
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Forwarded> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<OffsetDateTime> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<HttpStatus> header) {
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
                    return create(EntityTag.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<EntityTag> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<EffectiveConnectionType> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Void> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Float> header) {
                return header.getValue().toString();
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
        private static final class DNTHeaderKey extends HeaderKey<Boolean> {
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
        private static final class DeviceMemoryHeaderKey extends HeaderKey<Float> {
            private DeviceMemoryHeaderKey() {
                super("Device-Memory", Target.REQUEST);
            }

            @Override
            public @NotNull Header<Float> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                return create(Float.parseFloat(value));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Float> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<OffsetDateTime> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<CrossOrigin.ResourcePolicy> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<CrossOrigin.OpenerPolicy> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<CrossOrigin.EmbedderPolicy> header) {
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
                        @NotNull Cookie cookie = Cookie.Parser.deserialize(matcher.group());
                        cookies.add(cookie);
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
                    builder.append(cookie.getName()).append("=").append(cookie.getValue());
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<ContentSecurityPolicy> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<ContentSecurityPolicy> header) {
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
                    return create(ContentRange.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid content range", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<ContentRange> header) {
                return header.getValue().toString();
            }
        }
        private static final class ContentLocationHeaderKey extends HeaderKey<Origin> {
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
        private static final class ContentEncodingHeaderKey extends HeaderKey<PseudoEncoding[]> {
            private ContentEncodingHeaderKey() {
                super("Content-Encoding", Target.BOTH);
            }

            @Override
            public @NotNull Header<PseudoEncoding[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull List<PseudoEncoding> encodings = new ArrayList<>();

                for (@NotNull String name : value.split("\\s*,\\s*")) {
                    @NotNull Optional<Encoding> optional = Encoding.retrieve(name);
                    encodings.add(optional.map(PseudoEncoding::createAvailable).orElseGet(() -> PseudoEncoding.createUnavailable(name)));
                }

                return create(encodings.toArray(new PseudoEncoding[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<PseudoEncoding[]> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<ContentDisposition> header) {
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
                    return create(Connection.Parser.deserialize(value));
                } catch (ParseException e) {
                    throw new HeaderFormatException("cannot parse '" + value + "' into a valid connection", e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Connection> header) {
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
                        data.add(Wildcard.create(SiteData.getById(name)));
                    }
                }

                //noinspection unchecked
                return create(data.toArray(new Wildcard[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<SiteData>[]> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<CacheControl> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Credentials> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<URIAuthority> header) {
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
                    } catch (@NotNull ParseException | @NotNull UnknownHostException | @NotNull URISyntaxException e) {
                        throw new HeaderFormatException("cannot parse alternative service '" + group + "'", e);
                    }
                }

                return create(Optional.of(services));
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Method[]> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Method[]> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<PseudoString<HeaderKey<?>>>[]> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<PseudoString<HeaderKey<?>>>[]> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<@Nullable URIAuthority>> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<Method>[]> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Wildcard<PseudoString<HeaderKey<?>>>[]> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<AcceptRange> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<MediaType.Type[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull MediaType.Type type : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(type);
                }

                return builder.toString();
            }
        }
        private static final class AcceptPatchHeaderKey extends HeaderKey<MediaType<?, ?>[]> {
            private AcceptPatchHeaderKey() {
                super("Accept-Patch", Target.RESPONSE);
            }

            @Override
            public @NotNull Header<MediaType<?, ?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Pattern pattern = Pattern.compile("\\s*,\\s*");
                @NotNull Matcher matcher = pattern.matcher(value);
                @NotNull MediaType<?, ?>[] types = new MediaType[matcher.groupCount()];

                int row = 0;
                while (matcher.find()) {
                    try {
                        types[row] = MediaType.Parser.deserialize(matcher.group());
                    } catch (@NotNull ParseException e) {
                        throw new HeaderFormatException("cannot parse media type '" + matcher.group() + "'", e);
                    }

                    row++;
                }

                return create(types);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<MediaType<?, ?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull MediaType<?, ?> type : header.getValue()) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Weight<Locale>[]> header) {
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

                    @NotNull String string = matcher.group("encoding");
                    @Nullable Float weight = matcher.group("weight") != null ? Float.parseFloat(matcher.group("weight").replace(",", ".")) : null;

                    @NotNull Optional<Encoding> optional = Encoding.retrieve(string);
                    pairs[group] = optional.map(encoding -> Weight.create(weight, PseudoEncoding.createAvailable(encoding))).orElseGet(() -> Weight.create(weight, PseudoEncoding.createUnavailable(string)));
                }

                return create(pairs);
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Weight<PseudoEncoding>[]> header) {
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
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<Weight<PseudoCharset>[]> header) {
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
        }
        private static final class AcceptHeaderKey extends HeaderKey<MediaType<?, ?>[]> {
            private AcceptHeaderKey() {
                super("Accept", Target.REQUEST);
            }

            @Override
            public @NotNull Header<MediaType<?, ?>[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Matcher matcher = Pattern.compile("\\s*,\\s*").matcher(value);
                @NotNull List<MediaType<?, ?>> types = new LinkedList<>();

                try {
                    for (int group = 0; group < matcher.groupCount(); group++) {
                        @NotNull String name = matcher.group(group);

                        if (!name.isEmpty()) {
                            types.add(MediaType.Parser.deserialize(name));
                        }
                    }
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException("cannot parse Accept header's content types", e);
                }

                return create(types.toArray(new MediaType[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<MediaType<?, ?>[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull MediaType<?, ?> type : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(type);
                }

                return builder.toString();
            }
        }
        private static final class ContentTypeHeaderKey extends HeaderKey<MediaType<?, ?>> {
            private ContentTypeHeaderKey() {
                super("Content-Type", Target.BOTH);
            }

            @Override
            public @NotNull Header<MediaType<?, ?>> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                try {
                    return create(MediaType.Parser.deserialize(value));
                } catch (@NotNull ParseException e) {
                    throw new HeaderFormatException(e);
                }
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<MediaType<?, ?>> header) {
                return MediaType.Parser.serialize(header.getValue());
            }
        }
        private static final class TransferEncodingHeaderKey extends HeaderKey<PseudoEncoding[]> {
            private TransferEncodingHeaderKey() {
                super("Transfer-Encoding", Target.BOTH);
            }

            @Override
            public @NotNull Header<PseudoEncoding[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
                @NotNull Matcher matcher = Pattern.compile("\\s*,\\s*").matcher(value);
                @NotNull List<PseudoEncoding> encodings = new LinkedList<>();

                while (matcher.find()) {
                    @NotNull String name = matcher.group(matcher.group());

                    @NotNull Optional<Encoding> optional = Encoding.retrieve(name);
                    encodings.add(optional.map(PseudoEncoding::createAvailable).orElseGet(() -> PseudoEncoding.createUnavailable(name)));
                }

                return create(encodings.toArray(new PseudoEncoding[0]));
            }
            @Override
            public @NotNull String write(@NotNull HttpVersion version, @NotNull Header<PseudoEncoding[]> header) {
                @NotNull StringBuilder builder = new StringBuilder();

                for (@NotNull PseudoEncoding encoding : header.getValue()) {
                    if (builder.length() > 0) builder.append(", ");
                    builder.append(encoding.raw());
                }

                return builder.toString();
            }
        }
    }

}
