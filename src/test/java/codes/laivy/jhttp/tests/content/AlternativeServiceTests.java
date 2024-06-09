package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.content.AlternativeService;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

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
            "http/1.1=\"localhost:80/\"",
            "http/1.1     =   \":80\"  ;  ma    =   1  ;   persist   =    1",
    };

    @Test
    @Order(value = 0)
    void validate() throws ParseException {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(AlternativeService.isAlternativeService(valid));
            Assertions.assertEquals(AlternativeService.parse(VALIDS[0]), AlternativeService.parse(AlternativeService.parse(VALIDS[0]).toString()));
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException {
        @NotNull AlternativeService service = AlternativeService.parse("http/1.1=\"localhost:500/\"; ma=12345; persist=1");

        Assertions.assertEquals(service.getVersion(), HttpVersion.HTTP1_1().getId());
        Assertions.assertEquals(service.getAge(), Duration.ofSeconds(12345));
        Assertions.assertTrue(service.isPersistent());
    }

}
