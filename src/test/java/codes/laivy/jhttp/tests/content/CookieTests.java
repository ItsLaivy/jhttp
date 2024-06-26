package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.module.Cookie;
import codes.laivy.jhttp.module.Cookie.Request;
import codes.laivy.jhttp.module.Cookie.Request.SameSite;
import codes.laivy.jhttp.url.domain.Domain;
import codes.laivy.jhttp.utilities.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.text.ParseException;
import java.time.Duration;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class CookieTests {

    private CookieTests() {
    }

    private static final @NotNull String[] VALIDS = new String[] {
            "cookie_name=cookie_value",
            "cookie_name   =   cookie_value" // Try with some spaces
    };

    @Test
    @Order(value = 0)
    void validate() {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(Cookie.Parser.validate(valid), "cannot validate '" + valid + "' as a cookie");
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException {
        @NotNull Cookie cookie = Cookie.Parser.deserialize("cookie_name   =   cookie_value");
        Assertions.assertEquals(cookie.getName(), "cookie_name");
        Assertions.assertEquals(cookie.getValue(), "cookie_value");
    }
    @Test
    @Order(value = 2)
    void serialization() throws ParseException {
        @NotNull Cookie reference = Cookie.Parser.deserialize("cookie_name   =   cookie_value");
        @NotNull Cookie clone = Cookie.Parser.deserialize(Cookie.Parser.serialize(reference));

        Assertions.assertEquals(reference, clone);
    }

    @Nested
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
    final class Requests {

        private final @NotNull String[] VALIDS = new String[] {
                "cookie_name=cookie_value; Secure; Partitioned; HttpOnly; Domain=example.com; SameSite=Strict; Path=/index; Max-Age=123456; Expires=Wed, 12 Feb 1997 16:29:51 -0500",
                "cookie_name=cookie_value; Domain=localhost; Secure; Path=/index; Partitioned; HttpOnly; SameSite=Lax; Max-Age=123456;",
                "cookie_name=cookie_value",
                "cookie_name     =   cookie_value  ;   Secure  ;    Partitioned   ;   HttpOnly  ;   Domain   =   example.com  ;   SameSite    =    None; Path   =   /index/; Max-Age   =    123456   ;   Expires   =     Wed, 12 Feb 1997 16:29:51 -0500",
        };

        @Test
        @Order(value = 0)
        void validate() {
            for (@NotNull String valid : VALIDS) {
                Assertions.assertTrue(Request.Parser.validate(valid), "cannot validate '" + valid + "' as a cookie request");
            }
        }
        @Test
        @Order(value = 1)
        void assertions() throws ParseException {
            @NotNull Request request = Request.Parser.deserialize("cookie_name=cookie_value; Secure; Partitioned; HttpOnly; Domain=example.com; SameSite=Strict; Path=/index; Max-Age=123456; Expires=Wed, 12 Feb 1997 16:29:51 -0500");

            Assertions.assertTrue(request.isSecure());
            Assertions.assertTrue(request.isPartitioned());
            Assertions.assertTrue(request.isHttpOnly());
            Assertions.assertEquals(Domain.parse("example.com"), request.getDomain());
            Assertions.assertEquals(SameSite.STRICT, request.getSameSite());
            Assertions.assertEquals(URI.create("/index"), request.getPath());
            Assertions.assertEquals(Duration.ofSeconds(123456), request.getMaxAge());
            Assertions.assertEquals(DateUtils.RFC822.convert("Wed, 12 Feb 1997 16:29:51 -0500").toInstant(), request.getExpires());
        }
        @Test
        @Order(value = 2)
        void serialization() throws ParseException {
            @NotNull Request reference = Request.Parser.deserialize("cookie_name=cookie_value; Secure; Partitioned; HttpOnly; Domain=example.com; SameSite=Strict; Path=/index; Max-Age=123456; Expires=Wed, 12 Feb 1997 16:29:51 -0500");
            @NotNull Request clone = Request.Parser.deserialize(Request.Parser.serialize(reference));

            Assertions.assertEquals(reference, clone);
        }
    }

}
