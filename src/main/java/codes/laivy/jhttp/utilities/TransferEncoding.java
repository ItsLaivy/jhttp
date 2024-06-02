package codes.laivy.jhttp.utilities;

import codes.laivy.jhttp.exception.TransferEncodingException;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.*;

public abstract class TransferEncoding {

    // Object

    private final @NotNull String name;

    protected TransferEncoding(@NotNull String name) {
        this.name = name;
    }

    public final @NotNull String getName() {
        return name;
    }

    public abstract byte @NotNull [] decompress(byte @NotNull [] bytes) throws TransferEncodingException;
    public abstract byte @NotNull [] compress(byte @NotNull [] bytes) throws TransferEncodingException;

    public final synchronized void register() {
        Encodings.collection.removeIf(encoding -> encoding.getName().equalsIgnoreCase(getName()));
        Encodings.collection.add(this);
    }

    // Equals

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TransferEncoding that = (TransferEncoding) object;
        return Objects.equals(getName().toLowerCase(), that.getName().toLowerCase());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getName().toLowerCase());
    }

    @Override
    public final @NotNull String toString() {
        return name;
    }

    // Classes

    public static final class Encodings {

        private static final @NotNull Set<TransferEncoding> collection = ConcurrentHashMap.newKeySet();

        private Encodings() {
            throw new UnsupportedOperationException();
        }

        public static @NotNull Optional<TransferEncoding> retrieve(@NotNull String name) {
            @NotNull Set<TransferEncoding> encodings = new HashSet<>(Encodings.collection);
            encodings.add(new Chunked());
            encodings.add(new GZip());
            encodings.add(new Deflate());
            encodings.add(new Compress());
            encodings.add(new Identity());

            return stream().filter(encoding -> encoding.getName().equalsIgnoreCase(name)).findFirst();
        }

        public static boolean add(@NotNull TransferEncoding encoding) {
            return collection.add(encoding);
        }
        public static boolean remove(@NotNull TransferEncoding encoding) {
            return collection.remove(encoding);
        }
        public static boolean contains(@NotNull TransferEncoding encoding) {
            return collection.contains(encoding);
        }

        public int size() {
            return collection.size();
        }
        public boolean isEmpty() {
            return collection.isEmpty();
        }

        public static @NotNull Stream<TransferEncoding> stream() {
            return collection.stream();
        }
        public static @NotNull Iterator<TransferEncoding> iterator() {
            return collection.iterator();
        }
        public static @NotNull TransferEncoding[] toArray() {
            return collection.toArray(new TransferEncoding[0]);
        }
        
    }

    public static final class Chunked extends TransferEncoding {

        // Static initializers

        // todo: enhance this
        @Range(from = 1, to = Integer.MAX_VALUE)
        private static int slice = 2048;

        @Range(from = 1, to = Integer.MAX_VALUE)
        public static int getSlice() {
            return slice;
        }
        public static void setSlice(
                @Range(from = 1, to = Integer.MAX_VALUE)
                int slice
        ) {
            Chunked.slice = slice;
        }

        // Object

        private static final @NotNull Chunked instance = new Chunked();

        public static @NotNull Chunked getInstance() {
            return instance;
        }

        private Chunked() {
            super("chunked");
        }

        @Override
        public byte @NotNull [] decompress(byte @NotNull [] bytes) throws TransferEncodingException {

        }
        @Override
        public byte @NotNull [] compress(byte @NotNull [] bytes) throws TransferEncodingException {

        }

    }
    public static final class GZip extends TransferEncoding {

        private static final @NotNull GZip instance = new GZip();

        public static @NotNull GZip getInstance() {
            return instance;
        }

        private GZip() {
            super("gzip");
        }

        @Override
        public byte @NotNull [] decompress(byte @NotNull [] bytes) throws TransferEncodingException {
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
        public byte @NotNull [] compress(byte @NotNull [] bytes) throws TransferEncodingException {
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

    }
    public static final class Deflate extends TransferEncoding {

        // Static initializers

        private static @NotNull Deflater deflater = new Deflater();

        public static @NotNull Deflater getDeflater() {
            return deflater;
        }
        public static void setDeflater(@NotNull Deflater deflater) {
            Deflate.deflater = deflater;
        }

        // Object

        private Deflate() {
            super("deflate");
        }

        // Getters

        // Modules

        @Override
        public byte @NotNull [] decompress(byte @NotNull [] bytes) throws TransferEncodingException {
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
        public byte @NotNull [] compress(byte @NotNull [] bytes) throws TransferEncodingException {
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

    }
    public static final class Compress extends TransferEncoding {

        private static final @NotNull Compress instance = new Compress();

        public static @NotNull Compress getInstance() {
            return instance;
        }

        private Compress() {
            super("compress");
        }

        @Override
        public byte @NotNull [] decompress(byte @NotNull [] bytes) throws TransferEncodingException {
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
        public byte @NotNull [] compress(byte @NotNull [] bytes) throws TransferEncodingException {
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

    }
    public static final class Identity extends TransferEncoding {

        private static final @NotNull Identity instance = new Identity();

        public static @NotNull Identity getInstance() {
            return instance;
        }

        private Identity() {
            super("identity");
        }

        @Override
        public byte @NotNull [] decompress(byte @NotNull [] bytes) {
            return bytes;
        }
        @Override
        public byte @NotNull [] compress(byte @NotNull [] bytes) throws TransferEncodingException {
            return bytes;
        }

    }

}
