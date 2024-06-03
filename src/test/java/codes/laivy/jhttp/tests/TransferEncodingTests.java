package codes.laivy.jhttp.tests;

import codes.laivy.jhttp.exception.IllegalHttpVersionException;
import codes.laivy.jhttp.exception.encoding.TransferEncodingException;
import codes.laivy.jhttp.encoding.TransferEncoding;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class TransferEncodingTests {

    private TransferEncodingTests() {
    }

    @Test
    @Order(value = 0)
    void compressAndDecompress() throws TransferEncodingException, IllegalHttpVersionException {
        @NotNull String target = "Just a Cool Text with\r\n Some cool characteristics and formatting!";

        for (@NotNull HttpVersion version : HttpVersion.getVersions()) {
            for (@NotNull TransferEncoding encoding : TransferEncoding.Encodings.toArray()) {
                if (!encoding.isCompatible(version)) {
                    continue;
                }

                byte[] compressed = encoding.compress(version, target.getBytes(StandardCharsets.UTF_8));
                byte[] decompressed = encoding.decompress(version, compressed);

                Assertions.assertEquals(target, new String(decompressed, StandardCharsets.UTF_8), "cannot proceed compress/decompress test using '" + encoding.getName() + "' encoding on http version '" + version + "'");
            }
        }
    }

}
