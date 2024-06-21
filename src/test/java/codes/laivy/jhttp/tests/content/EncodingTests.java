package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.parser.IllegalHttpVersionException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class EncodingTests {

    private EncodingTests() {
    }

    @Test
    @Order(value = 0)
    void compressAndDecompress() throws EncodingException, IllegalHttpVersionException {
        @NotNull String target = "Just a Cool Text with\r\n Some cool characteristics and formatting!";

        for (@NotNull Encoding encoding : Encoding.toArray()) {
            @NotNull String compressed = encoding.compress(target);
            @NotNull String decompressed = encoding.decompress(compressed);

            Assertions.assertEquals(target, decompressed, "cannot proceed compress/decompress test using '" + encoding.getName() + "' encoding");
        }
    }

}

