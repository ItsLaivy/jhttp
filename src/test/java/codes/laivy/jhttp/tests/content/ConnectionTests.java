package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.content.Connection;
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
            Assertions.assertTrue(Connection.validate(valid));
            Assertions.assertEquals(Connection.parse(valid), Connection.parse(Connection.parse(valid).toString()));
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException {
        @NotNull Connection connection = Connection.parse("keep-alive,Upgrade,Authorization");

        Assertions.assertEquals(connection.getType(), Connection.Type.KEEP_ALIVE);
        Assertions.assertTrue(Arrays.stream(connection.getKeys()).anyMatch(key -> key.getName().equals("Upgrade")));
        Assertions.assertTrue(Arrays.stream(connection.getKeys()).anyMatch(key -> key.getName().equals("Authorization")));
    }

}
