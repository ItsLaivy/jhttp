package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.content.ContentLocation;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class ContentLocationTests {

    private ContentLocationTests() {
    }

    private static final @NotNull String[] VALIDS = new String[] {
            "http://example.com/documents/1234",
            "localhost:80/test",
            "/just/test"
    };

    @Test
    @Order(value = 0)
    void validate() {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(ContentLocation.isContentLocation(valid));
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException, UnknownHostException, URISyntaxException {
        @NotNull ContentLocation location = ContentLocation.parse("localhost:80/test/excellent");

        // Path
        Assertions.assertEquals(location.getURI().getPath(), URI.create("/test/excellent").getPath());

        // Authority
        Assertions.assertNotNull(location.getAuthority());
        Assertions.assertEquals(location.getAuthority().getPort(), 80);
        Assertions.assertEquals(location.getAuthority().getHostName(), "localhost");
    }

}
