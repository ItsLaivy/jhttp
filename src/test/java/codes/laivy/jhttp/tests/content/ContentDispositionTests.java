package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.content.ContentDisposition;
import codes.laivy.jhttp.content.ContentDisposition.Property;
import codes.laivy.jhttp.utilities.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.text.ParseException;
import java.time.OffsetDateTime;

import static codes.laivy.jhttp.content.ContentDisposition.Type.ATTACHMENT;

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
    void validate() throws ParseException {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(ContentDisposition.validate(valid), "cannot verify '" + valid + "' as a valid content disposition");
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException {
        @NotNull ContentDisposition disposition = ContentDisposition.parse("attachment  ;   name    =  \"test\"  ; filename  =  \"example.txt\"    ;   creation-date  = \"Wed, 12 Feb 1997 16:29:51 -0500\"; read-date  = \"Wed, 12 Feb 1997 16:29:51 -0500\"; modification-date=\"Wed, 12 Feb 1997 16:29:51 -0500\"; size  =  12345");
        @NotNull OffsetDateTime instant = DateUtils.RFC822.convert("Wed, 12 Feb 1997 16:29:51 -0500");

        Assertions.assertNotNull(disposition.getProperty());
        Assertions.assertEquals(disposition.getType(), ATTACHMENT);
        Assertions.assertEquals(disposition.getName(), "test");

        @NotNull Property property = disposition.getProperty();
        Assertions.assertEquals(property.getCreation(), instant);
        Assertions.assertEquals(property.getModification(), instant);
        Assertions.assertEquals(property.getRead(), instant);
        Assertions.assertEquals(property.getSize(), 12345L);
    }
    @Test
    @Order(value = 2)
    void serialization() throws ParseException {
        @NotNull ContentDisposition reference = ContentDisposition.parse("attachment; name=\"test\"; filename=\"example.txt\"; creation-date=\"Wed, 12 Feb 1997 16:29:51 -0500\"; read-date=\"Wed, 12 Feb 1997 16:29:51 -0500\"; modification-date=\"Wed, 12 Feb 1997 16:29:51 -0500\"; size=12345");
        @NotNull ContentDisposition clone = ContentDisposition.parse(reference.toString());

        Assertions.assertEquals(reference, clone);
    }

}
