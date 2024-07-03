package codes.laivy.jhttp.protocol.factory;

import codes.laivy.jhttp.element.Target;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaderKey;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;

/**
 * A factory interface for creating and managing HTTP headers. This class provides methods
 * for serializing, parsing, and validating HTTP headers.
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public interface HttpHeaderFactory {

    // Getters

    /**
     * Retrieves the HTTP version used by this factory.
     *
     * @return The HTTP version used by this factory. Never null.
     */
    @NotNull HttpVersion getVersion();

    // Modules

    @NotNull
    HttpHeaders createMutable(@NotNull Target target);
    @NotNull
    HttpHeaders createImmutable(@NotNull HttpHeaders clone);

    /**
     * Safely serializes an HTTP header to a string representation.
     *
     * @param header The HTTP header to be serialized. Must not be null.
     * @return The string representation of the HTTP header. Never null.
     * @throws IllegalArgumentException if the header value contains illegal characters.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default @NotNull String serialize(@NotNull HttpHeader<?> header) {
        @NotNull String name = header.getKey().getName();
        @NotNull String value = ((HttpHeaderKey) header.getKey()).write(getVersion(), header);

        if (value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException("header value contains illegal characters");
        } else {
            return name + ": " + value;
        }
    }

    /**
     * Parses a string into a valid HTTP header object.
     *
     * @param string The string to be parsed into a header. Must not be null.
     * @return The parsed HTTP header. Never null.
     * @throws HeaderFormatException if the string is not a valid header format.
     */
    default @NotNull HttpHeader<?> parse(@NotNull String string) throws HeaderFormatException {
        @NotNull String[] split = string.split("\\s*:\\s*", 2);

        if (!string.contains(":")) {
            throw new HeaderFormatException("header missing separator between key and value");
        } else if (string.contains("\n") || string.contains("\r")) {
            throw new HeaderFormatException("header contains illegal characters");
        } else if (split.length != 2) {
            throw new HeaderFormatException("header with blank value");
        } else if (!split[0].matches(HttpHeaderKey.NAME_FORMAT_REGEX.pattern())) {
            throw new HeaderFormatException("illegal header key '" + split[0] + "'");
        } else {
            @NotNull HttpHeaderKey<?> key = HttpHeaderKey.retrieve(split[0]);
            @NotNull String value = split[1].trim();

            return key.read(getVersion(), value);
        }
    }

    /**
     * Validates if a string is a well-formed HTTP header that can be parsed without issues.
     *
     * @param string The string to be validated. Must not be null.
     * @return True if the string is a valid HTTP header, false otherwise.
     */
    @SuppressWarnings("RedundantIfStatement")
    default boolean validate(@NotNull String string) {
        @NotNull String[] split = string.split("\\s*:\\s*", 2);

        if (!string.contains(":")) {
            return false;
        } else if (string.contains("\n") || string.contains("\r")) {
            return false;
        } else if (split.length != 2) {
            return false;
        } else if (!split[0].matches(HttpHeaderKey.NAME_FORMAT_REGEX.pattern())) {
            return false;
        }

        return true;
    }

}
