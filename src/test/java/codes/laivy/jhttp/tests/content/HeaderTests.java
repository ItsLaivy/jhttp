package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.authorization.Credentials;
import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.headers.Header;
import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.headers.Weight;
import codes.laivy.jhttp.headers.Wildcard;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.module.*;
import codes.laivy.jhttp.module.UserAgent.Product;
import codes.laivy.jhttp.module.connection.EffectiveConnectionType;
import codes.laivy.jhttp.module.content.AcceptRange;
import codes.laivy.jhttp.network.BitMeasure;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.url.email.Email;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;

import java.nio.charset.Charset;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class HeaderTests {

    private HeaderTests() {
    }

    // Tests

    @Nested
    final class Accept extends HeaderTest<MediaType<?>[]> {
        private Accept() {
            super(
                    HeaderKey.ACCEPT,

                    new String[] {
                            "*/*",
                            "application/json",
                            "text/html, application/xhtml+xml, application/xml;q=0.9, image/webp, */*;q=0.8",
                            "text/html   ,   application/xhtml+xml     ,   application   /  xml   ;   q   =   0.9   ,   image  /   webp, *  /  *  ;  q   =  0.8"
                    }, new String[] {
                            "",
                            "text",
                            "dawdawda",
                            "wdafagf, fawfawf, application/json"
                    }
            );
        }
    }
    @Nested
    final class AcceptCH extends HeaderTest<HeaderKey<?>[]> {
        private AcceptCH() {
            super(
                    HeaderKey.ACCEPT_CH,

                    new String[] {
                            "DPR",
                            "DPR   ,  Viewport-Width",
                            "DPR,Viewport-Width",
                    }, new String[] {
                            "",
                            "DPR, ",
                            "DPR; ",
                            "DPR; Viewport-Width",
                    }
            );
        }
    }
    @SuppressWarnings("deprecation")
    @Nested
    final class AcceptCHLifetime extends HeaderTest<Duration> {
        private AcceptCHLifetime() {
            super(
                    HeaderKey.ACCEPT_CH_LIFETIME,

                    new String[] {
                            "86400",
                            "60",
                            "85134421"
                    }, new String[] {
                            "",
                            "any text"
                    }
            );
        }
    }
    @Nested
    final class AcceptCharset extends HeaderTest<Weight<Deferred<Charset>>[]> {
        private AcceptCharset() {
            super(
                    HeaderKey.ACCEPT_CHARSET,

                    new String[] {
                            "utf-8",
                            "utf-8  ,   iso-8859-1  ;  q  =  0.5",
                            "utf-8, iso-8859-1;q=0.5"
                    }, new String[] {
                            "",
                            "any text;,utf-8,"
                    }
            );
        }
    }
    @Nested
    final class AcceptEncoding extends HeaderTest<Wildcard<Weight<Deferred<Encoding>>[]>> {
        private AcceptEncoding() {
            super(
                    HeaderKey.ACCEPT_ENCODING,

                    new String[] {
                            "*",
                            "deflate",
                            "deflate;q=1.0",
                            "deflate  ,  gzip   ; q  =  1.0  , *  ;  q  =  0.5",
                            "deflate, gzip;q=1.0, *;q=0.5",
                    }, new String[] {
                            "",
                            "deflate,",
                            "deflate,gzip;,"
                    }
            );
        }
    }
    @Nested
    final class AcceptLanguage extends HeaderTest<Wildcard<Weight<Locale>[]>> {
        private AcceptLanguage() {
            super(
                    HeaderKey.ACCEPT_LANGUAGE,

                    new String[] {
                            "*",
                            "en_US",
                            "fr-CH  ,   fr  ;  q  =  0.9  ,   en  ;  q  =  0.8  ,  de ;  q = 0.7 ,  * ; q  =  0.5",
                            "fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5",
                    }, new String[] {
                            "",
                            "fr-CH,",
                            "fr-CH;,fr"
                    }
            );
        }
    }
    @Nested
    final class AcceptPatch extends HeaderTest<MediaType<?>[]> {
        private AcceptPatch() {
            super(
                    HeaderKey.ACCEPT_PATCH,

                    new String[] {
                            "application/example, text/example",
                            "text/example;charset=utf-8",
                            "application/merge-patch+json",
                            "text/example  ;  charset=utf-8  ,  application/json",
                            "application/example, text/example",
                    }, new String[] {
                            "",
                            "text/example,",
                    }
            );
        }
    }
    @Nested
    final class AcceptPost extends HeaderTest<MediaType.Type[]> {
        private AcceptPost() {
            super(
                    HeaderKey.ACCEPT_POST,

                    new String[] {
                            "application/example, text/example",
                            "image/webp",
                            "*/*",
                    }, new String[] {
                            "",
                            "*/*,",
                    }
            );
        }
    }
    @Nested
    final class AcceptRanges extends HeaderTest<AcceptRange> {
        private AcceptRanges() {
            super(
                    HeaderKey.ACCEPT_RANGES,

                    new String[] {
                            "none",
                            "bytes",
                            "NoNe",
                            "ByTeS"
                    }, new String[] {
                            "fdawda",
                            ""
                    }
            );
        }
    }
    @Nested
    final class AcceptControlAllowCredentials extends HeaderTest<Boolean> {
        private AcceptControlAllowCredentials() {
            super(
                    HeaderKey.ACCEPT_CONTROL_ALLOW_CREDENTIALS,

                    new String[] {
                            "true",
                            "false",
                            "TrUe",
                            "fAlSe"
                    }, new String[] {
                            "fdawda",
                            ""
                    }
            );
        }
    }
    @Nested
    final class AcceptControlAllowHeaders extends HeaderTest<Wildcard<HeaderKey<?>[]>> {
        private AcceptControlAllowHeaders() {
            super(
                    HeaderKey.ACCEPT_CONTROL_ALLOW_HEADERS,

                    new String[] {
                            "X-Custom-Header",
                            "X-Custom-Header  ,   Upgrade-Insecure-Requests, Accept",
                            "X-Custom-Header, Upgrade-Insecure-Requests",
                            "content-type,x-requested-with",
                    }, new String[] {
                            ""
                    }
            );
        }
    }
    @Nested
    final class AcceptControlAllowMethods extends HeaderTest<Wildcard<Method[]>> {
        private AcceptControlAllowMethods() {
            super(
                    HeaderKey.ACCEPT_CONTROL_ALLOW_METHODS,

                    new String[] {
                            "GET, POST, OPTIONS",
                            "get    ,   post",
                            "get,head,post,put,delete,connect,options,trace,patch",
                            "*"
                    }, new String[] {
                            "fdawda",
                            "awda, wdad a, dwad",
                            ""
                    }
            );
        }
    }
    @Nested
    final class AcceptControlAllowOrigin extends HeaderTest<Wildcard<@Nullable URIAuthority>> {
        private AcceptControlAllowOrigin() {
            super(
                    HeaderKey.ACCEPT_CONTROL_ALLOW_ORIGIN,

                    new String[] {
                            "https://developer.mozilla.org",
                            "null",
                            "NULL",
                            "*"
                    }, new String[] {
                            "fdawda",
                            "awda, wdad a, dwad",
                            ""
                    }
            );
        }
    }
    @Nested
    final class AcceptControlExposeHeaders extends HeaderTest<Wildcard<HeaderKey<?>[]>> {
        private AcceptControlExposeHeaders() {
            super(
                    HeaderKey.ACCEPT_CONTROL_EXPOSE_HEADERS,

                    new String[] {
                            "Content-Encoding",
                            "Content-Encoding, Kuma-Revision",
                            "*"
                    }, new String[] {
                            "",
                            "Content-Encoding; Kuma-Revision",
                    }
            );
        }
    }
    @Nested
    final class AcceptControlMaxAge extends HeaderTest<Duration> {
        private AcceptControlMaxAge() {
            super(
                    HeaderKey.ACCEPT_CONTROL_MAX_AGE,

                    new String[] {
                            "86400",
                            "60",
                            "85132421"
                    }, new String[] {
                            "",
                            "any text"
                    }
            );
        }
    }
    @Nested
    final class AcceptControlHeadersImpl extends HeaderTest<HeaderKey<?>[]> {
        private AcceptControlHeadersImpl() {
            super(
                    HeaderKey.ACCEPT_CONTROL_REQUEST_HEADERS,

                    new String[] {
                            "content-type,x-pingother",
                            "content-type",
                    }, new String[] {
                            "",
                            "*",
                            "Content-Type,X-Pingother",
                            "content-type;x-pingother"
                    }
            );
        }
    }
    @Nested
    final class AcceptControlRequestMethod extends HeaderTest<Method> {
        private AcceptControlRequestMethod() {
            super(
                    HeaderKey.ACCEPT_CONTROL_REQUEST_METHOD,

                    new String[] {
                            "get",
                            "head",
                            "post",
                            "put",
                            "delete",
                            "connect",
                            "options",
                            "trace",
                            "patch"
                    }, new String[] {
                            "",
                            "*",
                            "dawd"
                    }
            );
        }
    }
    @Nested
    final class Age extends HeaderTest<Duration> {
        private Age() {
            super(
                    HeaderKey.AGE,

                    new String[] {
                            "86400",
                            "60",
                            "85134421"
                    }, new String[] {
                            "",
                            "any text"
                    }
            );
        }
    }
    @Nested
    final class Allow extends HeaderTest<Method[]> {
        private Allow() {
            super(
                    HeaderKey.ALLOW,

                    new String[] {
                            "GET, POST, OPTIONS",
                            "get    ,   post",
                            "get,head,post,put,delete,connect,options,trace,patch"
                    }, new String[] {
                            "fdawda",
                            "awda, wdad a, dwad",
                            "*",
                            ""
                    }
            );
        }
    }
    @Nested
    final class AltSvc extends HeaderTest<Optional<AlternativeService[]>> {
        private AltSvc() {
            super(
                    HeaderKey.ALT_SVC,

                    new String[] {
                            "clear",
                            "ClEaR",
                            "h2=\":443\"; ma=2592000;",
                            "h2=\":443\"; ma=2592000; persist=1",
                            "h2=\"alt.example.com:443\", h2=\":443\"",
                            "h3-25=\":443\"; ma=3600, h2=\":443\"; ma=3600",
                            "h3-25=\":443\"; ma=3600, h2=\":443\"; ma=3600; persist=1;",
                    }, new String[] {
                            "",
                            "*",
                            "h3-25=\":443\"; mad=3600, h2=\":443\"; ma=3600; persist=1;"
                    }
            );
        }
    }
    @Nested
    final class AltUsed extends HeaderTest<URIAuthority> {
        private AltUsed() {
            super(
                    HeaderKey.ALT_USED,

                    new String[] {
                            "alternate.example.net",
                            "alternate.example.net:80",
                            "localhost:80",
                            "localhost",
                    }, new String[] {
                            "192.168.0.1",
                            "",
                    }
            );
        }
    }
    @Nested
    final class Authorization extends HeaderTest<Credentials> {
        private Authorization() {
            super(
                    HeaderKey.AUTHORIZATION,

                    new String[] {
                            "Basic username:password",
                            "Bearer testToken12345",
                    }, new String[] {
                            "Basic",
                            "",
                    }
            );
        }
    }
    @Nested
    final class CacheControl extends HeaderTest<codes.laivy.jhttp.module.CacheControl> {
        private CacheControl() {
            super(
                    HeaderKey.CACHE_CONTROL,

                    new String[] {
                            "max-age=60,public,private",
                            "max-age=60,s-maxage=60,no-cache,must-revalidate,proxy-revalidate,no-store,private,public,must-understand,no-transform,immutable,stale-while-revalidate=60,stale-if-error=60,no-cache,max-stale=60,min-fresh=60,no-transform,only-if-cached", // Check all keys
                            "max-age   =   60   ,   public  ,   private"
                    }, new String[] {
                            "max-age=60;public",
                            "",
                    }
            );
        }
    }
    @Nested
    final class ClearSiteData extends HeaderTest<Wildcard<SiteData[]>> {
        private ClearSiteData() {
            super(
                    HeaderKey.CLEAR_SITE_DATA,

                    new String[] {
                            "\"cache\"",
                            "\"cache\", \"cookies\"",
                            "\"*\"",
                    }, new String[] {
                            "cache, cookies",
                            "*",
                            "",
                    }
            );
        }
    }
    @Nested
    final class Connection extends HeaderTest<codes.laivy.jhttp.module.connection.Connection> {
        private Connection() {
            super(
                    HeaderKey.CONNECTION,

                    new String[] {
                            "keep-alive",
                            "close",
                            "keEP-ALIve",
                            "CloSe",
                            "keep-alive, Proxy-Authenticate",
                            "keep-alive,Upgrade  , TE, Trailers",
                            "keep-alive",
                            "upgrade",
                            "close",
                            "close  ,  Proxy-Authenticate    , TE  ,  Trailers"
                    }, new String[] {
                            "",
                            "*",
                            "diwmda",
                            "vary"
                    }
            );
        }
    }
    @Nested
    final class ContentDisposition extends HeaderTest<codes.laivy.jhttp.module.content.ContentDisposition> {
        private ContentDisposition() {
            super(
                    HeaderKey.CONTENT_DISPOSITION,

                    new String[] {
                            "inline",
                            "attachment",
                            "attachment; filename=\"filename.jpg\"",
                            "attachment; filename*=\"filename.jpg\"",
                            "attachment; filename=\"example.txt\";",
                            "inline; filename=\"example.txt\";",
                            "form-data; filename=\"example.txt\"; size=12345",
                            "attachment  ;   name    =  \"teste\"  ; filename  =  \"example.txt\"    ;   creation-date  = \"Wed, 12 Feb 1997 16:29:51 -0500\"; read-date  = \"Wed, 12 Feb 1997 16:29:51 -0500\"; modification-date=\"Wed, 12 Feb 1997 16:29:51 -0500\"; size  =  12345",
                    }, new String[] {
                            "",
                            "wdad",
                            "form-data; filename=\"example.txt\"; size=12345,",
                    }
            );
        }
    }
    @SuppressWarnings("deprecation")
    @Nested
    final class ContentDPR extends HeaderTest<Float> {
        private ContentDPR() {
            super(
                    HeaderKey.CONTENT_DPR,

                    new String[] {
                            "0.1",
                            "2.0",
                    }, new String[] {
                            "2,0",
                            "",
                            "dawd"
                    }
            );
        }
    }
    @Nested
    final class ContentEncoding extends HeaderTest<Deferred<Encoding>[]> {
        private ContentEncoding() {
            super(
                    HeaderKey.CONTENT_ENCODING,

                    new String[] {
                            "gzip",
                            "compress",
                            "deflate",
                            "br",
                            "zstd",
                            "deflate, gzip, compress",
                            "deflate  ,   gzip   ,   compress"
                    }, new String[] {
                            "",
                            "deflate;gzip",
                            "deflate; gzip"
                    }
            );
        }
    }
    @Nested
    final class ContentLanguage extends HeaderTest<Locale[]> {
        private ContentLanguage() {
            super(
                    HeaderKey.CONTENT_LANGUAGE,

                    new String[] {
                            "de-DE",
                            "de-US",
                            "de-US, en-CA",
                            "de-US  ,  en-CA",
                    }, new String[] {
                            "",
                            "de-US; en-CA",
                    }
            );
        }
    }
    @Nested
    final class ContentLength extends HeaderTest<BitMeasure> {
        private ContentLength() {
            super(
                    HeaderKey.CONTENT_LENGTH,

                    new String[] {
                            "2412424214",
                            "0",
                    }, new String[] {
                            "",
                            "adawd",
                            "1.0",
                    }
            );
        }
    }
    @Nested
    final class ContentLocation extends HeaderTest<codes.laivy.jhttp.module.Origin> {
        private ContentLocation() {
            super(
                    HeaderKey.CONTENT_LOCATION,

                    new String[] {
                            "/my-first-blog-post",
                            "/index.php",
                            "http://example.com/documents/1234",
                            "localhost:80/test",
                            "https://localhost:501/test",
                            "/just/test"
                    }, new String[] {
                            "1",
                    }
            );
        }
    }
    @Nested
    final class ContentRange extends HeaderTest<codes.laivy.jhttp.module.content.ContentRange> {
        private ContentRange() {
            super(
                    HeaderKey.CONTENT_RANGE,

                    new String[] {
                            "bytes 200-1000/67589",
                            "bytes 200-1000/*",
                            "bytes */2313",
                    }, new String[] {
                            "bytes */*",
                            "bytes 231-2424",
                            "bytes 100-50/2323",
                            "",
                    }
            );
        }
    }
    @Nested
    final class ContentSecurityPolicy extends HeaderTest<codes.laivy.jhttp.module.content.ContentSecurityPolicy> {
        private ContentSecurityPolicy() {
            super(
                    HeaderKey.CONTENT_SECURITY_POLICY,

                    new String[]{
                            "default-src https:",
                            "default-src https: 'unsafe-eval' 'unsafe-inline'; object-src 'none'",
                            "default-src https:; report-uri /csp-violation-report-endpoint/",
                    }, new String[]{
                            "",
                    }
            );
        }
    }
    @Nested
    final class ContentSecurityPolicyReportOnly extends HeaderTest<codes.laivy.jhttp.module.content.ContentSecurityPolicy> {
        private ContentSecurityPolicyReportOnly() {
            super(
                    HeaderKey.CONTENT_SECURITY_POLICY_REPORT_ONLY,

                    new String[]{
                            "default-src https:",
                            "default-src https: 'unsafe-eval' 'unsafe-inline'; object-src 'none'",
                            "default-src https:; report-uri /csp-violation-report-endpoint/",
                    }, new String[]{
                            "",
                    }
            );
        }
    }
    @Nested
    final class ContentType extends HeaderTest<MediaType<?>> {
        private ContentType() {
            super(
                    HeaderKey.CONTENT_TYPE,

                    new String[] {
                            "text/html; charset=utf-8",
                            "multipart/form-data; boundary=something",
                            "application/json",
                    }, new String[] {
                            "text/html, application/json",
                            "adawdad",
                            "",
                    }
            );
        }
    }
    @Nested
    final class Cookie extends HeaderTest<codes.laivy.jhttp.module.Cookie[]> {
        private Cookie() {
            super(
                    HeaderKey.COOKIE,

                    new String[] {
                            "cookie_name=cookie_value",
                            "cookie_name=cookie_value;cookie_name_2=cookie_vlaue",
                            "cookie_name   =   cookie_value",
                            "cookie_name   =   cookie_value;cookie_name_2   = cookie_value",
                    }, new String[] {
                            "cookie name=cookie value",
                            "cookie_name",
                            "cookie_name=",
                            "cookie_name=cookie_value,cookie_name_2=cookie_vlaue",
                            "dwdada",
                            "",
                    }
            );
        }
    }
    @Nested
    final class CriticalCH extends HeaderTest<HeaderKey<?>[]> {
        private CriticalCH() {
            super(
                    HeaderKey.CRITICAL_CH,

                    new String[] {
                            "Sec-CH-Prefers-Reduced-Motion",
                            "Sec-CH-Prefers-Reduced-Motion,DPR",
                            "Sec-CH-Prefers-Reduced-Motion   ,  DPR",
                    }, new String[] {
                            "",
                            "Sec-CH-Prefers-Reduced-Motion,Test",
                            "Sec-CH-Prefers-Reduced-Motion;DPR",
                    }
            );
        }
    }
    @Nested
    final class CrossOriginEmbedderPolicy extends HeaderTest<CrossOrigin.EmbedderPolicy> {
        private CrossOriginEmbedderPolicy() {
            super(
                    HeaderKey.CROSS_ORIGIN_EMBEDDER_POLICY,

                    new String[] {
                            "credentialless",
                            "require-corp",
                            "unsafe-none",
                            "UnSafE-NONE",
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class CrossOriginOpenerPolicy extends HeaderTest<CrossOrigin.OpenerPolicy> {
        private CrossOriginOpenerPolicy() {
            super(
                    HeaderKey.CROSS_ORIGIN_OPENER_POLICY,

                    new String[] {
                            "unsafe-none",
                            "same-origin-allow-popups",
                            "same-origin",
                            "SaMe-ORIGIN",
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class CrossOriginResourcePolicy extends HeaderTest<CrossOrigin.ResourcePolicy> {
        private CrossOriginResourcePolicy() {
            super(
                    HeaderKey.CROSS_ORIGIN_RESOURCE_POLICY,

                    new String[] {
                            "same-site",
                            "same-origin",
                            "cross-origin",
                            "CrOsS-ORIGIN",
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class Date extends HeaderTest<OffsetDateTime> {
        private Date() {
            super(
                    HeaderKey.DATE,

                    new String[] {
                            "Wed, 12 Feb 1997 16:29:51 -0500"
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class DeviceMemory extends HeaderTest<BitMeasure> {
        private DeviceMemory() {
            super(
                    HeaderKey.DEVICE_MEMORY,

                    new String[] {
                            "0.25",
                            "0.5",
                            "1",
                            "2",
                            "4",
                            "8",
                    }, new String[] {
                            "",
                            "31231.0",
                            "kkijkl",
                    }
            );
        }
    }
    @SuppressWarnings("deprecation")
    @Nested
    final class DNT extends HeaderTest<Boolean> {
        private DNT() {
            super(
                    HeaderKey.DNT,

                    new String[] {
                            "0",
                            "1",
                            "null",
                            "NuLl",
                    }, new String[] {
                            "",
                            "true",
                            "false",
                    }
            );
        }
    }
    @Nested
    final class Downlink extends HeaderTest<BitMeasure> {
        private Downlink() {
            super(
                    HeaderKey.DOWNLINK,

                    new String[] {
                            "1.7",
                            "23",
                            "0.9",
                    }, new String[] {
                            "",
                            "wdoakdo",
                    }
            );
        }
    }
    @SuppressWarnings("deprecation")
    @Nested
    final class DPR extends HeaderTest<Float> {
        private DPR() {
            super(
                    HeaderKey.DPR,

                    new String[] {
                            "2.0",
                            "0.5",
                    }, new String[] {
                            "",
                            "dawd",
                    }
            );
        }
    }
    @Nested
    final class EarlyData extends HeaderTest<Void> {
        private EarlyData() {
            super(
                    HeaderKey.EARLY_DATA,

                    new String[] {
                            "1",
                    }, new String[] {
                            "",
                            "0",
                            "dawd",
                    }
            );
        }
    }
    @Nested
    final class ECT extends HeaderTest<EffectiveConnectionType> {
        private ECT() {
            super(
                    HeaderKey.ECT,

                    new String[] {
                            "slow-2g",
                            "2g",
                            "3g",
                            "4g",
                            "4G",
                            "SLOW-2G",
                    }, new String[] {
                            "",
                            "dwadawd",
                    }
            );
        }
    }
    @Nested
    final class ETag extends HeaderTest<EntityTag> {
        private ETag() {
            super(
                    HeaderKey.ETAG,

                    new String[] {
                            "W/\"cool\"",
                            "\"cool\"",
                    }, new String[] {
                            "W/cool",
                            "cool",
                            "",
                    }
            );
        }
    }
    @Nested
    final class Expect extends HeaderTest<HttpStatus> {
        private Expect() {
            super(
                    HeaderKey.EXPECT,

                    new String[] {
                            "100-continue",
                            "100",
                            "200",
                    }, new String[] {
                            "continue",
                            "wdada",
                            "",
                    }
            );
        }
    }
    @SuppressWarnings("deprecation")
    @Nested
    final class ExpectCT extends HeaderTest<ExpectCertificate> {
        private ExpectCT() {
            super(
                    HeaderKey.EXPECT_CT,

                    new String[] {
                            "max-age=86400, enforce, report-uri=\"https://foo.example.com/report\"",
                            "max-age  =   86400  ,   enforce  ,  report-uri  =  \"https://foo.example.com/report\"",
                            "max-age  =   86400",
                    }, new String[] {
                            "enforce, report-uri=\"https://foo.example.com/report\"",
                            "",
                            "awdawda",
                    }
            );
        }
    }
    @Nested
    final class Expires extends HeaderTest<OffsetDateTime> {
        private Expires() {
            super(
                    HeaderKey.EXPIRES,

                    new String[] {
                            "0",
                            "Wed, 12 Feb 1997 16:29:51 -0500"
                    }, new String[] {
                            "",
                            "1",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class Forwarded extends HeaderTest<codes.laivy.jhttp.module.Forwarded> {
        private Forwarded() {
            super(
                    HeaderKey.FORWARDED,

                    new String[] {
                            "by=unknown;for=unknown;host=jhttp.org;proto=http",
                            "by=hidden;for=hidden;host=localhost;proto=https",
                            "by=secret;for=secret;host=danielmeinicke.com;proto=https",
                            "by=203.0.113.43;for=203.0.113.43;host=localhost;proto=https",
                            "for  =  \"[1080::8:800:200c:417a]\"  ;by  =  \"[1080::8:800:200c:417a]\"; proto  =  http  ;  host  =  192.168.0.1",
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class From extends HeaderTest<Email> {
        private From() {
            super(
                    HeaderKey.FROM,

                    new String[] {
                            "contact@jhttp.org",
                            "dnlfg.contato@gmail.com"
                    }, new String[] {
                            "",
                            "kkijkl",
                            "test@gmail",
                            "01iaesjfaoeijfeoasjfda u1809r@gmail",
                            "_2d.1,@gmail",
                            "dawi0jfwiaoawdafawdawawdadfjoisedfjioaesfjioeasjdkioeasfjdoeiasfjaof@gmaidawkdol.com",
                    }
            );
        }
    }
    @Nested
    final class Host extends HeaderTest<codes.laivy.jhttp.url.Host> {
        private Host() {
            super(
                    HeaderKey.HOST,

                    new String[] {
                            "example.com:80",
                            "example.com",
                            "localhost:80",
                            "192.168.1.1",
                            "192.168.1.1:80",
                            "[2001:0db8:85a3:0000:0000:8a2e:0370:7334]",
                            "[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080",
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class IfMatch extends HeaderTest<Wildcard<EntityTag[]>> {
        private IfMatch() {
            super(
                    HeaderKey.IF_MATCH,

                    new String[] {
                            "\"bfc13a64729c4290ef5b2c2730249c88ca92d82d\"",
                            "\"67ab43\", \"54ed21\", \"7892dd\"",
                            "*",
                    }, new String[] {
                            "awdawd",
                            "",
                    }
            );
        }
    }
    @Nested
    final class IfModifiedSince extends HeaderTest<OffsetDateTime> {
        private IfModifiedSince() {
            super(
                    HeaderKey.IF_MODIFIED_SINCE,

                    new String[] {
                            "Wed, 12 Feb 1997 16:29:51 -0500"
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class IfNoneMatch extends HeaderTest<Wildcard<EntityTag[]>> {
        private IfNoneMatch() {
            super(
                    HeaderKey.IF_NONE_MATCH,

                    new String[] {
                            "\"bfc13a64729c4290ef5b2c2730249c88ca92d82d\"",
                            "\"67ab43\", \"54ed21\", \"7892dd\"",
                            "*",
                    }, new String[] {
                            "awdawd",
                            "",
                    }
            );
        }
    }
    @Nested
    final class IfUnmodifiedSince extends HeaderTest<OffsetDateTime> {
        private IfUnmodifiedSince() {
            super(
                    HeaderKey.IF_UNMODIFIED_SINCE,

                    new String[] {
                            "Wed, 12 Feb 1997 16:29:51 -0500"
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class KeepAlive extends HeaderTest<codes.laivy.jhttp.module.KeepAlive> {
        private KeepAlive() {
            super(
                    HeaderKey.KEEP_ALIVE,

                    new String[] {
                            "timeout=5, max=1000",
                            "timeout=5,max=1000",
                            "timeout   =   5",
                            "timeout  =  5  ,  max  =  1000",
                    }, new String[] {
                            "",
                            "max=5",
                            "max=5,",
                    }
            );
        }
    }
    @SuppressWarnings("deprecation")
    @Nested
    final class LargeAllocation extends HeaderTest<Optional<BitMeasure>> {
        private LargeAllocation() {
            super(
                    HeaderKey.LARGE_ALLOCATION,

                    new String[] {
                            "0",
                            "500",
                    }, new String[] {
                            "",
                            "awdad",
                    }
            );
        }
    }
    @Nested
    final class LastModified extends HeaderTest<OffsetDateTime> {
        private LastModified() {
            super(
                    HeaderKey.LAST_MODIFIED,

                    new String[] {
                            "Wed, 12 Feb 1997 16:29:51 -0500"
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class Location extends HeaderTest<codes.laivy.jhttp.module.Origin> {
        private Location() {
            super(
                    HeaderKey.LOCATION,

                    new String[] {
                            "/index.html",
                            "localhost:80/index.php",
                            "example.com",
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class MaxForwards extends HeaderTest<Integer> {
        private MaxForwards() {
            super(
                    HeaderKey.MAX_FORWARDS,

                    new String[] {
                            "0",
                            "102131",
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class NEL extends HeaderTest<NetworkErrorLogging> {
        private NEL() {
            super(
                    HeaderKey.NEL,

                    new String[] {
                            "{ \"report_to\"  : \"name_of_reporting_group\"  , \"max_age\": 12345, \"include_subdomains\": false, \"success_fraction\": 0.0, \"failure_fraction\": 1.0 }",
                            "{ \"report_to\"  : \"name_of_reporting_group\"  , \"max_age\": 12345, \"include_subdomains\": false, \"success_fraction\": 0.0, \"failure_fraction\": 1.0, \"request_headers\":[], \"response_headers\":[] }",
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class Origin extends HeaderTest<codes.laivy.jhttp.url.Host> {
        private Origin() {
            super(
                    HeaderKey.ORIGIN,

                    new String[] {
                            "null",
                            "NuLL",
                            "https://localhost",
                            "https://localhost:80",
                            "https://example.com:80",
                    }, new String[] {
                            "https://",
                            "",
                            "awdawiod"
                    }
            );
        }
    }
    @Nested
    final class OriginAgentCluster extends HeaderTest<Boolean> {
        private OriginAgentCluster() {
            super(
                    HeaderKey.ORIGIN_AGENT_CLUSTER,

                    new String[] {
                            "?1",
                            "?0",
                    }, new String[] {
                            "true",
                            "false",
                            "1",
                            "0",
                            "",
                            "dawda"
                    }
            );
        }
    }
    @SuppressWarnings("deprecation")
    @Nested
    final class Pragma extends HeaderTest<Void> {
        private Pragma() {
            super(
                    HeaderKey.PRAGMA,

                    new String[] {
                            "no-cache",
                    }, new String[] {
                            "",
                            "adawda",
                            "0"
                    }
            );
        }
    }
    @Nested
    final class ProxyAuthorization extends HeaderTest<Credentials> {
        private ProxyAuthorization() {
            super(
                    HeaderKey.PROXY_AUTHORIZATION,

                    new String[] {
                            "Basic username:password",
                            "Bearer testToken12345",
                    }, new String[] {
                            "Basic",
                            "",
                    }
            );
        }
    }
    @Nested
    final class Referer extends HeaderTest<codes.laivy.jhttp.module.Origin> {
        private Referer() {
            super(
                    HeaderKey.REFERER,

                    new String[] {
                            "/index.html",
                            "localhost:80/index.php",
                            "example.com",
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class RTT extends HeaderTest<Duration> {
        private RTT() {
            super(
                    HeaderKey.RTT,

                    new String[] {
                            "125",
                            "0",
                            "241",
                    }, new String[] {
                            "",
                            "adawd",
                    }
            );
        }
    }
    @Nested
    final class SaveData extends HeaderTest<Boolean> {
        private SaveData() {
            super(
                    HeaderKey.SAVE_DATA,

                    new String[] {
                            "ON",
                            "OFF",
                            "on",
                            "off",
                    }, new String[] {
                            "0",
                            "1",
                            "true",
                            "false",
                            "",
                            "adawd",
                    }
            );
        }
    }
    @Nested
    final class Server extends HeaderTest<Product> {
        private Server() {
            super(
                    HeaderKey.SERVER,

                    new String[] {
                            "JHTTP/1.0",
                            "JHTTP/1.0 (jhttp.org)",
                            "Mozilla-Server/3.0"
                    }, new String[] {
                            "",
                            "dwadawd ajwdoawda",
                    }
            );
        }
    }
    @Nested
    final class SetCookie extends HeaderTest<codes.laivy.jhttp.module.Cookie.Request> {
        private SetCookie() {
            super(
                    HeaderKey.SET_COOKIE,

                    new String[] {
                            "cookie_name=cookie_value; Secure; Partitioned; HttpOnly; Domain=example.com; SameSite=Strict; Path=/index; Max-Age=123456; Expires=Wed, 12 Feb 1997 16:29:51 -0500",
                            "cookie_name=cookie_value; Domain=localhost; Secure; Path=/index; Partitioned; HttpOnly; SameSite=Lax; Max-Age=123456;",
                            "cookie_name=cookie_value",
                            "cookie_name     =   cookie_value  ;   Secure  ;    Partitioned   ;   HttpOnly  ;   Domain   =   example.com  ;   SameSite    =    None; Path   =   /index/; Max-Age   =    123456   ;   Expires   =     Wed, 12 Feb 1997 16:29:51 -0500",
                    }, new String[] {
                            "",
                            "Secure; Partitioned",
                    }
            );
        }
    }
    @Nested
    final class SourceMap extends HeaderTest<codes.laivy.jhttp.module.Origin> {
        private SourceMap() {
            super(
                    HeaderKey.SOURCEMAP,

                    new String[] {
                            "/index.html",
                            "localhost:80/index.php",
                            "example.com",
                    }, new String[] {
                            "",
                            "kkijkl",
                    }
            );
        }
    }
    @Nested
    final class TE extends HeaderTest<Weight<Deferred<Encoding>>[]> {
        private TE() {
            super(
                    HeaderKey.TE,

                    new String[] {
                            "compress",
                            "deflate",
                            "gzip",
                            "trailers",
                            "trailers, deflate;q=0.5",
                            "trailers  ,  deflate  ;  q  = 0.5",
                    }, new String[] {
                            "",
                    }
            );
        }
    }
    @Nested
    final class Trailer extends HeaderTest<HeaderKey<?>[]> {
        private Trailer() {
            super(
                    HeaderKey.TRAILER,

                    new String[] {
                            "Content-Length",
                            "Content-Length,Content-Type",
                            "Content-Length   ,  Content-Type",
                    }, new String[] {
                            "",
                            "Test",
                            "Content-Length;Content-Type",
                    }
            );
        }
    }
    @Nested
    final class TransferEncoding extends HeaderTest<Deferred<Encoding>[]> {
        private TransferEncoding() {
            super(
                    HeaderKey.TRANSFER_ENCODING,

                    new String[] {
                            "chunked",
                            "compress",
                            "deflate",
                            "gzip",
                            "gzip, chunked",
                            "gzip  ,  chunked",
                    }, new String[] {
                            "",
                    }
            );
        }
    }
    @Nested
    final class Vary extends HeaderTest<Wildcard<HeaderKey<?>[]>> {
        private Vary() {
            super(
                    HeaderKey.VARY,

                    new String[] {
                            "*",
                            "Sec-CH-Prefers-Reduced-Motion",
                            "Sec-CH-Prefers-Reduced-Motion,Test",
                            "Sec-CH-Prefers-Reduced-Motion   ,  Test",
                    }, new String[] {
                            "",
                            "Sec-CH-Prefers-Reduced-Motion;Test",
                    }
            );
        }
    }
    @Nested
    final class UserAgent extends HeaderTest<codes.laivy.jhttp.module.UserAgent> {
        private UserAgent() {
            super(
                    HeaderKey.USER_AGENT,

                    new String[] {
                            "Mozilla/5.0 (platform; rv:geckoversion) Gecko/geckotrail Firefox/firefoxversion",
                            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0",
                            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) ",
                            "Chrome/51.0.2704.103 Safari/537.36",
                            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) ",
                            "Chrome/51.0.2704.106 Safari/537.36 OPR/38.0.2220.41",
                            "Opera/9.80 (Macintosh; Intel Mac OS X; U; en) Presto/2.2.15 Version/10.00",
                            "Opera/9.60 (Windows NT 6.0; U; en) Presto/2.1.1",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) ",
                            "Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59",
                            "Mozilla/5.0 (iPhone; CPU iPhone OS 13_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.1.1 Mobile/15E148 Safari/604.1",
                            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)\n",
                            "Mozilla/5.0 (compatible; YandexAccessibilityBot/3.0; +http://yandex.com/bots)\n",
                            "curl/7.64.1",
                            "PostmanRuntime/7.26.5"
                    }, new String[] {
                            "",
                            "(platform; rv:geckoversion) Gecko/geckotrail Firefox/firefoxversion",
                    }
            );
        }
    }
    @Nested
    final class Upgrade extends HeaderTest<codes.laivy.jhttp.module.Upgrade[]> {
        private Upgrade() {
            super(
                    HeaderKey.UPGRADE,

                    new String[] {
                            "example/1, foo/2",
                            "example/1",
                            "example",
                            "websocket    ,   example/2.2, foo",
                    }, new String[] {
                            "",
                            "example-/1,4, 21 daw dko",
                    }
            );
        }
    }
    @SuppressWarnings("deprecation")
    @Nested
    final class Digest extends HeaderTest<codes.laivy.jhttp.module.Digest[]> {
        private Digest() {
            super(
                    HeaderKey.DIGEST,

                    new String[] {
                            "sha-256=X48E9qOokqqrvdts8nOJRJN3OWDUoyWxBf7kbu9DBPE=",
                            "sha-256=X48E9qOokqqrvdts8nOJRJN3OWDUoyWxBf7kbu9DBPE=,unixsum=30637",
                            "sha-256=X48E9qOokqqrvdts8nOJRJN3OWDUoyWxBf7kbu9DBPE=,id-sha-256=0KJL0PvNLH5UbYZLTT7DBFuSyxKpnjyadrWx5E90E/z=",
                            "sha-256=X48E9qOokqqrvdts8nOJRJN3OWDUoyWxBf7kbu9DBPE=    ,    id-sha-256=0KJL0PvNLH5UbYZLTT7DBFuSyxKpnjyadrWx5E90E/z=",
                            "md5=0cc175b9c0f1b6a831c399e269772661"
                    }, new String[] {
                            "",
                            "sha-256=X48E9qOokqqrvdts8nOJRJN3OWDUoyWxBf7kbu9DBPE=",
                            "sha-233=X48E9qOokqqrvdts8nOOWDUoyWxBf7kbu9DBPE=",
                    }
            );
        }
    }

    // Utilities classes

    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
    private static abstract class HeaderTest<T> {

        // Object

        private final @NotNull HeaderKey<T> key;

        private final @NotNull String @NotNull [] valids;
        private final @NotNull String @NotNull [] invalids;

        private final @NotNull BiFunction<T, T, Boolean> function;

        // Constructor

        protected HeaderTest(
                @NotNull HeaderKey<T> key,

                @NotNull String @NotNull [] valids,
                @NotNull String @NotNull [] invalids
        ) {
            this(key, valids, invalids, new BiFunction<T, T, Boolean>() {
                @SuppressWarnings("DataFlowIssue")
                @Override
                public @NotNull Boolean apply(T t1, T t2) {
                    @Nullable Object object1 = t1;
                    @Nullable Object object2 = t2;

                    while (object1 instanceof Wildcard<?> || object1 instanceof Weight<?> || object1 instanceof Optional<?>) {
                        if (object1 instanceof Wildcard<?>) {
                            if (((Wildcard<?>) object1).isWildcard() && ((Wildcard<?>) object1).isWildcard()) {
                                return true;
                            }

                            object1 = ((Wildcard<?>) object1).getValue();
                            object2 = ((Wildcard<?>) object2).getValue();
                        } else if (object1 instanceof Weight<?>) {
                            object1 = ((Weight<?>) object1).getValue();
                            object2 = ((Weight<?>) object2).getValue();
                        } else {
                            object1 = ((Optional<?>) object1).orElse(null);
                            object2 = ((Optional<?>) object2).orElse(null);
                        }
                    }

                    if (object1 != null && object1.getClass().isArray()) {
                        //noinspection unchecked
                        return Arrays.equals((T[]) object1, (T[]) object2);
                    } else {
                        return Objects.equals(object1, object2);
                    }
                }
            });
        }
        protected HeaderTest(
                @NotNull HeaderKey<T> key,

                @NotNull String @NotNull [] valids,
                @NotNull String @NotNull [] invalids,

                @NotNull BiFunction<T, T, Boolean> function
        ) {
            this.key = key;

            this.valids = valids;
            this.invalids = invalids;

            this.function = function;
        }

        // Getters

        public @NotNull HeaderKey<T> getKey() {
            return key;
        }

        public @NotNull String @NotNull [] getValids() {
            return valids;
        }
        public @NotNull String @NotNull [] getInvalids() {
            return invalids;
        }

        // Test methods

        @Test
        @Order(value = 0)
        void validate() {
            for (@NotNull String valid : getValids()) {
                try {
                    getKey().read(HttpVersion.HTTP1_1(), valid);
                } catch (@NotNull UnsupportedOperationException ignore) {
                } catch (@NotNull Throwable e) {
                    throw new RuntimeException("the header '" + getKey() + "' cannot be validated using '" + valid + "'", e);
                }
            }
        }
        @Test
        @Order(value = 1)
        void invalidate() {
            for (@NotNull String invalid : getInvalids()) {
                try {
                    getKey().read(HttpVersion.HTTP1_1(), invalid);
                    throw new RuntimeException("the header '" + getKey() + "' cannot be invalidated using '" + invalid + "'");
                } catch (@NotNull Throwable ignore) {
                }
            }
        }

        @Test
        @Order(value = 2)
        void serialization() throws HeaderFormatException {
            for (@NotNull String valid : getValids()) {
                try {
                    @NotNull Header<T> reference = getKey().read(HttpVersion.HTTP1_1(), valid);
                    @NotNull Header<T> clone = getKey().read(HttpVersion.HTTP1_1(), getKey().write(HttpVersion.HTTP1_1(), reference));

                    Assertions.assertTrue(function.apply(reference.getValue(), clone.getValue()), "cannot match reference '" + reference.getValue() + "' and clone '" + clone.getValue() + "'");
                } catch (@NotNull Throwable throwable) {
                    throw new RuntimeException("cannot test serialization using '" + valid + "'", throwable);
                }
            }
        }
    }

}
