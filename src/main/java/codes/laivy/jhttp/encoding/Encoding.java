package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.TransferEncodingException;
import codes.laivy.jhttp.exception.parser.IllegalHttpVersionException;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * This class contains all the registered encodings. When creating your own encoding, in order for it to be
 * identified and used in the reading/writing of HTTP data, you need to register it here using the
 * {@link #register()} or {@link #add(Encoding)} method.
 *
 * <p>The retrieve methods will always return the default encodings available in this package, except if there
 * is already a custom encoding with the same name already added here, in which case it will return your encoding
 * instead.</p>
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
// todo: 03/06/2023 add more tokens (See https://en.wikipedia.org/wiki/HTTP_compression)
public abstract class Encoding {

    // Static initializers

    // todo: multi threading
    private static final @NotNull Set<Encoding> collection = ConcurrentHashMap.newKeySet();

    /**
     * Retrieves all the available encodings, including the default ones and any custom ones that have been added.
     *
     * @return an unmodifiable collection of all registered encodings
     * @author Daniel Richard (Laivy)
     */
    public static @NotNull Collection<Encoding> retrieve() {
        @NotNull Set<Encoding> encodings = new HashSet<>(collection);

        // If there's a custom encoding with any of these names, it will not be added
        // since Sets doesn't allow multiples elements with the same properties.
        encodings.add(ChunkedEncoding.builder().build());
        encodings.add(GZipEncoding.builder().build());
        encodings.add(DeflateEncoding.builder().build());
        encodings.add(CompressEncoding.builder().build());
        encodings.add(IdentityEncoding.builder().build());

        return Collections.unmodifiableSet(encodings);
    }

    /**
     * Retrieves an encoding by its name.
     *
     * @param name the name of the encoding to retrieve
     * @return an optional containing the encoding if found, otherwise an empty optional
     * @author Daniel Richard (Laivy)
     */
    public static @NotNull Optional<Encoding> retrieve(@NotNull String name) {
        return stream().filter(encoding -> encoding.getName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Adds a custom encoding to the collection.
     *
     * @param encoding the encoding to add
     * @return {@code true} if the encoding was added successfully, {@code false} if it was already present
     * @author Daniel Richard (Laivy)
     */
    public static boolean add(@NotNull Encoding encoding) {
        return collection.add(encoding);
    }

    /**
     * Removes a custom encoding from the collection.
     *
     * @param encoding the encoding to remove
     * @return {@code true} if the encoding was removed successfully, {@code false} if it was not present
     * @author Daniel Richard (Laivy)
     */
    public static boolean remove(@NotNull Encoding encoding) {
        return collection.remove(encoding);
    }

    /**
     * Checks if a specific encoding is present in the collection.
     *
     * @param encoding the encoding to check for
     * @return {@code true} if the encoding is present, {@code false} otherwise
     * @author Daniel Richard (Laivy)
     */
    public static boolean contains(@NotNull Encoding encoding) {
        return retrieve().contains(encoding);
    }

    /**
     * Checks if a specific encoding is present in the collection by it's name.
     *
     * @param name the encoding name
     * @return {@code true} if the encoding is present, {@code false} otherwise
     * @author Daniel Richard (Laivy)
     */
    public static boolean contains(@NotNull String name) {
        return retrieve(name).isPresent();
    }

    /**
     * Returns the number of encodings in the collection, including the default ones.
     *
     * @return the number of encodings in the collection
     * @author Daniel Richard (Laivy)
     */
    public int size() {
        return retrieve().size();
    }

    /**
     * Returns a stream of all the encodings in the collection.
     *
     * @return a stream of all encodings
     * @author Daniel Richard (Laivy)
     */
    public static @NotNull Stream<Encoding> stream() {
        return retrieve().stream();
    }

    /**
     * Returns an iterator over the encodings in the collection.
     *
     * @return an iterator over the encodings
     * @author Daniel Richard (Laivy)
     */
    public static @NotNull Iterator<Encoding> iterator() {
        return retrieve().iterator();
    }

    /**
     * Returns an array containing all the encodings in the collection.
     *
     * @return an array of all encodings
     * @author Daniel Richard (Laivy)
     */
    public static @NotNull Encoding[] toArray() {
        return retrieve().toArray(new Encoding[0]);
    }

    // Object

    private final @NotNull String name;

    protected Encoding(@NotNull String name) {
        this.name = name;

        if (name.contains(",") || StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("illegal transfer encoding name '" + name + "'");
        }
    }

    public final @NotNull String getName() {
        return name;
    }

    public abstract byte @NotNull [] decompress(@NotNull HttpVersion version, byte @NotNull [] bytes) throws TransferEncodingException, IllegalHttpVersionException;

    public abstract byte @NotNull [] compress(@NotNull HttpVersion version, byte @NotNull [] bytes) throws TransferEncodingException, IllegalHttpVersionException;

    public boolean isCompatible(@NotNull HttpVersion version) {
        return true;
    }

    public final synchronized void register() {
        collection.removeIf(encoding -> encoding.getName().equalsIgnoreCase(getName()));
        collection.add(this);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Encoding that = (Encoding) object;
        return Objects.equals(getName().toLowerCase(), that.getName().toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName().toLowerCase());
    }

    @Override
    public final @NotNull String toString() {
        return name.toLowerCase();
    }

}
