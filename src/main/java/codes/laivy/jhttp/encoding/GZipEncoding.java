package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.EncodingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipEncoding extends Encoding {

    // Static initializers

    public static @NotNull Builder builder() {
        return new Builder();
    }

    // Object

    private final int buffer;

    protected GZipEncoding(int buffer) {
        super("gzip", "x-gzip");
        this.buffer = buffer;
    }

    // Getters

    public int getBuffer() {
        return buffer;
    }

    // Implementations

    @Override
    public byte @NotNull [] decompress(byte @NotNull [] bytes) throws EncodingException {
        try (@NotNull ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
             @NotNull GZIPInputStream gzipStream = new GZIPInputStream(byteStream, getBuffer());
             @NotNull ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[getBuffer()];
            int len;
            while ((len = gzipStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, len);
            }

            return outStream.toByteArray();
        } catch (IOException e) {
            throw new EncodingException("cannot decompress with gzip native stream", e);
        }
    }
    @Override
    public byte @NotNull [] compress(byte @NotNull [] bytes) throws EncodingException {
        try (@NotNull ByteArrayOutputStream byteStream = new ByteArrayOutputStream(bytes.length);
             @NotNull GZIPOutputStream stream = new GZIPOutputStream(byteStream)) {

            stream.write(bytes);
            stream.finish();

            return byteStream.toByteArray();
        } catch (IOException e) {
            throw new EncodingException("cannot compress with gzip native stream", e);
        }
    }

    // Classes

    public static final class Builder {

        private int buffer = 2048;

        private Builder() {
        }

        // Modules

        public int buffer() {
            return buffer;
        }
        @Contract("_->this")
        public @NotNull Builder buffer(int buffer) {
            this.buffer = buffer;
            return this;
        }

        // Builder

        public @NotNull GZipEncoding build() {
            return new GZipEncoding(buffer);
        }

    }

}
