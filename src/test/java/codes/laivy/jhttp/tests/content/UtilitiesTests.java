package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.utilities.DateUtils.RFC822;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.text.ParseException;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class UtilitiesTests {

    private UtilitiesTests() {
    }

    @Test
    @Order(value = 0)
    void rfc822() throws ParseException {
        @NotNull String expected = "Wed, 12 Feb 1997 16:29:51 -0500";
        Assertions.assertEquals(RFC822.convert(expected), RFC822.convert(RFC822.convert(RFC822.convert(expected))));
    }

}
