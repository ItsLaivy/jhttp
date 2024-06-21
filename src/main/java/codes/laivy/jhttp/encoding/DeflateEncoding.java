package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.TransferEncodingException;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

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

        try (@NotNull ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
             @NotNull InflaterInputStream stream = new InflaterInputStream(byteStream)) {

            @NotNull ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new TransferEncodingException("cannot decompress with gzip native stream", e);
        }
    }

    @Override
    public byte @NotNull [] compress(@NotNull HttpVersion version, byte @NotNull [] bytes) throws TransferEncodingException {
        if (bytes.length == 0) return new byte[0];

        try (@NotNull ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             @NotNull DeflaterOutputStream stream = new DeflaterOutputStream(byteStream)) {

            stream.write(bytes);
            stream.finish();

            return byteStream.toByteArray();
        } catch (IOException e) {
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
