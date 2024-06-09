package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.content.ContentRange;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.text.ParseException;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class ContentRangeTests {

    private ContentRangeTests() {
    }

    private static final @NotNull String[] VALIDS = new String[] {
            "bytes */*",
            "bytes 0-50/*",
            "bytes 0-50/100"
    };

    @Test
    @Order(value = 0)
    void validate() {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(ContentRange.isContentRange(valid));
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException {
        @NotNull ContentRange range = ContentRange.parse("bytes 0-50/100");

        Assertions.assertFalse(range.getRange().isWildcard());
        Assertions.assertFalse(range.getSize().isWildcard());

        Assertions.assertEquals(range.getUnit(), "bytes");
        Assertions.assertEquals(range.getRange().getValue().getMinimum(), 0L);
        Assertions.assertEquals(range.getRange().getValue().getMinimum(), 50L);
        Assertions.assertEquals(range.getSize().getValue(), 100L);
    }

}
