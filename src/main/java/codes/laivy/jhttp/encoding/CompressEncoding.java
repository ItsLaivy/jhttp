package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.EncodingException;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    public @NotNull String decompress(@NotNull String string) throws EncodingException {
        byte[] bytes = string.getBytes();
        if (bytes.length == 0) return new String(new byte[0]);

        // Dictionary
        @NotNull Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, "" + (char) i);
        }

        // Start decompression
        @NotNull ByteArrayOutputStream result = new ByteArrayOutputStream();
        int dictSize = 256;

        try {
            int oldCode = ((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF);
            @NotNull String oldString = dictionary.get(oldCode);
            result.write(oldString.getBytes(StandardCharsets.UTF_8));

            int i = 2;
            while (i < bytes.length) {
                int code = ((bytes[i] & 0xFF) << 8) | (bytes[i + 1] & 0xFF);
                i += 2;

                @NotNull String current;
                if (dictionary.containsKey(code)) {
                    current = dictionary.get(code);
                } else if (code == dictSize) {
                    current = oldString + oldString.charAt(0);
                } else {
                    throw new IllegalArgumentException("Bad compressed code");
                }

                result.write(current.getBytes(StandardCharsets.UTF_8));

                dictionary.put(dictSize++, oldString + current.charAt(0));
                oldString = current;
            }
        } catch (@NotNull IOException e) {
            throw new RuntimeException(e);
        }

        // Finish
        return result.toString();
    }

    @Override
    public @NotNull String compress(@NotNull String string) throws EncodingException {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

        // Dictionary
        @NotNull Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put("" + (char) i, i);
        }

        // Start compression
        @NotNull List<Integer> result = new ArrayList<>();
        @NotNull String current = "";

        int dictSize = 256;
        for (byte b : bytes) {
            char c = (char) (b & 0xFF);

            @NotNull String combined = current + c;
            if (dictionary.containsKey(combined)) {
                current = combined;
            } else {
                result.add(dictionary.get(current));
                dictionary.put(combined, dictSize++);
                current = "" + c;
            }
        }

        if (!current.isEmpty()) {
            result.add(dictionary.get(current));
        }

        // Finish
        @NotNull ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int code : result) {
            outputStream.write((code >> 8) & 0xFF);
            outputStream.write(code & 0xFF);
        }

        return outputStream.toString();
    }


    // Classes

    public static final class Builder {

        private Builder() {
        }

        public @NotNull CompressEncoding build() {
            return new CompressEncoding();
        }

    }

}
