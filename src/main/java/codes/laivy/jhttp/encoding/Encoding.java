package codes.laivy.jhttp.encoding;

import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
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
 * @author Daniel Meinicke (Laivy)
 * @since 1.0-SNAPSHOT
 */
// todo: 03/06/2023 add more tokens (See https://en.wikipedia.org/wiki/HTTP_compression)
@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class Encoding {

    // Static initializers

    private static final @NotNull Set<Encoding> collection = new HashSet<>();

    static {
        add(ChunkedEncoding.builder().build());
        add(GZipEncoding.builder().build());
        add(DeflateEncoding.builder().build());
        add(CompressEncoding.builder().build());
        add(IdentityEncoding.builder().build());
    }

    /**
     * Retrieves all the available encodings.
     *
     * @return an unmodifiable collection of all registered encodings
     * @author Daniel Meinicke (Laivy)
     */
    public static @NotNull Collection<Encoding> retrieve() {
        synchronized (collection) {
            return Collections.unmodifiableSet(collection);
        }
    }

    /**
     * Retrieves an encoding by its name or alias.
     *
     * @param string the name or alias of the encoding to retrieve
     * @return an optional containing the encoding if found, otherwise an empty optional
     * @author Daniel Meinicke (Laivy)
     */
    public static @NotNull Optional<Encoding> retrieve(@NotNull String string) {
        return stream().filter(encoding -> encoding.getName().equalsIgnoreCase(string) || Arrays.stream(encoding.getAliases()).anyMatch(alias -> alias.equalsIgnoreCase(string))).findFirst();
    }

    /**
     * Adds a custom encoding to the collection.
     *
     * @param encoding the encoding to add
     * @return {@code true} if the encoding was added successfully, {@code false} if it was already present
     * @author Daniel Meinicke (Laivy)
     */
    public static boolean add(@NotNull Encoding encoding) {
        synchronized (collection) {
            // Check if there's an encoding with that name already defined
            if (collection.stream().anyMatch(enc -> enc.getName().equalsIgnoreCase(encoding.getName()))) {
                return false;
            }

            // Check if there's another encoding with the same aliases
            for (@NotNull Encoding that : retrieve()) {
                if (Arrays.stream(that.getAliases()).anyMatch(new HashSet<>(Arrays.asList(encoding.getAliases()))::contains)) {
                    return false;
                }
            }

            return collection.add(encoding);
        }
    }

    /**
     * Removes a custom encoding from the collection.
     *
     * @param encoding the encoding to remove
     * @return {@code true} if the encoding was removed successfully, {@code false} if it was not present
     * @author Daniel Meinicke (Laivy)
     */
    public static boolean remove(@NotNull Encoding encoding) {
        synchronized (collection) {
            return collection.remove(encoding);
        }
    }

    /**
     * Checks if a specific encoding is present in the collection.
     *
     * @param encoding the encoding to check for
     * @return {@code true} if the encoding is present, {@code false} otherwise
     * @author Daniel Meinicke (Laivy)
     */
    public static boolean contains(@NotNull Encoding encoding) {
        return retrieve().contains(encoding);
    }

    /**
     * Checks if a specific encoding is present in the collection by it's name.
     *
     * @param name the encoding name
     * @return {@code true} if the encoding is present, {@code false} otherwise
     * @author Daniel Meinicke (Laivy)
     */
    public static boolean contains(@NotNull String name) {
        return retrieve(name).isPresent();
    }

    /**
     * Returns the number of encodings in the collection, including the default ones.
     *
     * @return the number of encodings in the collection
     * @author Daniel Meinicke (Laivy)
     */
    public static int size() {
        return retrieve().size();
    }

    /**
     * Returns a stream of all the encodings in the collection.
     *
     * @return a stream of all encodings
     * @author Daniel Meinicke (Laivy)
     */
    public static @NotNull Stream<Encoding> stream() {
        return retrieve().stream();
    }

    /**
     * Returns an iterator over the encodings in the collection.
     *
     * @return an iterator over the encodings
     * @author Daniel Meinicke (Laivy)
     */
    public static @NotNull Iterator<Encoding> iterator() {
        return retrieve().iterator();
    }

    /**
     * Returns an array containing all the encodings in the collection.
     *
     * @return an array of all encodings
     * @author Daniel Meinicke (Laivy)
     */
    public static @NotNull Encoding[] toArray() {
        return retrieve().toArray(new Encoding[0]);
    }

    // Object

    private final @NotNull String name;
    private final @NotNull String @NotNull [] aliases;

    protected Encoding(@NotNull String name, @NotNull String @NotNull ... aliases) {
        this.name = name;
        this.aliases = Arrays.copyOf(aliases, aliases.length);

        // Verifications
        @NotNull Consumer<@NotNull String> consumer = string -> {
            if (StringUtils.isBlank(string) || string.contains(",")) {
                throw new IllegalArgumentException("illegal transfer encoding name/alias '" + string + "'");
            }
        };

        consumer.accept(getName());
        for (@NotNull String alias : getAliases()) {
            consumer.accept(alias);
        }
    }

    // Getters

    public final @NotNull String getName() {
        return name;
    }
    public @NotNull String @NotNull [] getAliases() {
        return aliases;
    }

    // Modules

    public abstract byte @NotNull [] decompress(byte @NotNull [] bytes) throws EncodingException;
    public abstract byte @NotNull [] compress(byte @NotNull [] bytes) throws EncodingException;

    public final synchronized void register() {
        add(this);
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
