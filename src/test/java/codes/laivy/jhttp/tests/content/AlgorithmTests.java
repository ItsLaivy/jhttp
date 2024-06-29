package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.module.Digest;
import codes.laivy.jhttp.module.Digest.Algorithm;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.security.NoSuchAlgorithmException;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class AlgorithmTests {

    private AlgorithmTests() {
    }

    @Test
    @Order(value = 0)
    void serialization() throws NoSuchAlgorithmException {
        @NotNull String expected = "Testing";

        for (@NotNull Algorithm algorithm : Algorithm.values()) {
            @NotNull Digest digest = algorithm.encode(expected);
            Assertions.assertTrue(algorithm.validate(digest.getValue()));
        }
    }

}
