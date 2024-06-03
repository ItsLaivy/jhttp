package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.TransferEncodingException;
import codes.laivy.jhttp.protocol.HttpVersion;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.jetbrains.annotations.NotNull;

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

    protected GZipEncoding() {
        super("gzip");
    }

    @Override
    public byte @NotNull [] decompress(@NotNull HttpVersion version, byte @NotNull [] bytes) throws TransferEncodingException {
        if (bytes.length == 0) return new byte[0];

        try (@NotNull ByteInputStream byteStream = new ByteInputStream(bytes, bytes.length)) {
            try (@NotNull GZIPInputStream stream = new GZIPInputStream(byteStream)) {
                return byteStream.getBytes();
            }
        } catch (@NotNull IOException e) {
            throw new TransferEncodingException("cannot decompress with gzip native stream", e);
        }
    }

    @Override
    public byte @NotNull [] compress(@NotNull HttpVersion version, byte @NotNull [] bytes) throws TransferEncodingException {
        if (bytes.length == 0) return new byte[0];

        try (@NotNull ByteArrayOutputStream byteStream = new ByteArrayOutputStream(bytes.length)) {
            try (@NotNull GZIPOutputStream stream = new GZIPOutputStream(byteStream)) {
                stream.write(bytes);
                return byteStream.toByteArray();
            }
        } catch (@NotNull IOException e) {
            throw new TransferEncodingException("cannot compress with gzip native stream", e);
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
