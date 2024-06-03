package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.TransferEncodingException;
import codes.laivy.jhttp.protocol.HttpVersion;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;

public class DeflateEncoding extends Encoding {

    // Static initializers

    public static @NotNull Builder builder() {
        return new Builder();
    }

    // Object

    private final @NotNull Deflater deflater;

    protected DeflateEncoding(@NotNull Deflater deflater) {
        super("deflate");
        this.deflater = deflater;
    }

    // Getters

    public final @NotNull Deflater getDeflater() {
        return deflater;
    }

    // Modules

    @Override
    public byte @NotNull [] decompress(@NotNull HttpVersion version, byte @NotNull [] bytes) throws TransferEncodingException {
        if (bytes.length == 0) return new byte[0];

        try (@NotNull ByteInputStream byteStream = new ByteInputStream(bytes, bytes.length)) {
            try (@NotNull DeflaterInputStream stream = new DeflaterInputStream(byteStream, getDeflater())) {
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
            try (@NotNull DeflaterOutputStream stream = new DeflaterOutputStream(byteStream, getDeflater())) {
                stream.write(bytes);
                return byteStream.toByteArray();
            }
        } catch (@NotNull IOException e) {
            throw new TransferEncodingException("cannot compress with deflater native stream", e);
        }
    }

    // Classes

    public static final class Builder {

        private @NotNull Deflater deflater = new Deflater();

        private Builder() {
        }

        // Methods

        public @NotNull Deflater deflater() {
            return deflater;
        }
        @Contract("_->this")
        public @NotNull Builder deflater(@NotNull Deflater deflater) {
            this.deflater = deflater;
            return this;
        }

        // Builder

        public @NotNull DeflateEncoding build() {
            return new DeflateEncoding(deflater);
        }

    }

}
