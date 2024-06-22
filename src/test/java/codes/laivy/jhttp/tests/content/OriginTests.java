package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.module.Origin;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class OriginTests {

    private OriginTests() {
    }

    private static final @NotNull String[] VALIDS = new String[] {
            "http://example.com/documents/1234",
            "localhost:80/test",
            "https://localhost:501/test",
            "/just/test"
    };

    @Test
    @Order(value = 0)
    void validate() throws UnknownHostException, ParseException, URISyntaxException {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(Origin.Parser.validate(valid), "cannot validate '" + valid + "' as an origin");
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException, UnknownHostException, URISyntaxException {
        @NotNull Origin origin = Origin.Parser.deserialize("localhost:80/test/excellent");

        // Path
        Assertions.assertEquals(origin.getURI().getPath(), URI.create("/test/excellent").getPath());

        // Authority
        Assertions.assertNotNull(origin.getDomain());
        Assertions.assertNotNull(origin.getDomain().getHost().getPort());

        Assertions.assertEquals(origin.getDomain().getHost().getPort(), 80);
        Assertions.assertEquals(origin.getDomain().getHost().getName(), "localhost");
    }
    @Test
    @Order(value = 2)
    void serialization() throws ParseException, UnknownHostException, URISyntaxException {
        @NotNull Origin reference = Origin.Parser.deserialize("localhost:80/test/excellent");
        @NotNull Origin clone = Origin.Parser.deserialize(Origin.Parser.serialize(reference));

        Assertions.assertEquals(reference, clone);
    }

}
