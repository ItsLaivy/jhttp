package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.content.AlternativeService;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.Duration;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class AlternativeServiceTests {

    private AlternativeServiceTests() {
    }

    private static final @NotNull String[] VALIDS = new String[] {
            "h3-25=\":443\"; ma=3600",
            "http/1.1=\":443\"; ma=1; persist=1",
            "http/1.1=\"localhost:80/\"; ma=1; persist=1",
            "ma=1; persist=1; http/1.1=\":80/\"",
            "persist=1; ma=1; http/1.1=\":80/\";",
            "http/1.1=\"localhost:80/\"",
            "http/1.1     =   \":80\"  ;  ma    =   1  ;   persist   =    1",
    };

    @Test
    @Order(value = 0)
    void validate() throws ParseException, UnknownHostException, URISyntaxException {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(AlternativeService.validate(valid), "cannot verify '" + valid + "' as a valid alternative service");
            Assertions.assertEquals(AlternativeService.parse(VALIDS[0]), AlternativeService.parse(AlternativeService.parse(VALIDS[0]).toString()), "cannot parse-obtain '" + valid + "' as a valid alternative service");
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException, UnknownHostException, URISyntaxException {
        @NotNull AlternativeService service = AlternativeService.parse("http/1.1=\"localhost:500/\"; ma=12345; persist=1");

        Assertions.assertArrayEquals(service.getVersion(), HttpVersion.HTTP1_1().getId());
        Assertions.assertEquals(service.getAge(), Duration.ofSeconds(12345));
        Assertions.assertTrue(service.isPersistent());
    }
    @Test
    @Order(value = 2)
    void validation() throws ParseException, UnknownHostException, URISyntaxException {
        @NotNull AlternativeService reference = AlternativeService.parse("http/1.1=\"localhost:500/\"; ma=12345; persist=1");
        @NotNull AlternativeService clone = AlternativeService.parse(reference.toString());

        Assertions.assertEquals(reference, clone);
    }

}
