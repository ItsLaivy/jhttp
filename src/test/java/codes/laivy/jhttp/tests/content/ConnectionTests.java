package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.content.Connection;
import codes.laivy.jhttp.content.Connection.Parser;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.text.ParseException;
import java.util.Arrays;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class ConnectionTests {

    private ConnectionTests() {
    }

    private static final @NotNull String[] VALIDS = new String[] {
            "keep-alive, Upgrade, Authorization",
            "keep-alive,Upgrade  , Authorization, Forwarded, Content-Digest",
            "keep-alive",
            "close",
            "close  ,  Upgrade  , Authorization  , Forwarded  ,  Content-Digest"
    };

    @Test
    @Order(value = 0)
    void validate() throws ParseException {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(Parser.validate(valid));
            Assertions.assertEquals(Parser.deserialize(valid), Parser.deserialize(Parser.deserialize(valid).toString()));
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException {
        @NotNull Connection connection = Parser.deserialize("keep-alive,Upgrade,Authorization");

        Assertions.assertEquals(connection.getType(), Connection.Type.KEEP_ALIVE);
        Assertions.assertTrue(Arrays.stream(connection.getKeys()).anyMatch(key -> key.getName().equals("Upgrade")));
        Assertions.assertTrue(Arrays.stream(connection.getKeys()).anyMatch(key -> key.getName().equals("Authorization")));
    }
    @Test
    @Order(value = 2)
    void serialization() throws ParseException {
        @NotNull Connection reference = Parser.deserialize("keep-alive,Upgrade,Authorization");
        @NotNull Connection clone = Parser.deserialize(Parser.serialize(reference));

        Assertions.assertEquals(reference, clone);
    }

}
