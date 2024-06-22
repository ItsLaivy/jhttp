package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.EncodingException;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// todo: can also be called "x-gzip"
public class GZipEncoding extends Encoding {

    // Static initializers

    public static @NotNull Builder builder() {
        return new Builder();
    }

    // Object

    protected GZipEncoding() {
        super("gzip");
    }

    // Implementations

    @Override
    public @NotNull String decompress(@NotNull String string) throws EncodingException {
        try (@NotNull ByteArrayInputStream byteStream = new ByteArrayInputStream(string.getBytes(StandardCharsets.ISO_8859_1));
             @NotNull GZIPInputStream gzipStream = new GZIPInputStream(byteStream);
             @NotNull ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, len);
            }

            return new String(outStream.toByteArray(), StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            throw new EncodingException("cannot decompress with gzip native stream", e);
        }
    }
    @Override
    public @NotNull String compress(@NotNull String string) throws EncodingException {
        byte[] bytes = string.getBytes(StandardCharsets.ISO_8859_1);

        try (@NotNull ByteArrayOutputStream byteStream = new ByteArrayOutputStream(bytes.length);
             @NotNull GZIPOutputStream stream = new GZIPOutputStream(byteStream)) {

            stream.write(bytes);
            stream.finish();

            return new String(byteStream.toByteArray(), StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            throw new EncodingException("cannot compress with gzip native stream", e);
        }
    }

    // Classes

    public static final class Builder {

        private Builder() {
        }

        public @NotNull GZipEncoding build() {
            return new GZipEncoding();
        }

    }

}
