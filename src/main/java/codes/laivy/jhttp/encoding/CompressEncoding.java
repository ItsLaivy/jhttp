package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompressEncoding extends Encoding {

    // Static initializers

    public static @NotNull Builder builder() {
        return new Builder();
    }

    // Object
    
    protected CompressEncoding() {
        super("compress");
    }

    @Override
    public byte @NotNull [] decompress(@NotNull HttpVersion version, byte @NotNull [] bytes) throws EncodingException {
        if (bytes.length == 0) return new byte[0];

        @NotNull Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, "" + (char) i);
        }

        @NotNull List<Byte> result = new LinkedList<>();
        int oldCode = bytes[0];
        result.add((byte) oldCode);

        for (int i = 1; i < bytes.length; i++) {
            int code = bytes[i] & 0xff;
            @NotNull String current;

            if (dictionary.containsKey(code)) {
                current = dictionary.get(code);
            } else if (code == dictionary.size()) {
                current = dictionary.get(oldCode) + dictionary.get(oldCode).charAt(0);
            } else {
                throw new IllegalArgumentException("Bad compressed code");
            }

            for (char c : current.toCharArray()) {
                result.add((byte) c);
            }

            dictionary.put(dictionary.size(), dictionary.get(oldCode) + current.charAt(0));
            oldCode = code;
        }

        byte[] decompressed = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) {
            decompressed[i] = result.get(i);
        }

        return decompressed;
    }

    @Override
    public byte @NotNull [] compress(@NotNull HttpVersion version, byte @NotNull [] bytes) throws EncodingException {
        if (bytes.length == 0) return new byte[0];

        @NotNull Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(String.valueOf(i), i);
        }

        @NotNull List<Byte> result = new LinkedList<>();
        @NotNull String current = "";

        for (byte b : bytes) {
            String combined = current + (char) b;
            if (dictionary.containsKey(combined)) {
                current = combined;
            } else {
                result.add((byte) (int) dictionary.get(current));
                dictionary.put(combined, dictionary.size());
                current = "" + (char) b;
            }
        }
        result.add((byte) (int) dictionary.get(current));

        byte[] compressed = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) {
            compressed[i] = result.get(i);
        }

        return compressed;
    }

    // Classes

    public static final class Builder {

        private Builder() {
        }

        public @NotNull IdentityEncoding build() {
            return new IdentityEncoding();
        }

    }

}
