package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.EncodingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class DeflateEncoding extends Encoding {

    // Static initializers

    public static @NotNull Builder builder() {
        return new Builder();
    }

    // Object

    private final @NotNull Inflater inflater;
    private final @NotNull Deflater deflater;

    protected DeflateEncoding(@NotNull Inflater inflater, @NotNull Deflater deflater) {
        super("deflate");

        this.inflater = inflater;
        this.deflater = deflater;
    }

    // Getters

    public final @NotNull Inflater getInflater() {
        return inflater;
    }
    public final @NotNull Deflater getDeflater() {
        return deflater;
    }

    // Modules

    @Override
    public byte @NotNull [] decompress(byte @NotNull [] bytes) throws EncodingException {
        try (@NotNull ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
             @NotNull InflaterInputStream stream = new InflaterInputStream(byteStream);
             @NotNull ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new EncodingException("cannot decompress with gzip native stream", e);
        }
    }
    @Override
    public byte @NotNull [] compress(byte @NotNull [] bytes) throws EncodingException {
        try (@NotNull ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             @NotNull DeflaterOutputStream stream = new DeflaterOutputStream(byteStream, getDeflater())) {

            stream.write(bytes);
            stream.finish();

            return byteStream.toByteArray();
        } catch (IOException e) {
            throw new EncodingException("cannot compress with deflater native stream", e);
        }
    }

    // Classes

    public static final class Builder {

        private @NotNull Inflater inflater = new Inflater();
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

        public @NotNull Inflater inflater() {
            return inflater;
        }
        @Contract("_->this")
        public @NotNull Builder inflater(@NotNull Inflater inflater) {
            this.inflater = inflater;
            return this;
        }

        // Builder

        public @NotNull DeflateEncoding build() {
            return new DeflateEncoding(inflater, deflater);
        }

    }

}
