package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.content.Origin;
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
            "/just/test"
    };

    @Test
    @Order(value = 0)
    void validate() throws UnknownHostException, ParseException, URISyntaxException {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(Origin.Parser.validate(valid));
            Assertions.assertEquals(Origin.Parser.parse(VALIDS[0]), Origin.Parser.parse(Origin.Parser.parse(VALIDS[0]).toString()));
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException, UnknownHostException, URISyntaxException {
        @NotNull Origin location = Origin.Parser.parse("localhost:80/test/excellent");

        // Path
        Assertions.assertEquals(location.getURI().getPath(), URI.create("/test/excellent").getPath());

        // Authority
        Assertions.assertNotNull(location.getDomain());
        Assertions.assertNotNull(location.getDomain().getHost().getPort());

        Assertions.assertEquals(location.getDomain().getHost().getPort(), 80);
        Assertions.assertEquals(location.getDomain().getHost().getName(), "localhost");
    }

}
