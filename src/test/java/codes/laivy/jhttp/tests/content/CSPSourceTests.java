package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.url.CSPSource.Domain;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.text.ParseException;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class CSPSourceTests {

    private CSPSourceTests() {
    }

    @Nested
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
    final class Domains {

        private final @NotNull String[] VALIDS = new String[] {
                "http://*.example.com",
                "mail.example.com:443",
                "https://store.example.com",
                "*.example.com",
                "https://*.example.com:12/path/to/file.js",
                "ws://example.com/"
        };

        @Test
        @Order(value = 0)
        void validate() {
            for (@NotNull String valid : VALIDS) {
                Assertions.assertTrue(Domain.validate(valid));
            }
        }
        @Test
        @Order(value = 1)
        void assertions() throws ParseException {
            @NotNull Domain domain = Domain.parse("https://*.example.com:12/path/to/file.js");

            Assertions.assertEquals(domain.getPort(), 12);
            Assertions.assertEquals(domain.getHostname(), "example.com");
            Assertions.assertEquals(domain.getUri(), URI.create("/path/to/file.js"));
            Assertions.assertTrue(domain.isSecure());

            // Subdomains
            Assertions.assertEquals(1, domain.getSubdomains().length);
            Assertions.assertTrue(domain.getSubdomains()[0].isWildcard());
        }
    }

}
