package codes.laivy.jhttp.tests.http1_1;

import codes.laivy.jhttp.headers.HeaderKey;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.text.ParseException;

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
                "GET /index.php HTTP/1.1\r\nHost: [2001:0db8:85a3:0000:0000:8a2e:0370:7334]:8080\r\n\r\n"
        };

        @Test
        @Order(value = 0)
        void validate() throws Throwable {
            for (@NotNull String valid : VALIDS) {
                Assertions.assertTrue(HttpVersion.HTTP1_1().getFactory().getRequest().isCompatible(valid), "cannot validate http 1.1 request '" + valid + "'");
                HttpVersion.HTTP1_1().getFactory().getRequest().parse(valid);
            }
        }
        @Test
        @Order(value = 1)
        void assertions() throws ParseException {

        }
    }

}
