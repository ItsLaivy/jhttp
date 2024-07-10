package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import codes.laivy.jhttp.url.Data;
import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.url.domain.Domain;
import codes.laivy.jhttp.url.domain.Port;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class SourceTests {

    private SourceTests() {
    }

    @Nested
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
    final class DomainsUrl {

        private final @NotNull String[] VALIDS = new String[] {
                "http://dwf.example.com",
                "mail.example.com:443",
                "https://store.example.com",
                "dad.example.com",
                "https://gaga.example.com:12",
                "dwad.example.com:12",
                "localhost:12",
                "https://localhost:12"
        };

        @Test
        @Order(value = 0)
        void validate() {
            for (@NotNull String valid : VALIDS) {
                Assertions.assertTrue(Domain.validate(valid), "cannot validate domain '" + valid + "'");
            }
        }
        @Test
        @Order(value = 1)
        void assertions() throws ParseException {
            @NotNull Domain<?> domain = Domain.parse("https://daw.example.com:12");

            Assertions.assertNotNull(domain.getProtocol());

            Assertions.assertEquals(Port.create(12), domain.getHost().getPort());
            Assertions.assertEquals("example.com", ((Host.Name) domain.getHost()).getSLD() + "." + ((Host.Name) domain.getHost()).getTLD());
            Assertions.assertTrue(domain.getProtocol().isSecure());

            // Subdomains
            Assertions.assertEquals(1, ((Host.Name) domain.getHost()).getSubdomains().length);
        }
        @Test
        @Order(value = 2)
        void serialization() throws ParseException {
            @NotNull Domain<?> reference = Domain.parse("https://daw.example.com:12");
            @NotNull Domain<?> clone = Domain.parse(reference.toString());

            Assertions.assertEquals(reference, clone);
        }
    }

    @Nested
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
    final class DataUri {

        private final @NotNull String[] VALIDS = new String[] {
                "data:text/plain;charset=utf-8;base64,SGV5IQ==",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA",
                "data:application/json;base64,eyJrZXkiOiAiVmFsdWUifQ==",
                "data:;base64,SGVsbG8=",
                "data:,Hello%20World!"
        };

        @Test
        @Order(value = 0)
        void validate() throws ParseException, UnsupportedEncodingException {
            for (@NotNull String valid : VALIDS) {
                Assertions.assertTrue(Data.validate(valid));
                Assertions.assertEquals(Data.parse(valid), Data.parse(Data.parse(valid).toString()));
            }
        }

        @Test
        @Order(value = 1)
        void assertions() throws Throwable {
            @NotNull MediaType<JsonElement> expected = MediaType.APPLICATION_JSON();
            @NotNull ContentSecurityPolicy.Source source = ContentSecurityPolicy.Source.parse("data:application/json;base64,eyJrZXkiOiAiVmFsdWUifQ==");
            Assertions.assertInstanceOf(Data.class, source);

            @NotNull Data data = (Data) source;
            Assertions.assertTrue(data.isBase64());
            Assertions.assertEquals(expected, data.getMediaType());
            Assertions.assertArrayEquals("{\"key\": \"Value\"}".getBytes(StandardCharsets.UTF_8), data.getData());
        }
        @Test
        @Order(value = 2)
        void serialization() throws Throwable {
            @NotNull Data reference = (Data) ContentSecurityPolicy.Source.parse("data:application/json;base64,eyJrZXkiOiAiVmFsdWUifQ==");
            @NotNull Data clone = Data.parse(reference.toString());

            Assertions.assertEquals(reference, clone);
        }
    }

}
