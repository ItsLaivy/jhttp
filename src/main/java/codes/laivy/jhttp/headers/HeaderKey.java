package codes.laivy.jhttp.headers;

import codes.laivy.jhttp.exception.HeaderFormatException;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.utilities.TransferEncoding;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HeaderKey<T> {

    // Static initializers

    public static final @NotNull Pattern NAME_FORMAT_REGEX = Pattern.compile("^[A-Za-z][A-Za-z0-9-]*$");

    public static @NotNull HeaderKey<?> create(@NotNull String name) {
        try {
            @NotNull Field field = HeaderKey.class.getDeclaredField(name.replace("-", "_").toLowerCase());
            return (HeaderKey<?>) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
        } catch (Throwable throwable) {
            throw new IllegalStateException("Cannot create header key '" + name + "'");
        }

        return new StringHeaderKey(name);
    }

    // Provided

    public static @NotNull HeaderKey<?> ACCEPT = new StringHeaderKey("Accept");
    public static @NotNull HeaderKey<?> ACCEPT_CH = new StringHeaderKey("Accept-CH");
    @Deprecated
    public static @NotNull HeaderKey<?> ACCEPT_CH_LIFETIME = new StringHeaderKey("Accept-CH-Lifetime", Pattern.compile("^\\d+$"));
    public static @NotNull HeaderKey<?> ACCEPT_CHARSET = new StringHeaderKey("Accept-Charset");
    public static @NotNull HeaderKey<?> ACCEPT_ENCODING = new StringHeaderKey("Accept-Encoding");
    public static @NotNull HeaderKey<?> ACCEPT_LANGUAGE = new StringHeaderKey("Accept-Language");
    public static @NotNull HeaderKey<?> ACCEPT_PATCH = new StringHeaderKey("Accept-Patch");

    /**
     * @see <a href="https://regexr.com/7sft5">RegExr Tests</a>
     * @apiNote Last change: 23/02/2024 | 16:36 (GMT-3)
     */
    public static @NotNull HeaderKey<?> ACCEPT_POST = new StringHeaderKey("Accept-Post", Pattern.compile("^(?i)([a-zA-Z0-9+-.*]+/[a-zA-Z0-9+-.*]+(, *)?)+$"));
    public static @NotNull HeaderKey<?> ACCEPT_RANGES = new StringHeaderKey("Accept-Ranges");
    public static @NotNull HeaderKey<?> ACCEPT_CONTROL_ALLOW_CREDENTIALS = new StringHeaderKey("Access-Control-Allow-Credentials");
    public static @NotNull HeaderKey<?> ACCEPT_CONTROL_ALLOW_HEADERS = new StringHeaderKey("Access-Control-Allow-Headers");
    public static @NotNull HeaderKey<?> ACCEPT_CONTROL_ALLOW_METHODS = new StringHeaderKey("Access-Control-Allow-Methods");
    public static @NotNull HeaderKey<?> ACCEPT_CONTROL_ALLOW_ORIGIN = new StringHeaderKey("Access-Control-Allow-Origin");
    public static @NotNull HeaderKey<?> ACCEPT_CONTROL_EXPOSE_HEADERS = new StringHeaderKey("Access-Control-Expose-Headers");
    public static @NotNull HeaderKey<?> ACCEPT_CONTROL_MAX_AGE = new StringHeaderKey("Access-Control-Max-Age");
    public static @NotNull HeaderKey<?> ACCEPT_CONTROL_REQUEST_HEADERS = new StringHeaderKey("Access-Control-Request-Headers");
    public static @NotNull HeaderKey<?> ACCEPT_CONTROL_REQUEST_METHOD = new StringHeaderKey("Access-Control-Request-Method");
    public static @NotNull HeaderKey<?> AGE = new StringHeaderKey("Age", Pattern.compile("^\\d+$"));

    /**
     * @see <a href="https://regexr.com/7sftn">RegExr Tests</a>
     * @apiNote Last change: 23/02/2024 | 19:38 (GMT-3)
     */
    public static @NotNull HeaderKey<?> ALLOW = new StringHeaderKey("Allow", Pattern.compile("^(?i)(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|TRACE|CONNECT)(,[ ]?(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|TRACE|CONNECT))*?$"));
    public static @NotNull HeaderKey<?> ALT_SVC = new StringHeaderKey("Alt-Svc");
    public static @NotNull HeaderKey<?> ALT_USED = new StringHeaderKey("Alt-Used");
    public static @NotNull HeaderKey<?> AUTHORIZATION = new StringHeaderKey("Authorization");
    public static @NotNull HeaderKey<?> CACHE_CONTROL = new StringHeaderKey("Cache-Control");
    public static @NotNull HeaderKey<?> CLEAR_SITE_DATA = new StringHeaderKey("Clear-Site-Data");
    public static @NotNull HeaderKey<?> CONNECTION = new StringHeaderKey("Connection", Pattern.compile("^(?i)(keep-alive|close)(,\\s?[a-zA-Z0-9!#$%&'*+.^_`|~-]+)*$"));
    public static @NotNull HeaderKey<?> CONTENT_DISPOSITION = new StringHeaderKey("Content-Disposition");
    @Deprecated
    public static @NotNull HeaderKey<?> CONTENT_DPR = new StringHeaderKey("Content-DPR");
    public static @NotNull HeaderKey<?> CONTENT_ENCODING = new StringHeaderKey("Content-Encoding");
    public static @NotNull HeaderKey<?> CONTENT_LANGUAGE = new StringHeaderKey("Content-Language");
    public static @NotNull HeaderKey<?> CONTENT_LENGTH = new StringHeaderKey("Content-Length");
    public static @NotNull HeaderKey<?> CONTENT_LOCATION = new StringHeaderKey("Content-Location");
    public static @NotNull HeaderKey<?> CONTENT_RANGE = new StringHeaderKey("Content-Range");
    public static @NotNull HeaderKey<?> CONTENT_SECURITY_POLICY = new StringHeaderKey("Content-Security-Policy");
    public static @NotNull HeaderKey<?> CONTENT_SECURITY_POLICY_REPORT_ONLY = new StringHeaderKey("Content-Security-Policy-Report-Only");
    /**
     * @see <a href="https://regexr.com/7sfu0">RegExr Tests</a>
     * @apiNote Last change: 23/02/2024 | 19:06 (GMT-3)
     */
    public static @NotNull HeaderKey<?> CONTENT_TYPE = new StringHeaderKey("Content-Type", Pattern.compile("^[a-zA-Z0-9+-.*]+/[a-zA-Z0-9+-.*]+(?:; ?(boundary=[a-zA-Z0-9-]+|charset=[a-zA-Z0-9-]+))?(?:; ?(boundary=[a-zA-Z0-9-]+|charset=[a-zA-Z0-9-]+))?$"));
    public static @NotNull HeaderKey<?> COOKIE = new StringHeaderKey("Cookie");
    public static @NotNull HeaderKey<?> CRITICAL_CH = new StringHeaderKey("Critical-CH");
    public static @NotNull HeaderKey<?> CROSS_ORIGIN_EMBEDDER_POLICY = new StringHeaderKey("Cross-Origin-Embedder-Policy");
    public static @NotNull HeaderKey<?> CROSS_ORIGIN_OPENER_POLICY = new StringHeaderKey("Cross-Origin-Opener-Policy");
    public static @NotNull HeaderKey<?> CROSS_ORIGIN_RESOURCE_POLICY = new StringHeaderKey("Cross-Origin-Resource-Policy");
    /**
     * @see <a href="https://regexr.com/7sgub">RegExr Tests</a>
     * @apiNote Last change: 25/02/2024 | 01:43 (GMT-3)
     */
    public static @NotNull HeaderKey<?> DATE = new StringHeaderKey("Date", Pattern.compile("(Mon|Tue|Wed|Thu|Fri|Sat|Sun), ([0-2][0-9]|3[0-1]) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) (19[0-9]{2}|20[0-9]{2}) ([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9] GMT"));
    public static @NotNull HeaderKey<?> DEVICE_MEMORY = new StringHeaderKey("Device-Memory");
    @Deprecated
    public static @NotNull HeaderKey<?> DIGEST = new StringHeaderKey("Digest");
    @Deprecated
    public static @NotNull HeaderKey<?> DNT = new StringHeaderKey("DNT");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> DOWNLINK = new StringHeaderKey("Downlink");
    @Deprecated
    public static @NotNull HeaderKey<?> DPR = new StringHeaderKey("DPR");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> EARLY_DATA = new StringHeaderKey("Early-Data");
    @ApiStatus.Experimental
    public static @NotNull HeaderKey<?> ECT = new StringHeaderKey("ECT");
    public static @NotNull HeaderKey<?> ETAG = new StringHeaderKey("ETag");
    public static @NotNull HeaderKey<?> EXPECT = new StringHeaderKey("Expect");
    public static @NotNull HeaderKey<?> EXPECT_CT = new StringHeaderKey("Expect-CT");
    public static @NotNull HeaderKey<?> EXPIRES = new StringHeaderKey("Expires");
    public static @NotNull HeaderKey<?> FORWARDED = new StringHeaderKey("Forwarded");
    public static @NotNull HeaderKey<?> FROM = new StringHeaderKey("From");
    public static @NotNull HeaderKey<?> HOST = new StringHeaderKey("Host");
    public static @NotNull HeaderKey<?> IF_MATCH = new StringHeaderKey("If-Match");
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
    public static @NotNull HeaderKey<TransferEncoding[]> TRANSFER_ENCODING = new TransferEncodingHeaderKey();
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

    private HeaderKey(@NotNull String name) {
        this.name = name;
    }

    @Contract(pure = true)
    public @NotNull String getName() {
        return name;
    }

    // Modules

    public abstract @NotNull Header<T> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException;
    public abstract @NotNull String write(@NotNull Header<T> header);

    // Implementations

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof HeaderKey)) return false;
        HeaderKey<?> that = (HeaderKey<?>) object;
        return getName().equalsIgnoreCase(that.getName());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getName().toLowerCase());
    }
    @Override
    public @NotNull String toString() {
        return getName();
    }

    // Classes

    private static final class StringHeaderKey extends HeaderKey<String> {

        private final @Nullable Pattern pattern;

        private StringHeaderKey(@NotNull String name) {
            super(name);
            this.pattern = null;
        }
        private StringHeaderKey(@NotNull String name, @Nullable Pattern pattern) {
            super(name);
            this.pattern = pattern;
        }

        // Getters

        public @Nullable Pattern getPattern() {
            return pattern;
        }

        // Modules

        @Override
        public @NotNull Header<String> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            if (getPattern() != null && !getPattern().matcher(value).matches()) {
                throw new HeaderFormatException("the header key '" + getName() + "' value '" + value + "' doesn't matches with the regex '" + getPattern() + "'");
            }

            return Header.create(this, value);
        }
        @Override
        public @NotNull String write(@NotNull Header<String> header) {
            return header.getValue();
        }
    }
    private static final class TransferEncodingHeaderKey extends HeaderKey<TransferEncoding[]> {
        private TransferEncodingHeaderKey() {
            super("Transfer-Encoding");
        }

        @Override
        public @NotNull Header<TransferEncoding[]> read(@NotNull HttpVersion version, @NotNull String value) throws HeaderFormatException {
            @NotNull Matcher matcher = Pattern.compile("\\s*,\\s*").matcher(value);
            @NotNull TransferEncoding[] encodings = new TransferEncoding[matcher.groupCount()];

            for (int group = 0; group < matcher.groupCount(); group++) {
                @NotNull String encodingValue = matcher.group(group);
                @NotNull Optional<TransferEncoding> optional = Arrays.stream(TransferEncoding.getEncodings()).filter(encoding -> encoding.getName().equalsIgnoreCase(encodingValue)).findFirst();
                encodings[group] = optional.orElseThrow(() -> new HeaderFormatException("unknown transfer encoding with name '" + encodingValue + "'"));
            }

            return Header.create(this, encodings);
        }
        @Override
        public @NotNull String write(@NotNull Header<TransferEncoding[]> header) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull TransferEncoding encoding : header.getValue()) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(encoding.getName());
            }

            return builder.toString();
        }
    }

}
