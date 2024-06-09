package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.authorization.Credentials;
import codes.laivy.jhttp.utilities.URIAuthority;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.net.UnknownHostException;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class URIAuthorityTests {

    private URIAuthorityTests() {
    }

    private static final @NotNull String[] VALIDS = new String[] {
            "/",
            "/index.php",
            "localhost/index.php",
            "localhost",
            "user@localhost",
            "user@localhost/index.php",
            "user:pass@localhost/index.php",
            "user:pass@localhost",
            "user:pass@localhost:80/index.php",
            "user:pass@localhost:80/page/test/index.php",
    };

    @Test
    @Order(value = 0)
    void validate() {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(URIAuthority.isUriAuthority(valid));
        }
    }
    @Test
    @Order(value = 1)
    void validatePort() throws UnknownHostException, URISyntaxException {
        @NotNull URIAuthority authority = URIAuthority.parse("user:pass@localhost/page/test/index.php");
        Assertions.assertEquals(authority.getPort(), 80);
        authority = URIAuthority.parse("user:pass@localhost:443/page/test/index.php");
        Assertions.assertEquals(authority.getPort(), 443);
    }
    @Test
    @Order(value = 2)
    void validateUserinfo() throws UnknownHostException, URISyntaxException {
        @NotNull String test = "user:pass@localhost:80/page/test/index.php";
        @NotNull URIAuthority authority = URIAuthority.parse(test);

        Assertions.assertNotNull(authority.getUserInfo());
        Assertions.assertEquals(authority.getUserInfo(), new Credentials.Basic("user", "pass"));
    }

}
