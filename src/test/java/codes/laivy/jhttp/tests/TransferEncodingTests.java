package codes.laivy.jhttp.tests;

import codes.laivy.jhttp.exception.TransferEncodingException;
import codes.laivy.jhttp.utilities.TransferEncoding;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class TransferEncodingTests {

    private TransferEncodingTests() {
    }

    @Test
    @Order(value = 0)
    void chunked() {
    }
    @Test
    @Order(value = 1)
    void gzip() throws TransferEncodingException {
        @NotNull String target = "Just a Cool Text!";

        @NotNull TransferEncoding encoding = TransferEncoding.GZip.getInstance();
        byte[] compressed = encoding.compress(target.getBytes(StandardCharsets.UTF_8));
        byte[] decompressed = encoding.decompress(compressed);

        Assertions.assertEquals(target, new String(decompressed, StandardCharsets.UTF_8));
    }

}
