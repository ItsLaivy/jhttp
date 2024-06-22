package codes.laivy.jhttp.media;

import codes.laivy.jhttp.media.json.JsonMediaParser;
import codes.laivy.jhttp.media.text.TextMediaParser;
import codes.laivy.jhttp.pseudo.provided.PseudoCharset;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a media type and its associated parser. Each media type is defined by a type, optional parameters,
 * and a parser that can handle the content of that type.
 *
 * @param <T> the type of the content that this media type handles
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public final class MediaType<T> {

    // Static initializers

    public static @NotNull MediaType<JsonElement> APPLICATION_JSON = create(new Type("application", "json"), JsonMediaParser.create());

    /**
     * Creates a new media type with the specified type, parser, and parameters.
     *
     * @param type       the type of the media
     * @param parser     the parser to handle this media type
     * @param parameters the parameters associated with this media type
     * @param <T>        the type of the content that this media type handles
     * @return the newly created media type
     */
    public static <T> @NotNull MediaType<T> create(
            @NotNull Type type,
            @NotNull MediaParser<T> parser,
            @NotNull Parameter @NotNull ... parameters
    ) {
        return new MediaType<>(type, parser, parameters);
    }

    /**
     * Creates a new media type with the specified type and parameters, using a default text parser.
     *
     * @param type       the type of the media
     * @param parameters the parameters associated with this media type
     * @return the newly created media type
     */
    public static @NotNull MediaType<?> create(@NotNull Type type, @NotNull Parameter @NotNull ... parameters) {
        return new MediaType<>(type, TextMediaParser.create(), parameters);
    }

    // Object

    private final @NotNull Type type;
    private final @NotNull Parameter @NotNull [] parameters;
    private final @NotNull MediaParser<T> parser;

    private MediaType(
            @NotNull Type type,
            @NotNull MediaParser<T> parser,
            @NotNull Parameter @NotNull [] parameters
    ) {
        this.type = type;
        this.parser = parser;

        this.parameters = parameters;
    }

    // Getters

    /**
     * Returns the type of this media type.
     *
     * @return the type of this media type
     */
    public @NotNull Type getType() {
        return type;
    }

    /**
     * Returns the parser associated with this media type.
     *
     * @return the parser associated with this media type
     */
    public @NotNull MediaParser<T> getParser() {
        return parser;
    }

    /**
     * Returns the parameters associated with this media type.
     *
     * @return an array of parameters associated with this media type
     */
    public @NotNull Parameter @NotNull [] getParameters() {
        return parameters;
    }

    /**
     * Returns the parameter with the specified key, if present.
     *
     * @param key the key of the parameter to retrieve
     * @return an optional containing the parameter if present, or an empty optional if not
     */
    public @NotNull Optional<Parameter> getParameter(@NotNull String key) {
        return Arrays.stream(parameters).filter(p -> p.getKey().equalsIgnoreCase(key)).findFirst();
    }

    /**
     * Returns the charset parameter of this media type, if present.
     *
     * @return the charset parameter if present, or null if not
     */
    public @Nullable PseudoCharset getCharset() {
        @Nullable Parameter parameter = getParameter("charset").orElse(null);
        return parameter != null ? PseudoCharset.create(parameter.getValue()) : null;
    }

    /**
     * Returns the boundary parameter of this media type, if present.
     *
     * @return the boundary parameter if present, or null if not
     */
    public @Nullable Boundary getBoundary() {
        @Nullable Parameter parameter = getParameter("boundary").orElse(null);

        if (parameter != null) {
            return new Boundary(parameter.getValue());
        } else {
            return null;
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @NotNull MediaType<?> that = (MediaType<?>) o;
        return Objects.equals(getType(), that.getType()) && Arrays.equals(getParameters(), that.getParameters());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getType(), Arrays.hashCode(getParameters()));
    }
    @Override
    public @NotNull String toString() {
        return Parser.serialize(this);
    }

    // Classes

    /**
     * Utility class for parsing and serializing media types.
     *
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    public static final class Parser {
        private Parser() {
            throw new UnsupportedOperationException();
        }

        // Serializers

        @ApiStatus.Internal
        private static final @NotNull Pattern PARSE_PATTERN = Pattern.compile("([^;\\s]+)(?:;\\s*charset=([^;\\s]+))?(?:;\\s*(.*))?");

        /**
         * Serializes the given media type to a string.
         *
         * @param media the media type to serialize
         * @return the string representation of the media type
         */
        public static @NotNull String serialize(@NotNull MediaType<?> media) {
            @NotNull StringBuilder builder = new StringBuilder();
            builder.append(media.getType());

            for (@NotNull Parameter parameter : media.getParameters()) {
                builder.append("; ").append(parameter);
            }

            return builder.toString();
        }

        /**
         * Deserializes the given string into a media type.
         *
         * @param string the string to deserialize
         * @return the deserialized media type
         * @throws ParseException if an error occurs during deserialization
         */
        public static @NotNull MediaType<?> deserialize(@NotNull String string) throws ParseException {
            @NotNull Pattern pattern = Pattern.compile("([\\w-]+\\s*=\\s*[^;]+)|(^[^;]+)");
            @NotNull Matcher matcher = pattern.matcher(string);

            @Nullable Type type = null;
            @NotNull List<Parameter> parameters = new LinkedList<>();

            try {
                while (matcher.find()) {
                    if (matcher.group(2) != null) {
                        type = Type.parse(matcher.group(2));
                    } else {
                        @NotNull String[] split = matcher.group(1).split("\\s*=\\s*");
                        parameters.add(new Parameter(split[0], split[1]));
                    }
                }
            } catch (@NotNull Throwable throwable) {
                throw new ParseException("cannot parse media type '" + string + "': " + throwable.getMessage(), -1);
            }

            if (type == null) {
                throw new ParseException("cannot obtain name in media type text '" + string + "'", -1);
            }

            return create(type, parameters.toArray(new Parameter[0]));
        }

        /**
         * Validates whether the given string is a valid media type.
         *
         * @param string the string to validate
         * @return true if the string is a valid media type, false otherwise
         */
        public static boolean validate(@NotNull String string) {
            try {
                deserialize(string);
                return true;
            } catch (@NotNull Throwable throwable) {
                return false;
            }
        }

    }

    /**
     * Represents the type of media type, consisting of a type and an optional subtype.
     *
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    public static final class Type implements CharSequence {

        // Static initializers

        /**
         * Parses a string into a media type {@link Type}.
         *
         * @param string the string to parse
         * @return the parsed media type {@link Type}
         */
        public static @NotNull Type parse(@NotNull String string) {
            @NotNull String[] split = string.split("/", 2);

            if (split.length > 1) {
                return new Type(split[0], split[1]);
            } else {
                return new Type(split[0], null);
            }
        }

        // Object

        private final @NotNull String type;
        private final @Nullable String subtype;

        public Type(@NotNull String type, @Nullable String subtype) {
            this.type = type;
            this.subtype = subtype;

            if ((type.contains(";") || type.contains(",")) || (subtype != null && (subtype.contains(";") || subtype.contains(",")))) {
                throw new IllegalArgumentException("type or subtype with illegal characters");
            }
        }

        // Getters

        /**
         * Returns the type of this media type {@link Type}.
         *
         * @return the type of this media type {@link Type}
         */
        public @NotNull String getType() {
            return type;
        }

        /**
         * Returns the subtype of this media type {@link Type}.
         *
         * @return the subtype of this media type {@link Type}, or null if none
         */
        public @Nullable String getSubType() {
            return subtype;
        }

        /**
         * Determines whether this media type {@link Type} is multipart.
         *
         * @return true if this media type {@link Type} is multipart, false otherwise
         */
        public boolean isMultipart() {
            return getType().equalsIgnoreCase("multipart");
        }

        // Implementations

        @Override
        public int length() {
            return toString().length();
        }
        @Override
        public char charAt(int index) {
            return toString().charAt(index);
        }

        @Override
        public @NotNull CharSequence subSequence(int start, int end) {
            return toString().subSequence(start, end);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Type type = (Type) o;
            return this.type.equalsIgnoreCase(type.type) && (subtype != null && type.subtype != null && subtype.equalsIgnoreCase(type.subtype));
        }
        @Override
        public int hashCode() {
            return Objects.hash(type.toLowerCase(), (subtype != null ? subtype.toLowerCase() : null));
        }
        @Override
        public @NotNull String toString() {
            return type + (subtype != null ? "/" + subtype : "");
        }

    }

    /**
     * Represents a parameter of a media type, consisting of a key-value pair.
     *
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    public static final class Parameter {

        private final @NotNull String key;
        private final @NotNull String value;

        public Parameter(@NotNull String key, @NotNull String value) {
            this.key = key;
            this.value = value;

            if (key.contains(";") || key.contains(",") || value.contains(";") || value.contains(",")) {
                throw new IllegalArgumentException("content type parameter key or value with illegal characters");
            }
        }

        // Getters

        /**
         * Returns the key of this parameter.
         *
         * @return the key of this parameter
         */
        public @NotNull String getKey() {
            return key;
        }

        /**
         * Returns the value of this parameter.
         *
         * @return the value of this parameter
         */
        public @NotNull String getValue() {
            return value;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            @NotNull Parameter parameter = (Parameter) o;
            return Objects.equals(getKey(), parameter.getKey()) && Objects.equals(getValue(), parameter.getValue());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getKey(), getValue());
        }

        @Override
        public @NotNull String toString() {
            return getKey() + "=" + getValue();
        }

    }

    /**
     * Represents a boundary parameter for multipart media types.
     *
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    public static final class Boundary implements CharSequence {

        private final @NotNull String name;

        public Boundary(@NotNull String name) {
            this.name = name;
        }

        /**
         * Returns the name of this boundary.
         *
         * @return the name of this boundary
         */
        public @NotNull String getName() {
            return name;
        }

        // Implementations

        @Override
        public int length() {
            return name.length();
        }
        @Override
        public char charAt(int index) {
            return name.charAt(index);
        }

        @Override
        public @NotNull CharSequence subSequence(int start, int end) {
            return name.subSequence(start, end);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Boundary boundary = (Boundary) o;
            return Objects.equals(name, boundary.name);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public @NotNull String toString() {
            return name;
        }
    }

}
