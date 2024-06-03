package codes.laivy.jhttp.tests;

import codes.laivy.jhttp.exception.encoding.TransferEncodingException;
import codes.laivy.jhttp.encoding.TransferEncoding;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class TransferEncodingTests {

    private TransferEncodingTests() {
    }

    @Test
    @Order(value = 0)
    void compressAndDecompress() throws TransferEncodingException {
        @NotNull String target = "Just a Cool Text!";

        for (@NotNull TransferEncoding encoding : TransferEncoding.Encodings.toArray()) {
            byte[] compressed = encoding.compress(target.getBytes(StandardCharsets.UTF_8));
            byte[] decompressed = encoding.decompress(compressed);

            Assertions.assertEquals(target, new String(decompressed, StandardCharsets.UTF_8), "cannot proceed compress/decompress test using '" + encoding.getName() + "' encoding");
        }
    }

}
