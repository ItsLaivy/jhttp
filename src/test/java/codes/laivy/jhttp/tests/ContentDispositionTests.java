package codes.laivy.jhttp.tests;

import codes.laivy.jhttp.utilities.ContentDisposition;
import codes.laivy.jhttp.utilities.ContentDisposition.Property;
import codes.laivy.jhttp.utilities.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.text.ParseException;
import java.time.Instant;

import static codes.laivy.jhttp.utilities.ContentDisposition.Type.ATTACHMENT;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class ContentDispositionTests {

    private ContentDispositionTests() {
    }

    private static final @NotNull String[] VALIDS = new String[] {
            "attachment; filename=\"example.txt\";",
            "inline; filename=\"example.txt\";",
            "form-data; filename=\"example.txt\"; size=12345",
            "attachment  ;   name    =  \"teste\"  ; filename  =  \"example.txt\"    ;   creation-date  = \"Wed, 12 Feb 1997 16:29:51 -0500\"; read-date  = \"Wed, 12 Feb 1997 16:29:51 -0500\"; modification-date=\"Wed, 12 Feb 1997 16:29:51 -0500\"; size  =  12345",
    };

    @Test
    @Order(value = 0)
    void validate() {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(ContentDisposition.isContentDisposition(valid));
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException {
        @NotNull ContentDisposition service = ContentDisposition.parse("attachment  ;   name    =  \"test\"  ; filename  =  \"example.txt\"    ;   creation-date  = \"Wed, 12 Feb 1997 16:29:51 -0500\"; read-date  = \"Wed, 12 Feb 1997 16:29:51 -0500\"; modification-date=\"Wed, 12 Feb 1997 16:29:51 -0500\"; size  =  12345");
        @NotNull Instant instant = DateUtils.RFC822.convert("Wed, 12 Feb 1997 16:29:51 -0500");

        Assertions.assertNotNull(service.getProperty());
        Assertions.assertEquals(service.getType(), ATTACHMENT);
        Assertions.assertEquals(service.getName(), "test");

        @NotNull Property property = service.getProperty();
        Assertions.assertEquals(property.getCreation(), instant);
        Assertions.assertEquals(property.getModification(), instant);
        Assertions.assertEquals(property.getRead(), instant);
        Assertions.assertEquals(property.getSize(), 12345L);
    }

}
