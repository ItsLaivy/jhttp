package codes.laivy.jhttp.tests.http1_1;

import codes.laivy.jhttp.authorization.Credentials.Basic;
import codes.laivy.jhttp.encoding.GZipEncoding;
import codes.laivy.jhttp.exception.MissingHeaderException;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.exception.parser.IllegalHttpVersionException;
import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.message.EncodedMessage;
import codes.laivy.jhttp.request.HttpRequest;
import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.utilities.Method;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

import static codes.laivy.jhttp.headers.HeaderKey.HOST;
import static codes.laivy.jhttp.protocol.HttpVersion.HTTP1_1;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class HttpFactoryTests {

    @BeforeEach
    void setUp() throws ClassNotFoundException {
        // Load headers
        Class.forName(HeaderKey.class.getName());
    }

    @Nested
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
    final class Requests {

        private final @NotNull String[] VALIDS = new String[] {
                "GET /index.php HTTP/1.1\r\nHost: localhost\r\n\r\n",
                "GET /index.php HTTP/1.1\r\nHost: 192.0.2.1:8080\r\n\r\n",
                "GET /index.php HTTP/1.1\r\nHost: [2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080\r\n\r\n",
                "GET https://username:password@example.com:8080/index.php HTTP/1.1\r\nHost: [2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080\r\n\r\n",
        };

        @Test
        @Order(value = 0)
        void validate() throws Throwable {
            for (@NotNull String valid : VALIDS) {
                Assertions.assertTrue(HTTP1_1().getFactory().getRequest().isCompatible(valid), "cannot validate http 1.1 request '" + valid + "'");
                HTTP1_1().getFactory().getRequest().parse(valid);
            }
        }
        @Test
        @Order(value = 1)
        void assertions() throws ParseException, EncodingException, HeaderFormatException, IOException, MissingHeaderException, IllegalHttpVersionException, URISyntaxException {
            @NotNull String expected = "Hello, this is a jhttp gzip text just for tests :)";
            @NotNull String encoded = GZipEncoding.builder().build().compress(expected);

            @NotNull String string = "GET https://username:password@example.com:8080/index.php HTTP/1.1\r\nHost: [2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080\r\nContent-Encoding: gzip\r\n\r\n" + encoded;

            @NotNull HttpRequest request = HTTP1_1().getFactory().getRequest().parse(string);

            Assertions.assertNotNull(request.getMessage());
            Assertions.assertEquals(request.getAuthority(), URIAuthority.create(new Basic("username", "password"), InetSocketAddress.createUnresolved("example.com", 8080)));
            Assertions.assertEquals(((EncodedMessage) request.getMessage()).getDecoded(), expected);
            Assertions.assertEquals(request.getMethod(), Method.GET);
            Assertions.assertEquals(request.getUri(), URI.create("/index.php"));
            Assertions.assertEquals(request.getHeaders().get(HOST)[0].getValue(), Host.IPv6.parse("[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080"));
        }
        @Test
        @Order(value = 2)
        void serialization() throws ParseException, EncodingException, HeaderFormatException, IOException, MissingHeaderException, IllegalHttpVersionException, URISyntaxException {
            @NotNull String expected = "Hello, this is a jhttp gzip text just for tests :)";
            @NotNull String encoded = GZipEncoding.builder().build().compress(expected);

            @NotNull String string = "GET https://username:password@example.com:8080/index.php HTTP/1.1\r\nHost: [2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080\r\nContent-Encoding: gzip\r\n\r\n" + encoded;

            @NotNull HttpRequest reference = HTTP1_1().getFactory().getRequest().parse(string);
            @NotNull HttpRequest clone = HTTP1_1().getFactory().getRequest().parse(reference.toString());

            Assertions.assertEquals(reference, clone);
        }

    }

}
