package codes.laivy.jhttp.tests.http1_1;

import codes.laivy.jhttp.authorization.Credentials.Basic;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.request.HttpRequest;
import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.encoding.GZipEncoding;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.headers.HttpHeaderKey;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.URIAuthority;
import codes.laivy.jhttp.utilities.DateUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.net.InetSocketAddress;
import java.net.URI;

import static codes.laivy.jhttp.headers.HttpHeaderKey.*;
import static codes.laivy.jhttp.protocol.HttpVersion.HTTP1_1;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class HttpFactoryTests {

    @BeforeEach
    void setUp() throws ClassNotFoundException {
        // Load headers
        Class.forName(HttpHeaderKey.class.getName());
    }

    @Nested
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
    final class Requests {

        private final @NotNull String[] VALIDS = new String[]{
                "GET /index.java HTTP/1.1\r\nHost: localhost\r\n\r\n",
                "GET /index HTTP/1.1\r\nHost: 192.0.2.1:8080\r\n\r\n",
                "GET /index HTTP/1.1\r\nHost: [2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080\r\n\r\n",
                "GET https://username:password@example.com:8080/index HTTP/1.1\r\nHost: [2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080\r\n\r\n",
                "GET https://username:password@example.com:8080/index HTTP/1.1\r\nHost: [2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080\r\nContent-Encoding: gzip\r\n\r\n" + GZipEncoding.builder().build().compress("Cool Text")
        };

        Requests() throws EncodingException {
        }

        @Test
        @Order(value = 0)
        void validate() throws Throwable {
            for (@NotNull String valid : VALIDS) {
                Assertions.assertTrue(HTTP1_1().getRequestFactory().validate(valid), "cannot validate http 1.1 request '" + valid + "'");
                HTTP1_1().getRequestFactory().parse(valid);
            }
        }
        @Test
        @Order(value = 1)
        void assertions() throws Throwable {
            @NotNull String expected = "Hello, this is a jhttp gzip text just for tests :)\n";
            @NotNull String encoded = GZipEncoding.builder().build().compress(expected);

            @NotNull String string = "GET https://username:password@example.com:8080/index HTTP/1.1\r\nHost: [2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080\r\nContent-Encoding: gzip\r\n\r\n" + encoded;

            @NotNull HttpRequest request = HTTP1_1().getRequestFactory().parse(string);

            Assertions.assertNotNull(request.getBody());
            Assertions.assertEquals(URIAuthority.create(new Basic("username", "password"), InetSocketAddress.createUnresolved("example.com", 8080)), request.getAuthority());
            Assertions.assertEquals(Method.GET, request.getMethod());
            Assertions.assertEquals(URI.create("/index"), request.getUri());
            Assertions.assertEquals(Host.IPv6.parse("[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080"), request.getHeaders().get(HOST)[0].getValue());

            // Validate content
            Assertions.assertEquals(expected, request.getBody().toString());
        }
        @Test
        @Order(value = 2)
        void serialization() throws Throwable {
            @NotNull String expected = "Hello, this is a jhttp gzip text just for tests :)\n";
            @NotNull String encoded = GZipEncoding.builder().build().compress(expected);

            @NotNull String string = "GET https://username:password@example.com:8080/index HTTP/1.1\r\nHost: [2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080\r\nContent-Encoding: gzip\r\n\r\n" + encoded;

            @NotNull HttpRequest reference = HTTP1_1().getRequestFactory().parse(string);
            @NotNull HttpRequest clone = HTTP1_1().getRequestFactory().parse(reference.toString());

            Assertions.assertEquals(reference, clone);
        }

        @Test
        @Order(value = 3)
        void contentType() throws Throwable {
            @NotNull String string = "GET http://localhost/index HTTP/1.1\r\nHost: localhost\r\nContent-Type: application/json\r\n\r\n{\"text\":\"test\"}";

            @NotNull HttpRequest request = HTTP1_1().getRequestFactory().parse(string);
            @NotNull HttpHeaders headers = request.getHeaders();

            Assertions.assertTrue(headers.contains(CONTENT_TYPE));
            Assertions.assertEquals(1, headers.get(CONTENT_TYPE).length);
            Assertions.assertEquals(MediaType.APPLICATION_JSON(), headers.get(CONTENT_TYPE)[0].getValue());

            Assertions.assertNotNull(request.getBody());
            Assertions.assertNotNull(request.getBody().getContent(HTTP1_1(), MediaType.APPLICATION_JSON()));

            // Match
            @NotNull JsonObject object = new JsonObject();
            object.addProperty("text", "test");

            Assertions.assertEquals(object, request.getBody().getContent(HTTP1_1(), MediaType.APPLICATION_JSON()).getData());
        }

    }

    @Nested
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
    final class Responses {
        private final @NotNull String[] VALIDS = new String[]{
                "HTTP/1.1 200 OK\r\nDate: Mon, 27 Jul 2009 12:28:53 GMT\r\nServer: JHTTP Environment\r\nConnection: close\r\n\r\n",
                "HTTP/1.1 200 OK\r\nDate: Mon, 27 Jul 2009 12:28:53 GMT\r\nServer: JHTTP Environment\r\nContent-Encoding: gzip\r\n\r\n" + GZipEncoding.builder().build().compress("Cool Text")
        };

        Responses() throws EncodingException {
        }

        @Test
        @Order(value = 0)
        void validate() throws Throwable {
            for (@NotNull String valid : VALIDS) {
                Assertions.assertTrue(HTTP1_1().getResponseFactory().validate(valid), "cannot validate http 1.1 response '" + valid + "'");
                HTTP1_1().getResponseFactory().parse(valid);
            }
        }
        @Test
        @Order(value = 1)
        void assertions() throws Throwable {
            @NotNull String expected = "Hello, this is a jhttp gzip text just for tests :)\n";
            @NotNull String encoded = GZipEncoding.builder().build().compress(expected);

            @NotNull String string = "HTTP/1.1 200 OK\r\nDate: Mon, 27 Jul 2009 12:28:53 GMT\r\nServer: JHTTP Environment\r\nContent-Encoding: gzip\r\n\r\n" + encoded;

            @NotNull HttpResponse response = HTTP1_1().getResponseFactory().parse(string);

            Assertions.assertNotNull(response.getBody());
            Assertions.assertEquals(HttpStatus.OK, response.getStatus());

            // Headers
            Assertions.assertEquals("JHTTP Environment", response.getHeaders().get(SERVER)[0].getValue().toString());
            Assertions.assertEquals(DateUtils.RFC822.convert("Mon, 27 Jul 2009 12:28:53 GMT"), response.getHeaders().get(DATE)[0].getValue());

            // Validate content
            Assertions.assertEquals(expected, response.getBody().toString());
        }
        @Test
        @Order(value = 2)
        void serialization() throws Throwable {
            @NotNull String expected = "Hello, this is a jhttp gzip text just for tests :)\n";
            @NotNull String encoded = GZipEncoding.builder().build().compress(expected);

            @NotNull String string = "HTTP/1.1 200 OK\r\nDate: Mon, 27 Jul 2009 12:28:53 GMT\r\nServer: JHTTP Environment\r\nContent-Encoding: gzip\r\n\r\n" + encoded;

            @NotNull HttpResponse reference = HTTP1_1().getResponseFactory().parse(string);
            @NotNull HttpResponse clone = HTTP1_1().getResponseFactory().parse(reference.toString());

            Assertions.assertEquals(reference, clone);
        }

        @Test
        @Order(value = 3)
        void contentType() throws Throwable {
            @NotNull String string = "HTTP/1.1 200 OK\r\nDate: Mon, 27 Jul 2009 12:28:53 GMT\r\nServer: JHTTP Environment\r\nContent-Type: application/json\r\n\r\n{\"text\":\"test\"}";

            @NotNull HttpResponse response = HTTP1_1().getResponseFactory().parse(string);
            @NotNull HttpHeaders headers = response.getHeaders();

            Assertions.assertTrue(headers.contains(CONTENT_TYPE));
            Assertions.assertEquals(1, headers.get(CONTENT_TYPE).length);
            Assertions.assertEquals(MediaType.APPLICATION_JSON(), headers.get(CONTENT_TYPE)[0].getValue());

            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getContent(HTTP1_1(), MediaType.APPLICATION_JSON()));

            // Match
            @NotNull JsonObject object = new JsonObject();
            object.addProperty("text", "test");

            Assertions.assertEquals(object, response.getBody().getContent(HTTP1_1(), MediaType.APPLICATION_JSON()).getData());
        }

    }

}